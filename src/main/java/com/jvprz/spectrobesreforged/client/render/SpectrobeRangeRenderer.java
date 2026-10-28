package com.jvprz.spectrobesreforged.client.render;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.spectrobe.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.config.ClientConfig;
import com.jvprz.spectrobesreforged.common.network.SyncedGameData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import com.jvprz.spectrobesreforged.client.feature.scanner.FossilScan;
import com.jvprz.spectrobesreforged.common.content.block.FossilOreBlock;
import com.jvprz.spectrobesreforged.common.content.block.MineralOreBlock;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.jvprz.spectrobesreforged.common.registry.ModSounds;
import net.minecraft.sounds.SoundSource;

@EventBusSubscriber(modid = SpectrobesReforged.MODID, value = Dist.CLIENT)
public final class SpectrobeRangeRenderer {

    private static final int SEGMENTS = 64;

    private static final float OPEN_DURATION_TICKS = 6.0f;
    private static final float WAVE_INTERVAL_TICKS = 6.0f;
    private static final float WAVE_LIFETIME_TICKS = 14.0f;
    private static final float WAVE_RISE_HEIGHT = 1.25f;

    private static final int PARTICLE_SPAWN_EVERY_N_TICKS = 4;
    private static final int BASE_RING_LAYERS = 6;
    private static final double BASE_RING_THICKNESS = 0.24D;

    private static final int FOSSIL_SCAN_DEPTH = 64;
    private static final int FOSSIL_PARTICLES_PER_SPAWN = 6;
    private static final int MAX_FOSSIL_HINT_POSITIONS = 12;

    private static final float SWEEP_SPEED = 0.115f;
    private static final float SWEEP_WIDTH = 0.16f;
    private static final int SWEEP_FADE_STEPS = 7;
    private static final float SWEEP_FADE_STEP_ANGLE = 0.09f;
    private static final double SWEEP_LINE_Y = 0.04D;

    private static final int FOSSIL_TRIGGER_COOLDOWN_TICKS = 8;
    private static final int ACTIVE_HINT_HOLD_TICKS = 20;

    private static final Vector3f FOSSIL_HINT_COLOR = new Vector3f(1.0f, 0.55f, 0.1f);
    private static final Vector3f MINERAL_HINT_COLOR = new Vector3f(0.45f, 0.85f, 1.0f);

    private static final Map<UUID, Integer> RANGE_START_TICK = new HashMap<>();
    private static final Map<UUID, Integer> LAST_PARTICLE_TICK = new HashMap<>();
    private static final Map<UUID, FossilScan> SCANS = new HashMap<>();
    private static ClientLevel activeLevel;
    private static List<SpectrobeEntity> nearbySpectrobes = List.of();
    private static final Map<UUID, List<BlockPos>> DETECTED_FOSSILS = new HashMap<>();
    private static final Map<UUID, Map<BlockPos, ScanTargetType>> DETECTED_TARGET_TYPES = new HashMap<>();
    private static final Map<UUID, Map<BlockPos, Integer>> LAST_FOSSIL_TRIGGER_TICK = new HashMap<>();
    private static final Map<UUID, Map<BlockPos, Integer>> ACTIVE_FOSSIL_HINT_TICKS = new HashMap<>();
    private static final Map<UUID, Integer> LAST_ACTIVE_HINT_TICK = new HashMap<>();

    private SpectrobeRangeRenderer() {}

    private enum ScanTargetType {
        FOSSIL,
        MINERAL
    }

    @SubscribeEvent
    public static void renderRange(RenderLevelStageEvent event) {
        if (!ClientConfig.scanVisuals() || event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.lines());

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);

