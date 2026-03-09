package com.jvprz.spectrobesreforged.client.render;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeSpecies;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeSpeciesRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    private static final int FOSSIL_SCAN_INTERVAL_TICKS = 10;
    private static final int FOSSIL_SCAN_DEPTH = 64;
    private static final int FOSSIL_PARTICLES_PER_SPAWN = 6;
    private static final int MAX_FOSSIL_HINT_POSITIONS = 12;

    private static final Map<UUID, Integer> RANGE_START_TICK = new HashMap<>();
    private static final Map<UUID, Integer> LAST_PARTICLE_TICK = new HashMap<>();
    private static final Map<UUID, Integer> LAST_FOSSIL_SCAN_TICK = new HashMap<>();
    private static final Map<UUID, List<BlockPos>> DETECTED_FOSSILS = new HashMap<>();

    private SpectrobeRangeRenderer() {}

    @SubscribeEvent
    public static void renderRange(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
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

        cleanupOldEntries(mc);

        for (SpectrobeEntity spectrobe : mc.level.getEntitiesOfClass(
                SpectrobeEntity.class,
                mc.player.getBoundingBox().inflate(64.0)
        )) {
            UUID uuid = spectrobe.getUUID();

            if (!spectrobe.shouldRenderRangeCircle()) {
                RANGE_START_TICK.remove(uuid);
                LAST_PARTICLE_TICK.remove(uuid);
                LAST_FOSSIL_SCAN_TICK.remove(uuid);
                DETECTED_FOSSILS.remove(uuid);
                continue;
            }

            SpectrobeSpecies species = SpectrobeSpeciesRegistry.getByKey(spectrobe.getSpeciesKey());
            if (species == null) {
                continue;
            }

            int startTick = RANGE_START_TICK.computeIfAbsent(uuid, id -> spectrobe.tickCount);
            float time = (spectrobe.tickCount - startTick) + partialTick;

            double maxRadius = species.range() * 2;

            double x = Mth.lerp(partialTick, spectrobe.xOld, spectrobe.getX());
            double y = Mth.lerp(partialTick, spectrobe.yOld, spectrobe.getY()) + 0.05D;
            double z = Mth.lerp(partialTick, spectrobe.zOld, spectrobe.getZ());

            float openProgress = Math.min(time / OPEN_DURATION_TICKS, 1.0f);

            float openEased = easeOutBack(openProgress);
            double baseRadius = maxRadius * openEased;
            int baseAlpha = (int) (255.0f * Math.min(openProgress * 1.15f, 1.0f));

            drawThickCircle(
                    poseStack,
                    lineConsumer,
                    x,
                    y,
                    z,
                    baseRadius,
                    BASE_RING_THICKNESS,
                    BASE_RING_LAYERS,
                    255,
                    255,
                    255,
                    baseAlpha
            );

            if (openProgress >= 1.0f) {
                drawThickCircle(
                        poseStack,
                        lineConsumer,
                        x,
                        y,
                        z,
                        maxRadius,
                        BASE_RING_THICKNESS,
                        BASE_RING_LAYERS,
                        255,
                        255,
                        255,
                        190
                );

                float postOpenTime = time - OPEN_DURATION_TICKS;
                int waveCount = (int) Math.ceil((postOpenTime / WAVE_INTERVAL_TICKS)) + 1;

                for (int waveIndex = 0; waveIndex < waveCount; waveIndex++) {
                    float waveAge = postOpenTime - (waveIndex * WAVE_INTERVAL_TICKS);

                    if (waveAge < 0.0f || waveAge > WAVE_LIFETIME_TICKS) {
                        continue;
                    }

                    float waveProgress = waveAge / WAVE_LIFETIME_TICKS;
                    float waveEased = easeOutCubic(waveProgress);

                    double waveY = y + (WAVE_RISE_HEIGHT * waveEased);
                    int waveAlpha = (int) (150.0f * (1.0f - waveProgress));

                    drawCircle(
                            poseStack,
                            lineConsumer,
                            x,
                            waveY,
                            z,
                            maxRadius,
                            255,
                            255,
                            255,
                            waveAlpha
                    );
                }

                spawnAmbientParticles(mc.level, spectrobe, x, y, z, maxRadius);
                updateDetectedFossils(mc.level, spectrobe, maxRadius);
                spawnFossilHintParticles(mc.level, spectrobe);
            }
        }

        poseStack.popPose();
        bufferSource.endBatch(RenderType.lines());
    }

    private static void cleanupOldEntries(Minecraft mc) {
        Iterator<Map.Entry<UUID, Integer>> it = RANGE_START_TICK.entrySet().iterator();

        while (it.hasNext()) {
            UUID uuid = it.next().getKey();

            boolean stillValid = mc.level.getEntitiesOfClass(
                    SpectrobeEntity.class,
                    mc.player.getBoundingBox().inflate(128.0)
            ).stream().anyMatch(e -> e.getUUID().equals(uuid) && e.shouldRenderRangeCircle());

            if (!stillValid) {
                it.remove();
                LAST_PARTICLE_TICK.remove(uuid);
                LAST_FOSSIL_SCAN_TICK.remove(uuid);
                DETECTED_FOSSILS.remove(uuid);
            }
        }
    }

    private static void updateDetectedFossils(ClientLevel level, SpectrobeEntity spectrobe, double radius) {
        UUID uuid = spectrobe.getUUID();
        int gameTick = spectrobe.tickCount;

        Integer lastScan = LAST_FOSSIL_SCAN_TICK.get(uuid);
        if (lastScan != null && gameTick - lastScan < FOSSIL_SCAN_INTERVAL_TICKS) {
            return;
        }
        LAST_FOSSIL_SCAN_TICK.put(uuid, gameTick);

        BlockPos center = spectrobe.blockPosition();
        int radiusCeil = Mth.ceil(radius);

        LinkedHashSet<BlockPos> foundSet = new LinkedHashSet<>();

        for (int dx = -radiusCeil; dx <= radiusCeil; dx++) {
            for (int dz = -radiusCeil; dz <= radiusCeil; dz++) {
                double distSq = (dx * dx) + (dz * dz);
                if (distSq > radius * radius) {
                    continue;
                }

                for (int dy = 0; dy <= FOSSIL_SCAN_DEPTH; dy++) {
                    BlockPos pos = center.offset(dx, -dy, dz);
                    BlockState state = level.getBlockState(pos);

                    if (isTrackedFossil(state)) {
                        foundSet.add(pos.immutable());

                        if (foundSet.size() >= MAX_FOSSIL_HINT_POSITIONS) {
                            break;
                        }
                    }
                }

                if (foundSet.size() >= MAX_FOSSIL_HINT_POSITIONS) {
                    break;
                }
            }

            if (foundSet.size() >= MAX_FOSSIL_HINT_POSITIONS) {
                break;
            }
        }

        DETECTED_FOSSILS.put(uuid, new ArrayList<>(foundSet));
    }

    private static boolean isTrackedFossil(BlockState state) {
        String id = String.valueOf(BuiltInRegistries.BLOCK.getKey(state.getBlock()));
        return id.equals("spectrobesreforged:corona_fossil_ore")
                || id.equals("spectrobesreforged:aurora_fossil_ore")
                || id.equals("spectrobesreforged:flash_fossil_ore")
                || id.equals("spectrobesreforged:deepslate_corona_fossil_ore")
                || id.equals("spectrobesreforged:deepslate_aurora_fossil_ore")
                || id.equals("spectrobesreforged:deepslate_flash_fossil_ore");
    }

    private static void spawnFossilHintParticles(ClientLevel level, SpectrobeEntity spectrobe) {
        List<BlockPos> fossils = DETECTED_FOSSILS.get(spectrobe.getUUID());
        if (fossils == null || fossils.isEmpty()) {
            return;
        }

        int gameTick = spectrobe.tickCount;
        if ((gameTick & 1) != 0) {
            return;
        }

        int count = Math.min(fossils.size(), 4);

        for (int i = 0; i < count; i++) {
            BlockPos fossilPos = fossils.get(level.random.nextInt(fossils.size()));
            BlockPos surfacePos = findParticleSpawnPos(level, fossilPos, spectrobe.blockPosition().getY());

            if (surfacePos == null) {
                continue;
            }

            for (int p = 0; p < FOSSIL_PARTICLES_PER_SPAWN; p++) {
                double px = surfacePos.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.18D;
                double py = surfacePos.getY() + 0.12D + level.random.nextDouble() * 0.22D;
                double pz = surfacePos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 0.18D;

                double vx = (level.random.nextDouble() - 0.5D) * 0.008D;
                double vy = 0.12D + level.random.nextDouble() * 0.08D;
                double vz = (level.random.nextDouble() - 0.5D) * 0.008D;

                level.addParticle(
                        new DustParticleOptions(new Vector3f(1.0f, 0.55f, 0.1f), 1.15f),
                        px,
                        py,
                        pz,
                        vx,
                        vy,
                        vz
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
                    px,
                    py,
                    pz,
                    0.0D,
                    0.01D,
                    0.0D
            );
        }
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

    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return 1.0f + c3 * (float) Math.pow(t - 1.0f, 3) + c1 * (float) Math.pow(t - 1.0f, 2);
    }

    private static float easeOutCubic(float t) {
        return 1.0f - (float) Math.pow(1.0f - t, 3.0f);
    }
}