        for (SpectrobeEntity spectrobe : nearbySpectrobes) {
            UUID uuid = spectrobe.getUUID();
            if (spectrobe.isRemoved() || !spectrobe.shouldRenderRangeCircle()) continue;
            int range = SyncedGameData.range(spectrobe.getSpeciesKey());
            Integer startTick = RANGE_START_TICK.get(uuid);
            if (range <= 0 || startTick == null) continue;
            float time = (spectrobe.tickCount - startTick) + partialTick;

            double maxRadius = range * 2.0D;

            double x = Mth.lerp(partialTick, spectrobe.xOld, spectrobe.getX());
            double y = Mth.lerp(partialTick, spectrobe.yOld, spectrobe.getY()) + 0.05D;
            double z = Mth.lerp(partialTick, spectrobe.zOld, spectrobe.getZ());


            float openProgress = Math.min(time / OPEN_DURATION_TICKS, 1.0f);
            float openEased = easeOutBack(openProgress);

            double baseRadius = maxRadius * openEased;
            int baseAlpha = (int) (255.0f * Math.min(openProgress * 1.15f, 1.0f));

            drawThickCircle(
                    poseStack, lineConsumer,
                    x, y, z,
                    baseRadius,
                    BASE_RING_THICKNESS,
                    BASE_RING_LAYERS,
                    255, 255, 255, baseAlpha
            );

            if (openProgress >= 1.0f) {
                drawThickCircle(
                        poseStack, lineConsumer,
                        x, y, z,
                        maxRadius,
                        BASE_RING_THICKNESS,
                        BASE_RING_LAYERS,
                        255, 255, 255, 190
                );

                float postOpenTime = time - OPEN_DURATION_TICKS;
                int waveCount = (int) Math.ceil((postOpenTime / WAVE_INTERVAL_TICKS)) + 1;

                int firstWave = Math.max(0,
                        (int) Math.ceil((postOpenTime - WAVE_LIFETIME_TICKS) / WAVE_INTERVAL_TICKS));
                for (int waveIndex = firstWave; waveIndex < waveCount; waveIndex++) {
                    float waveAge = postOpenTime - (waveIndex * WAVE_INTERVAL_TICKS);

                    if (waveAge < 0.0f || waveAge > WAVE_LIFETIME_TICKS) {
                        continue;
                    }

                    float waveProgress = waveAge / WAVE_LIFETIME_TICKS;
                    float waveEased = easeOutCubic(waveProgress);

                    double waveY = y + (WAVE_RISE_HEIGHT * waveEased);
                    int waveAlpha = (int) (150.0f * (1.0f - waveProgress));

                    drawCircle(
                            poseStack, lineConsumer,
                            x, waveY, z,
                            maxRadius,
                            255, 255, 255, waveAlpha
                    );
                }

                float sweepAngle = positiveAngle(time * SWEEP_SPEED);

                drawSweepLineWithFade(
                        poseStack,
                        lineConsumer,
                        x,
                        y + SWEEP_LINE_Y,
                        z,
                        maxRadius,
                        sweepAngle
                );

            }
        }

        poseStack.popPose();
        bufferSource.endBatch(RenderType.lines());
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != activeLevel || mc.player == null) {
            clearState();
            activeLevel = mc.level;
        }
        if (mc.level == null || mc.player == null || mc.isPaused()) return;

        nearbySpectrobes = mc.level.getEntitiesOfClass(SpectrobeEntity.class,
                mc.player.getBoundingBox().inflate(64.0));
        Set<UUID> active = nearbySpectrobes.stream()
                .filter(e -> e.shouldRenderRangeCircle() && !e.isRemoved())
                .filter(e -> SyncedGameData.range(e.getSpeciesKey()) > 0)
                .map(SpectrobeEntity::getUUID).collect(Collectors.toSet());
        cleanupOldEntries(active);

        for (SpectrobeEntity spectrobe : nearbySpectrobes) {
            UUID uuid = spectrobe.getUUID();
            if (!active.contains(uuid)) {
                spectrobe.setClientScanHasTarget(false);
                continue;
            }
            int start = RANGE_START_TICK.computeIfAbsent(uuid, id -> {
                mc.level.playLocalSound(spectrobe.getX(), spectrobe.getY(), spectrobe.getZ(),
                        ModSounds.SCANNER_OPEN.get(), SoundSource.PLAYERS, 0.25F, 0.55F, false);
                return spectrobe.tickCount;
            });
            tickActiveHints(spectrobe);
            int age = spectrobe.tickCount - start;
            if (age < OPEN_DURATION_TICKS) {
                spectrobe.setClientScanHasTarget(false);
                continue;
            }
            double radius = SyncedGameData.range(spectrobe.getSpeciesKey()) * 2.0;
            if(ClientConfig.scanVisuals())spawnAmbientParticles(mc.level, spectrobe, spectrobe.getX(),
                    spectrobe.getY() + 0.05, spectrobe.getZ(), radius);
            updateDetectedFossils(mc.level, spectrobe, radius);
            spectrobe.setClientScanHasTarget(hasDetectedFossils(spectrobe));
            detectFossilsOnSweep(mc.level, spectrobe, positiveAngle(age * SWEEP_SPEED));
            if(ClientConfig.scanVisuals())renderActiveFossilHints(mc.level, spectrobe);
        }
    }

    private static void clearState() {
        nearbySpectrobes.forEach(e -> e.setClientScanHasTarget(false));
        nearbySpectrobes = List.of();
        RANGE_START_TICK.clear();
        LAST_PARTICLE_TICK.clear();
        SCANS.clear();
        DETECTED_FOSSILS.clear();
        DETECTED_TARGET_TYPES.clear();
        LAST_FOSSIL_TRIGGER_TICK.clear();
        ACTIVE_FOSSIL_HINT_TICKS.clear();
        LAST_ACTIVE_HINT_TICK.clear();
    }

    private static void cleanupOldEntries(Set<UUID> active) {
        RANGE_START_TICK.keySet().retainAll(active);
        LAST_PARTICLE_TICK.keySet().retainAll(active);
        SCANS.keySet().retainAll(active);
        DETECTED_FOSSILS.keySet().retainAll(active);
        DETECTED_TARGET_TYPES.keySet().retainAll(active);
        LAST_FOSSIL_TRIGGER_TICK.keySet().retainAll(active);
        ACTIVE_FOSSIL_HINT_TICKS.keySet().retainAll(active);
        LAST_ACTIVE_HINT_TICK.keySet().retainAll(active);
    }

    private static void tickActiveHints(SpectrobeEntity spectrobe) {
        UUID uuid = spectrobe.getUUID();
        int tick = spectrobe.tickCount;

        Integer lastTick = LAST_ACTIVE_HINT_TICK.get(uuid);
        if (lastTick != null && lastTick == tick) {
            return;
        }
        LAST_ACTIVE_HINT_TICK.put(uuid, tick);

        Map<BlockPos, Integer> active = ACTIVE_FOSSIL_HINT_TICKS.get(uuid);
        if (active == null || active.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<BlockPos, Integer>> it = active.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Integer> entry = it.next();
            int ticks = entry.getValue() - 1;
            if (ticks <= 0) {
                it.remove();
            } else {
                entry.setValue(ticks);
            }
        }
    }

    private static void updateDetectedFossils(ClientLevel level, SpectrobeEntity spectrobe, double radius) {
        UUID uuid = spectrobe.getUUID();
        FossilScan scan = SCANS.computeIfAbsent(uuid, id -> new FossilScan());
        if (!scan.tick(level, spectrobe.blockPosition(), radius, FOSSIL_SCAN_DEPTH,
                MAX_FOSSIL_HINT_POSITIONS)) return;
        List<BlockPos> found = scan.results();
        Map<BlockPos, ScanTargetType> types = new HashMap<>();
        for (BlockPos pos : found) {
            ScanTargetType type = getTrackedTargetType(level.getBlockState(pos));
            if (type != null) types.put(pos, type);
        }
        DETECTED_FOSSILS.put(uuid, new ArrayList<>(types.keySet()));
        DETECTED_TARGET_TYPES.put(uuid, types);
        Map<BlockPos, Integer> cooldowns = LAST_FOSSIL_TRIGGER_TICK.get(uuid);
        if (cooldowns != null) cooldowns.entrySet().removeIf(
                entry -> spectrobe.tickCount - entry.getValue() >= FOSSIL_TRIGGER_COOLDOWN_TICKS);
    }

    private static boolean hasDetectedFossils(SpectrobeEntity spectrobe) {
        List<BlockPos> fossils = DETECTED_FOSSILS.get(spectrobe.getUUID());
        return fossils != null && !fossils.isEmpty();
    }

    private static ScanTargetType getTrackedTargetType(BlockState state) {
        if (state.getBlock() instanceof FossilOreBlock) return ScanTargetType.FOSSIL;
        if (state.getBlock() instanceof MineralOreBlock) return ScanTargetType.MINERAL;
        return null;
    }

    private static void detectFossilsOnSweep(ClientLevel level, SpectrobeEntity spectrobe, float sweepAngle) {
        List<BlockPos> fossils = DETECTED_FOSSILS.get(spectrobe.getUUID());
        if (fossils == null || fossils.isEmpty()) {
            return;
        }

        UUID uuid = spectrobe.getUUID();
        int gameTick = spectrobe.tickCount;

        Map<BlockPos, Integer> perFossilCooldown =
                LAST_FOSSIL_TRIGGER_TICK.computeIfAbsent(uuid, id -> new HashMap<>());
        Map<BlockPos, Integer> activeHints =
                ACTIVE_FOSSIL_HINT_TICKS.computeIfAbsent(uuid, id -> new HashMap<>());

        double sx = spectrobe.getX();
        double sz = spectrobe.getZ();

        for (BlockPos fossilPos : fossils) {
            BlockPos surfacePos = findParticleSpawnPos(level, fossilPos, spectrobe.blockPosition().getY());
            if (surfacePos == null) {
                continue;
            }

            double dx = (surfacePos.getX() + 0.5D) - sx;
            double dz = (surfacePos.getZ() + 0.5D) - sz;

            if ((dx * dx + dz * dz) < 0.0001D) {
                continue;
            }

            float fossilAngle = positiveAngle((float) Math.atan2(dz, dx));
            float diff = Math.abs(angleDifference(sweepAngle, fossilAngle));

            if (diff > SWEEP_WIDTH) {
                continue;
            }

            Integer lastTrigger = perFossilCooldown.get(surfacePos);
            if (lastTrigger != null && gameTick - lastTrigger < FOSSIL_TRIGGER_COOLDOWN_TICKS) {
                continue;
            }

            perFossilCooldown.put(surfacePos.immutable(), gameTick);
            activeHints.put(surfacePos.immutable(), ACTIVE_HINT_HOLD_TICKS);

            ScanTargetType type = null;
            Map<BlockPos, ScanTargetType> detectedTypes = DETECTED_TARGET_TYPES.get(uuid);

            if (detectedTypes != null) {
                for (Map.Entry<BlockPos, ScanTargetType> detectedEntry : detectedTypes.entrySet()) {
                    BlockPos detectedPos = detectedEntry.getKey();

                    if (detectedPos.getX() == surfacePos.getX()
                            && detectedPos.getZ() == surfacePos.getZ()) {
                        type = detectedEntry.getValue();
                        break;
                    }
                }
            }

            float pitch = type == ScanTargetType.MINERAL
                    ? 1.18F + (level.random.nextFloat() * 0.08F)
                    : 0.95F + (level.random.nextFloat() * 0.10F);

            level.playLocalSound(
                    surfacePos.getX() + 0.5D,
                    surfacePos.getY() + 0.5D,
                    surfacePos.getZ() + 0.5D,
                    ModSounds.SCANNER_FOUND.get(),
                    SoundSource.PLAYERS,
                    0.75F,
                    pitch,
                    false
            );
        }
    }

    private static void renderActiveFossilHints(ClientLevel level, SpectrobeEntity spectrobe) {
        Map<BlockPos, Integer> activeHints = ACTIVE_FOSSIL_HINT_TICKS.get(spectrobe.getUUID());
        if (activeHints == null || activeHints.isEmpty()) {
            return;
        }

        Map<BlockPos, ScanTargetType> detectedTypes = DETECTED_TARGET_TYPES.get(spectrobe.getUUID());

        for (Map.Entry<BlockPos, Integer> entry : activeHints.entrySet()) {
            BlockPos surfacePos = entry.getKey();
            int ticksLeft = entry.getValue();

            ScanTargetType type = null;

            if (detectedTypes != null) {
                for (Map.Entry<BlockPos, ScanTargetType> detectedEntry : detectedTypes.entrySet()) {
                    BlockPos detectedPos = detectedEntry.getKey();
                    if (detectedPos.getX() == surfacePos.getX() && detectedPos.getZ() == surfacePos.getZ()) {
                        type = detectedEntry.getValue();
                        break;
                    }
                }
            }

            Vector3f color = type == ScanTargetType.MINERAL ? MINERAL_HINT_COLOR : FOSSIL_HINT_COLOR;

            float life = ticksLeft / (float) ACTIVE_HINT_HOLD_TICKS;
            float lifeEased = life * life;
            float scale = 0.75f + (lifeEased * 0.55f);

            for (int p = 0; p < FOSSIL_PARTICLES_PER_SPAWN; p++) {
                double px = surfacePos.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.14D;
                double py = surfacePos.getY() + 0.10D + level.random.nextDouble() * 0.35D;
                double pz = surfacePos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.14D;

                double vx = (level.random.nextDouble() - 0.5D) * 0.004D;
                double vy = 0.10D + level.random.nextDouble() * 0.05D;
                double vz = (level.random.nextDouble() - 0.5D) * 0.004D;

                level.addParticle(
                        new DustParticleOptions(color, scale),
                        px, py, pz,
                        vx, vy, vz
                );
            }
        }
    }

    private static BlockPos findParticleSpawnPos(ClientLevel level, BlockPos fossilPos, int spectrobeY) {
        int minY = fossilPos.getY() + 1;
        int maxY = Math.max(minY, spectrobeY + 2);

        for (int y = minY; y <= maxY; y++) {
            BlockPos pos = new BlockPos(fossilPos.getX(), y, fossilPos.getZ());
            BlockPos below = pos.below();

            if (level.getBlockState(pos).isAir() && !level.getBlockState(below).isAir()) {
                return pos;
            }
        }

        return null;
    }

    private static void spawnAmbientParticles(
            ClientLevel level,
            SpectrobeEntity spectrobe,
            double x,
            double y,
            double z,
            double radius
    ) {
        int gameTick = spectrobe.tickCount;
        UUID uuid = spectrobe.getUUID();

        Integer lastTick = LAST_PARTICLE_TICK.get(uuid);
        if (lastTick != null && gameTick - lastTick < PARTICLE_SPAWN_EVERY_N_TICKS) {
            return;
        }

        LAST_PARTICLE_TICK.put(uuid, gameTick);

        for (int i = 0; i < 2; i++) {
            double angle = level.random.nextDouble() * (Math.PI * 2.0);
            double px = x + Math.cos(angle) * radius;
            double pz = z + Math.sin(angle) * radius;
            double py = y + 0.02D + (level.random.nextDouble() * 0.08D);

            level.addParticle(
                    new DustParticleOptions(new Vector3f(1.0f, 1.0f, 1.0f), 0.8f),
                    px, py, pz,
                    0.0D, 0.01D, 0.0D
            );
        }
    }

    private static void drawSweepLineWithFade(
            PoseStack poseStack,
            VertexConsumer consumer,
            double cx,
            double cy,
            double cz,
            double radius,
            float sweepAngle
    ) {
        for (int i = SWEEP_FADE_STEPS; i >= 0; i--) {
            float ang = positiveAngle(sweepAngle - (i * SWEEP_FADE_STEP_ANGLE));
            float t = 1.0f - (i / (float) (SWEEP_FADE_STEPS + 1));

            int alpha = (int) Mth.lerp(t, 18.0f, 230.0f);

            drawSweepLine(
                    poseStack,
                    consumer,
                    cx, cy, cz,
                    radius,
                    ang,
                    255, 255, 255, alpha
            );
        }
    }

    private static void drawSweepLine(
            PoseStack poseStack,
            VertexConsumer consumer,
            double cx,
            double cy,
            double cz,
            double radius,
            float angle,
            int r,
            int g,
            int b,
            int a
    ) {
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        float x1 = (float) cx;
        float y1 = (float) cy;
        float z1 = (float) cz;

        float x2 = (float) (cx + Math.cos(angle) * radius);
        float y2 = (float) cy;
        float z2 = (float) (cz + Math.sin(angle) * radius);

        putLineVertex(consumer, pose, normal, x1, y1, z1, r, g, b, a);
        putLineVertex(consumer, pose, normal, x2, y2, z2, r, g, b, a);
    }

    private static void drawThickCircle(
            PoseStack poseStack,
            VertexConsumer consumer,
            double cx,
            double cy,
            double cz,
            double radius,
            double thickness,
            int layers,
            int r,
            int g,
            int b,
            int a
    ) {
        if (radius <= 0.01D || thickness <= 0.0D || layers <= 0 || a <= 0) {
            return;
        }

        if (layers == 1) {
            drawCircle(poseStack, consumer, cx, cy, cz, radius, r, g, b, a);
            return;
        }

        double start = radius - (thickness / 2.0D);
        double step = thickness / (layers - 1);

        for (int i = 0; i < layers; i++) {
            double layerRadius = start + (step * i);
            if (layerRadius > 0.01D) {
                drawCircle(poseStack, consumer, cx, cy, cz, layerRadius, r, g, b, a);
            }
        }
    }

    private static void drawCircle(
            PoseStack poseStack,
            VertexConsumer consumer,
            double cx,
            double cy,
            double cz,
            double radius,
            int r,
            int g,
            int b,
            int a
    ) {
        if (radius <= 0.01D || a <= 0) {
            return;
        }

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        for (int i = 0; i < SEGMENTS; i++) {
            double a1 = (Math.PI * 2.0D * i) / SEGMENTS;
            double a2 = (Math.PI * 2.0D * (i + 1)) / SEGMENTS;

            float x1 = (float) (cx + Math.cos(a1) * radius);
            float y1 = (float) cy;
            float z1 = (float) (cz + Math.sin(a1) * radius);

            float x2 = (float) (cx + Math.cos(a2) * radius);
            float y2 = (float) cy;
            float z2 = (float) (cz + Math.sin(a2) * radius);

            putLineVertex(consumer, pose, normal, x1, y1, z1, r, g, b, a);
            putLineVertex(consumer, pose, normal, x2, y2, z2, r, g, b, a);
        }
    }

    private static void putLineVertex(
            VertexConsumer consumer,
            Matrix4f pose,
            Matrix3f normal,
            float x,
            float y,
            float z,
            int r,
            int g,
            int b,
            int a
    ) {
        consumer.addVertex(pose, x, y, z)
                .setColor(r, g, b, a)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    private static float angleDifference(float a, float b) {
        float diff = a - b;
        while (diff > Math.PI) diff -= (float) (Math.PI * 2.0);
        while (diff < -Math.PI) diff += (float) (Math.PI * 2.0);
        return diff;
    }

    private static float positiveAngle(float angle) {
        float twoPi = (float) (Math.PI * 2.0);
        angle %= twoPi;
        if (angle < 0.0f) {
            angle += twoPi;
        }
        return angle;
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return 1.0f + c3 * (float) Math.pow(t - 1.0f, 3) + c1 * (float) Math.pow(t - 1.0f, 2);
    }

    private static float easeOutCubic(float t) {
        return 1.0f - (float) Math.pow(1.0f - t, 3.0f);
    }
}