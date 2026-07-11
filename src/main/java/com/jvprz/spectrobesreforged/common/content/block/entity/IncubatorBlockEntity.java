package com.jvprz.spectrobesreforged.common.content.block.entity;

import com.jvprz.spectrobesreforged.common.content.block.IncubatorBlock;
import com.jvprz.spectrobesreforged.common.feature.incubator.menu.IncubatorMenu;
import com.jvprz.spectrobesreforged.common.feature.prizmod.PrizmodSync;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeSpecies;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.registry.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import com.jvprz.spectrobesreforged.common.registry.ModBlockEntities;
import com.jvprz.spectrobesreforged.common.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class IncubatorBlockEntity extends BlockEntity
        implements MenuProvider, Container {

    private static final int FOSSIL_SLOT = 0;
    private static final int INVENTORY_SIZE = 1;

    private static final int MAX_PROGRESS = 200;
    private static final int MAX_FREQUENCY = 16;

    private static final int SOUND_LISTENER_RADIUS = 16;
    private static final int JUKEBOX_RADIUS = 16;
    private static final int JUKEBOX_SEARCH_INTERVAL = 10;

    private static final int IMPULSE_HOLD_TICKS = 5;
    private static final int IMPULSE_DECAY_INTERVAL = 2;

    /*
     * Awakening particles are intentionally sparse so the incubator
     * does not become visually overloaded.
     */
    private static final int AWAKENING_PARTICLE_INTERVAL = 12;

    /*
     * The final transfer lasts 30 ticks, approximately 1.5 seconds.
     */
    private static final int DELIVERY_DURATION_TICKS = 30;

    private final NonNullList<ItemStack> items =
            NonNullList.withSize(
                    INVENTORY_SIZE,
                    ItemStack.EMPTY
            );

    @Nullable
    private UUID awakeningPlayerUuid;

    private boolean awakening = false;
    private int awakeningProgress = 0;
    private int capturedFrequency = 0;
    private int selectedColor = 0;

    private int impulseFrequency = 0;
    private int impulseHoldTicks = 0;
    private int impulseDecayTicker = 0;

    @Nullable
    private BlockPos activeJukeboxPos;

    private int jukeboxSearchCooldown = 0;

    /*
     * Final Spectrobe delivery animation state.
     */
    private boolean deliveringSpectrobe = false;
    private int deliveryTicks = 0;

    @Nullable
    private UUID deliveryPlayerUuid;

    private final GameEventListener gameEventListener =
            new GameEventListener() {

                private final PositionSource source =
                        new BlockPositionSource(
                                IncubatorBlockEntity.this.worldPosition
                        );

                @Override
                public PositionSource getListenerSource() {
                    return this.source;
                }

                @Override
                public int getListenerRadius() {
                    return SOUND_LISTENER_RADIUS;
                }

                @Override
                public boolean handleGameEvent(
                        ServerLevel level,
                        Holder<GameEvent> gameEvent,
                        GameEvent.Context context,
                        Vec3 emitterPosition
                ) {
                    if (!IncubatorBlockEntity.this.awakening) {
                        return false;
                    }

                    /*
                     * Stop listening for sounds once the process has
                     * completed and is waiting for delivery.
                     */
                    if (IncubatorBlockEntity.this.awakeningProgress
                            >= MAX_PROGRESS) {
                        return false;
                    }

                    BlockState state =
                            IncubatorBlockEntity.this.getBlockState();

                    if (!state.hasProperty(IncubatorBlock.POWERED)
                            || !state.getValue(IncubatorBlock.POWERED)) {
                        return false;
                    }

                    int vanillaFrequency =
                            VibrationSystem.getGameEventFrequency(
                                    gameEvent
                            );

                    if (vanillaFrequency <= 0) {
                        return false;
                    }

                    Vec3 incubatorCenter =
                            IncubatorBlockEntity.this
                                    .getIncubatorCenter();

                    double distance =
                            emitterPosition.distanceTo(
                                    incubatorCenter
                            );

                    if (distance > SOUND_LISTENER_RADIUS) {
                        return false;
                    }

                    int baseFrequency =
                            (int) Math.ceil(
                                    vanillaFrequency
                                            * 16.0
                                            / 15.0
                            );

                    double distanceFactor =
                            1.0
                                    - distance
                                    / SOUND_LISTENER_RADIUS;

                    distanceFactor =
                            Math.max(
                                    0.0,
                                    Math.min(
                                            1.0,
                                            distanceFactor
                                    )
                            );

                    distanceFactor =
                            Math.sqrt(distanceFactor);

                    int finalFrequency =
                            (int) Math.round(
                                    baseFrequency
                                            * distanceFactor
                            );

                    if (finalFrequency <= 0) {
                        finalFrequency = 1;
                    }

                    IncubatorBlockEntity.this.receiveSoundImpulse(
                            finalFrequency
                    );

                    return true;
                }
            };

    private final DynamicGameEventListener<GameEventListener>
            dynamicGameEventListener =
            new DynamicGameEventListener<>(
                    this.gameEventListener
            );

    private final ContainerData data =
            new ContainerData() {

                @Override
                public int get(int index) {
                    return switch (index) {
                        case 0 ->
                                IncubatorBlockEntity.this.awakening
                                        ? 1
                                        : 0;

                        case 1 ->
                                IncubatorBlockEntity.this
                                        .awakeningProgress;

                        case 2 ->
                                IncubatorBlockEntity.this
                                        .capturedFrequency;

                        case 3 ->
                                IncubatorBlockEntity.this
                                        .selectedColor;

                        default -> 0;
                    };
                }

                @Override
                public void set(
                        int index,
                        int value
                ) {
                    switch (index) {
                        case 0 ->
                                IncubatorBlockEntity.this.awakening =
                                        value != 0;

                        case 1 ->
                                IncubatorBlockEntity.this
                                        .awakeningProgress =
                                        value;

                        case 2 ->
                                IncubatorBlockEntity.this
                                        .capturedFrequency =
                                        value;

                        case 3 ->
                                IncubatorBlockEntity.this
                                        .selectedColor =
                                        value;

                        default -> {
                        }
                    }
                }

                @Override
                public int getCount() {
                    return 4;
                }
            };

    public IncubatorBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(
                ModBlockEntities.INCUBATOR.get(),
                pos,
                state
        );
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (this.level instanceof ServerLevel serverLevel) {
            this.dynamicGameEventListener.add(
                    serverLevel
            );
        }
    }

    @Override
    public void setRemoved() {
        if (this.level instanceof ServerLevel serverLevel) {
            this.dynamicGameEventListener.remove(
                    serverLevel
            );
        }

        super.setRemoved();
    }

    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            IncubatorBlockEntity incubator
    ) {
        if (level.isClientSide) {
            return;
        }

        /*
         * The final transfer animation continues independently from
         * the normal awakening process.
         */
        if (incubator.deliveringSpectrobe) {
            incubator.tickSpectrobeDelivery(level);
            return;
        }

        if (!incubator.awakening) {
            return;
        }

        if (!state.hasProperty(IncubatorBlock.POWERED)
                || !state.getValue(IncubatorBlock.POWERED)) {
            return;
        }

        /*
         * Once the progress reaches its end, repeatedly attempt to
         * deliver the result.
         *
         * If the player is offline, awakening remains active, the
         * progress remains full and delivery is retried every tick.
         */
        if (incubator.awakeningProgress >= MAX_PROGRESS) {
            incubator.tryDeliverAwakenedSpectrobe(level);
            return;
        }

        incubator.awakeningProgress++;

        incubator.updateActiveJukebox(level);
        incubator.updateImpulse();

        int jukeboxFrequency =
                incubator.calculateJukeboxFrequency(
                        level
                );

        int currentSoundFrequency =
                Math.max(
                        jukeboxFrequency,
                        incubator.impulseFrequency
                );

        incubator.setCapturedFrequency(
                currentSoundFrequency
        );

        if (level instanceof ServerLevel serverLevel
                && incubator.awakeningProgress
                % AWAKENING_PARTICLE_INTERVAL == 0) {

            incubator.spawnAwakeningParticles(
                    serverLevel
            );
        }

        if (incubator.awakeningProgress >= MAX_PROGRESS) {
            incubator.awakeningProgress = MAX_PROGRESS;

            /*
             * Lock the final color using the exact frequency captured
             * during the final awakening tick.
             */
            incubator.updateSelectedColor();

            incubator.tryDeliverAwakenedSpectrobe(
                    level
            );
        }

        incubator.setChanged();
    }

    /**
     * Attempts to create the Spectrobe and add it to the Prizmod.
     *
     * <p>If the player is offline, this method returns false and
     * leaves the completed process waiting inside the incubator.</p>
     */
    private boolean tryDeliverAwakenedSpectrobe(
            Level level
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (this.awakeningPlayerUuid == null) {
            this.abortInvalidAwakening(null);
            return false;
        }

        ServerPlayer player =
                serverLevel.getServer()
                        .getPlayerList()
                        .getPlayer(
                                this.awakeningPlayerUuid
                        );

        /*
         * Do not consume the fossil while the player is offline.
         */
        if (player == null) {
            return false;
        }

        ItemStack fossilStack =
                this.getItem(FOSSIL_SLOT);

        String speciesKey =
                this.resolveSpeciesKey(
                        fossilStack
                );

        if (speciesKey == null) {
            this.abortInvalidAwakening(player);
            return false;
        }

        SpectrobeSpecies species =
                SpectrobeSpeciesRegistry.getByKey(
                        speciesKey
                );

        if (species == null) {
            this.abortInvalidAwakening(player);
            return false;
        }

        int hp =
                species.stats()
                        .hp()
                        .base();

        int attack =
                species.stats()
                        .attack()
                        .base();

        int defense =
                species.stats()
                        .defense()
                        .base();

        SpectrobeEntry newEntry =
                new SpectrobeEntry(
                        UUID.randomUUID(),
                        species.key(),
                        this.selectedColor,
                        species.initialStage().name(),
                        1,
                        hp,
                        hp,
                        attack,
                        defense,
                        0,
                        0,
                        0,
                        0
                );

        PrizmodData prizmodData =
                player.getData(
                        ModAttachments.PRIZMOD.get()
                );

        /*
         * A newly awakened Child Spectrobe is placed in the baby
         * slot first. If that slot is occupied, it is sent to storage.
         */
        if (prizmodData.getBabySlot().isEmpty()) {
            prizmodData.setBabySlot(newEntry);
        } else {
            prizmodData.addToBox(newEntry);
        }

        PrizmodSync.sync(player);

        /*
         * Consume the fossil only after the Spectrobe has been
         * successfully created and added to the player's Prizmod.
         */
        this.items.set(
                FOSSIL_SLOT,
                ItemStack.EMPTY
        );

        String displayName =
                species.key().substring(0, 1).toUpperCase()
                        + species.key().substring(1);

        player.displayClientMessage(
                Component.translatable(
                        "message.spectrobesreforged.incubator.awakened",
                        Component.literal(displayName),
                        this.selectedColor
                ),
                false
        );

        /*
         * Begin a moving digital transfer instead of drawing the
         * complete particle path in a single tick.
         */
        this.startSpectrobeDelivery(player);

        return true;
    }

    /**
     * Resolves the species key from the fossil item identifier.
     *
     * <p>Example:
     * spectrobesreforged:inkana_fossil becomes inkana.</p>
     */
    @Nullable
    private String resolveSpeciesKey(
            ItemStack fossilStack
    ) {
        if (fossilStack.isEmpty()) {
            return null;
        }

        ResourceLocation itemId =
                BuiltInRegistries.ITEM.getKey(
                        fossilStack.getItem()
                );

        String path =
                itemId.getPath();

        String suffix =
                "_fossil";

        if (!path.endsWith(suffix)) {
            return null;
        }

        String speciesKey =
                path.substring(
                        0,
                        path.length()
                                - suffix.length()
                );

        if (speciesKey.isBlank()) {
            return null;
        }

        return speciesKey;
    }

    private void abortInvalidAwakening(
            @Nullable ServerPlayer player
    ) {
        if (player != null) {
            player.displayClientMessage(
                    Component.translatable(
                            "message.spectrobesreforged.incubator.invalid_fossil"
                    ),
                    false
            );
        }

        /*
         * Do not consume the fossil if its species definition
         * cannot be found.
         */
        this.awakening = false;
        this.awakeningProgress = 0;
        this.capturedFrequency = 0;
        this.selectedColor = 0;
        this.awakeningPlayerUuid = null;

        this.deliveringSpectrobe = false;
        this.deliveryTicks = 0;
        this.deliveryPlayerUuid = null;

        this.resetTemporarySoundState();
        this.setChanged();
    }

    private void startSpectrobeDelivery(
            ServerPlayer player
    ) {
        this.awakening = false;
        this.awakeningProgress = MAX_PROGRESS;

        this.deliveringSpectrobe = true;
        this.deliveryTicks = 0;
        this.deliveryPlayerUuid = player.getUUID();

        this.resetTemporarySoundState();
        this.setChanged();
    }

    private void tickSpectrobeDelivery(
            Level level
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (this.deliveryPlayerUuid == null) {
            this.finishAwakening();
            return;
        }

        ServerPlayer player =
                serverLevel.getServer()
                        .getPlayerList()
                        .getPlayer(
                                this.deliveryPlayerUuid
                        );

        /*
         * The Spectrobe has already been delivered to the Prizmod.
         * If the player disconnects during the visual effect, safely
         * end the animation without duplicating the reward.
         */
        if (player == null) {
            this.finishAwakening();
            return;
        }

        Vec3 start =
                this.getIncubatorCenter();

        Vec3 end =
                player.position().add(
                        0.0,
                        player.getBbHeight() * 0.65,
                        0.0
                );

        double progress =
                Math.min(
                        1.0,
                        (double) this.deliveryTicks
                                / DELIVERY_DURATION_TICKS
                );

        /*
         * Smoothstep provides gentle acceleration and deceleration.
         */
        double easedProgress =
                progress
                        * progress
                        * (3.0 - 2.0 * progress);

        Vec3 direction =
                end.subtract(start);

        Vec3 currentPosition =
                start.add(
                        direction.scale(
                                easedProgress
                        )
                );

        /*
         * Add a small spiral movement so the effect resembles a
         * moving digital energy packet rather than a straight beam.
         *
         * The spiral radius starts at zero, grows near the middle
         * of the journey and returns to zero near the player.
         */
        Vec3 horizontalSide =
                new Vec3(
                        -direction.z,
                        0.0,
                        direction.x
                );

        if (horizontalSide.lengthSqr() > 0.0001) {
            horizontalSide =
                    horizontalSide.normalize();

            double spiralAngle =
                    this.deliveryTicks * 0.8;

            double spiralRadius =
                    Math.sin(progress * Math.PI)
                            * 0.18;

            currentPosition =
                    currentPosition.add(
                            horizontalSide.scale(
                                    Math.cos(spiralAngle)
                                            * spiralRadius
                            )
                    );

            currentPosition =
                    currentPosition.add(
                            0.0,
                            Math.sin(spiralAngle)
                                    * spiralRadius,
                            0.0
                    );
        }

        /*
         * Spawn one custom transfer particle at the current packet
         * position. The particle class handles the orb pulse and the
         * fragment sprite near the end of its lifetime.
         */
        serverLevel.sendParticles(
                ModParticles.SPECTROBE_TRANSFER.get(),
                currentPosition.x,
                currentPosition.y,
                currentPosition.z,
                1,
                0.0,
                0.0,
                0.0,
                0.0
        );

        /*
         * Display a compact launch burst on the first transfer tick.
         */
        if (this.deliveryTicks == 0) {
            serverLevel.sendParticles(
                    ModParticles.SPECTROBE_TRANSFER.get(),
                    start.x,
                    start.y,
                    start.z,
                    4,
                    0.14,
                    0.12,
                    0.14,
                    0.01
            );
        }

        this.deliveryTicks++;

        if (this.deliveryTicks > DELIVERY_DURATION_TICKS) {
            /*
             * Display a compact arrival burst around the player.
             */
            serverLevel.sendParticles(
                    ModParticles.SPECTROBE_TRANSFER.get(),
                    end.x,
                    end.y,
                    end.z,
                    6,
                    0.16,
                    0.25,
                    0.16,
                    0.015
            );

            this.finishAwakening();
            return;
        }

        this.setChanged();
    }

    private void finishAwakening() {
        this.awakening = false;
        this.awakeningProgress = 0;
        this.capturedFrequency = 0;
        this.selectedColor = 0;
        this.awakeningPlayerUuid = null;

        this.deliveringSpectrobe = false;
        this.deliveryTicks = 0;
        this.deliveryPlayerUuid = null;

        this.resetTemporarySoundState();
        this.setChanged();
    }

    private void resetTemporarySoundState() {
        this.impulseFrequency = 0;
        this.impulseHoldTicks = 0;
        this.impulseDecayTicker = 0;

        this.activeJukeboxPos = null;
        this.jukeboxSearchCooldown = 0;
    }

    private void receiveSoundImpulse(
            int frequency
    ) {
        int clamped =
                Math.max(
                        1,
                        Math.min(
                                MAX_FREQUENCY,
                                frequency
                        )
                );

        this.impulseFrequency =
                Math.max(
                        this.impulseFrequency,
                        clamped
                );

        this.impulseHoldTicks =
                IMPULSE_HOLD_TICKS;

        this.impulseDecayTicker = 0;
        this.setChanged();
    }

    private void updateImpulse() {
        if (this.impulseFrequency <= 0) {
            this.impulseFrequency = 0;
            return;
        }

        if (this.impulseHoldTicks > 0) {
            this.impulseHoldTicks--;
            return;
        }

        this.impulseDecayTicker++;

        if (this.impulseDecayTicker
                < IMPULSE_DECAY_INTERVAL) {
            return;
        }

        this.impulseDecayTicker = 0;
        this.impulseFrequency--;

        if (this.impulseFrequency < 0) {
            this.impulseFrequency = 0;
        }
    }

    private void updateActiveJukebox(
            Level level
    ) {
        if (this.jukeboxSearchCooldown > 0) {
            this.jukeboxSearchCooldown--;
            return;
        }

        this.jukeboxSearchCooldown =
                JUKEBOX_SEARCH_INTERVAL;

        if (this.getActiveJukebox(level) != null) {
            return;
        }

        this.activeJukeboxPos =
                this.findNearestPlayingJukebox(
                        level
                );
    }

    @Nullable
    private BlockPos findNearestPlayingJukebox(
            Level level
    ) {
        BlockPos nearest = null;

        double nearestDistance =
                Double.MAX_VALUE;

        BlockPos min =
                this.worldPosition.offset(
                        -JUKEBOX_RADIUS,
                        -JUKEBOX_RADIUS,
                        -JUKEBOX_RADIUS
                );

        BlockPos max =
                this.worldPosition.offset(
                        JUKEBOX_RADIUS,
                        JUKEBOX_RADIUS,
                        JUKEBOX_RADIUS
                );

        for (BlockPos candidate :
                BlockPos.betweenClosed(min, max)) {

            double distanceSquared =
                    candidate.distSqr(
                            this.worldPosition
                    );

            if (distanceSquared
                    > JUKEBOX_RADIUS
                    * JUKEBOX_RADIUS) {
                continue;
            }

            if (distanceSquared >= nearestDistance) {
                continue;
            }

            BlockEntity blockEntity =
                    level.getBlockEntity(candidate);

            if (!(blockEntity
                    instanceof JukeboxBlockEntity jukebox)) {
                continue;
            }

            if (!jukebox.getSongPlayer().isPlaying()) {
                continue;
            }

            nearestDistance =
                    distanceSquared;

            nearest =
                    candidate.immutable();
        }

        return nearest;
    }

    @Nullable
    private JukeboxBlockEntity getActiveJukebox(
            Level level
    ) {
        if (this.activeJukeboxPos == null) {
            return null;
        }

        double distanceSquared =
                this.activeJukeboxPos.distSqr(
                        this.worldPosition
                );

        if (distanceSquared
                > JUKEBOX_RADIUS
                * JUKEBOX_RADIUS) {
            this.activeJukeboxPos = null;
            return null;
        }

        BlockEntity blockEntity =
                level.getBlockEntity(
                        this.activeJukeboxPos
                );

        if (!(blockEntity
                instanceof JukeboxBlockEntity jukebox)) {
            this.activeJukeboxPos = null;
            return null;
        }

        if (!jukebox.getSongPlayer().isPlaying()) {
            this.activeJukeboxPos = null;
            return null;
        }

        return jukebox;
    }

    private int calculateJukeboxFrequency(
            Level level
    ) {
        JukeboxBlockEntity jukebox =
                this.getActiveJukebox(
                        level
                );

        if (jukebox == null) {
            return 0;
        }

        long songTicks =
                jukebox.getSongPlayer()
                        .getTicksSinceSongStarted();

        double fastWave =
                Math.sin(
                        songTicks * 0.42
                );

        double mediumWave =
                Math.sin(
                        songTicks * 0.19 + 1.7
                );

        double slowWave =
                Math.sin(
                        songTicks * 0.075 + 0.4
                );

        double mixedWave =
                (
                        fastWave * 0.45
                                + mediumWave * 0.35
                                + slowWave * 0.20
                                + 1.0
                ) / 2.0;

        mixedWave =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                mixedWave
                        )
                );

        /*
         * A nearby jukebox generally remains between 75% and
         * 100% of the maximum musical level.
         */
        double musicalLevel =
                0.75
                        + mixedWave
                        * 0.25;

        double distance =
                Math.sqrt(
                        jukebox.getBlockPos()
                                .distSqr(
                                        this.worldPosition
                                )
                );

        double normalizedDistance =
                Math.min(
                        1.0,
                        distance / JUKEBOX_RADIUS
                );

        double distanceFactor =
                1.0
                        - normalizedDistance
                        * 0.70;

        int frequency =
                (int) Math.round(
                        MAX_FREQUENCY
                                * musicalLevel
                                * distanceFactor
                );

        return Math.max(
                1,
                Math.min(
                        MAX_FREQUENCY,
                        frequency
                )
        );
    }

    /**
     * Returns the visual center of the complete two-block incubator.
     *
     * <p>The block entity exists in the FOOT part. The HEAD part is
     * placed to the left of the incubator's facing direction, so the
     * visual center is the midpoint between both block positions.</p>
     */
    private Vec3 getIncubatorCenter() {
        BlockState state =
                this.getBlockState();

        if (!state.hasProperty(IncubatorBlock.FACING)) {
            return Vec3.atCenterOf(
                    this.worldPosition
            ).add(
                    0.0,
                    0.65,
                    0.0
            );
        }

        Direction facing =
                state.getValue(
                        IncubatorBlock.FACING
                );

        Direction left =
                facing.getCounterClockWise();

        BlockPos headPos =
                this.worldPosition.relative(left);

        Vec3 footCenter =
                Vec3.atCenterOf(
                        this.worldPosition
                );

        Vec3 headCenter =
                Vec3.atCenterOf(
                        headPos
                );

        return footCenter.add(headCenter)
                .scale(0.5)
                .add(
                        0.0,
                        0.65,
                        0.0
                );
    }

    /**
     * Spawns sparse custom musical notes around the complete incubator
     * while the awakening process is running.
     *
     * <p>The number and spread increase slightly with the captured
     * sound frequency.</p>
     */
    private void spawnAwakeningParticles(
            ServerLevel level
    ) {
        /*
         * Do not show musical notes when no frequency is currently
         * being captured.
         */
        if (this.capturedFrequency <= 0) {
            return;
        }

        Vec3 center =
                this.getIncubatorCenter();

        int particleCount;

        if (this.capturedFrequency <= 5) {
            particleCount = 1;
        } else if (this.capturedFrequency <= 10) {
            particleCount =
                    level.random.nextBoolean()
                            ? 1
                            : 2;
        } else {
            particleCount = 2;
        }

        double horizontalRadius =
                0.65
                        + this.capturedFrequency
                        / 32.0;

        for (int i = 0; i < particleCount; i++) {
            double angle =
                    level.random.nextDouble()
                            * Math.PI
                            * 2.0;

            double radius =
                    horizontalRadius
                            * (
                            0.65
                                    + level.random.nextDouble()
                                    * 0.35
                    );

            double x =
                    center.x
                            + Math.cos(angle)
                            * radius;

            double y =
                    center.y
                            + level.random.nextDouble()
                            * 0.45;

            double z =
                    center.z
                            + Math.sin(angle)
                            * radius;

            /*
             * The particle provider randomly selects one of the two
             * musical-note sprites and gives it its upward movement.
             */
            level.sendParticles(
                    ModParticles.DIGITAL_NOTE.get(),
                    x,
                    y,
                    z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );
        }
    }


    private void setCapturedFrequency(
            int frequency
    ) {
        int clamped =
                Math.max(
                        0,
                        Math.min(
                                MAX_FREQUENCY,
                                frequency
                        )
                );

        if (this.capturedFrequency == clamped) {
            return;
        }

        this.capturedFrequency = clamped;
        this.updateSelectedColor();
        this.setChanged();
    }

    private void updateSelectedColor() {
        if (this.capturedFrequency <= 5) {
            this.selectedColor = 0;
        } else if (this.capturedFrequency <= 10) {
            this.selectedColor = 1;
        } else {
            this.selectedColor = 2;
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(
                "container.spectrobesreforged.incubator"
        );
    }

    public boolean isAwakening() {
        return this.awakening;
    }

    public int getAwakeningProgress() {
        return this.awakeningProgress;
    }

    public int getCapturedFrequency() {
        return this.capturedFrequency;
    }

    public int getSelectedColor() {
        return this.selectedColor;
    }

    @Nullable
    public UUID getAwakeningPlayerUuid() {
        return this.awakeningPlayerUuid;
    }

    public boolean startAwakening(
            Player player
    ) {
        if (this.awakening
                || this.deliveringSpectrobe) {
            return false;
        }

        ItemStack fossil =
                this.getItem(FOSSIL_SLOT);

        if (fossil.isEmpty()) {
            return false;
        }

        this.awakening = true;
        this.awakeningProgress = 0;
        this.capturedFrequency = 0;
        this.selectedColor = 0;
        this.awakeningPlayerUuid =
                player.getUUID();

        this.deliveringSpectrobe = false;
        this.deliveryTicks = 0;
        this.deliveryPlayerUuid = null;

        this.resetTemporarySoundState();
        this.setChanged();

        return true;
    }

    public void cancelAwakening() {
        this.awakening = false;
        this.awakeningProgress = 0;
        this.capturedFrequency = 0;
        this.selectedColor = 0;
        this.awakeningPlayerUuid = null;

        this.deliveringSpectrobe = false;
        this.deliveryTicks = 0;
        this.deliveryPlayerUuid = null;

        this.resetTemporarySoundState();
        this.setChanged();
    }

    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory inventory,
            Player player
    ) {
        return new IncubatorMenu(
                containerId,
                inventory,
                this,
                this.data
        );
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(
            int slot
    ) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(
            int slot,
            int amount
    ) {
        ItemStack removed =
                ContainerHelper.removeItem(
                        this.items,
                        slot,
                        amount
                );

        if (!removed.isEmpty()) {
            this.setChanged();
        }

        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(
            int slot
    ) {
        ItemStack removed =
                ContainerHelper.takeItem(
                        this.items,
                        slot
                );

        if (!removed.isEmpty()) {
            this.setChanged();
        }

        return removed;
    }

    @Override
    public void setItem(
            int slot,
            ItemStack stack
    ) {
        if (slot == FOSSIL_SLOT
                && stack.getCount() > 1) {
            stack =
                    stack.copyWithCount(1);
        }

        this.items.set(
                slot,
                stack
        );

        this.setChanged();
    }

    @Override
    public boolean stillValid(
            Player player
    ) {
        if (this.level == null) {
            return false;
        }

        if (this.level.getBlockEntity(
                this.worldPosition
        ) != this) {
            return false;
        }

        return player.distanceToSqr(
                this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.saveAdditional(
                tag,
                registries
        );

        ContainerHelper.saveAllItems(
                tag,
                this.items,
                registries
        );

        if (this.awakeningPlayerUuid != null) {
            tag.putUUID(
                    "AwakeningPlayer",
                    this.awakeningPlayerUuid
            );
        }

        tag.putBoolean(
                "Awakening",
                this.awakening
        );

        tag.putInt(
                "AwakeningProgress",
                this.awakeningProgress
        );

        tag.putInt(
                "CapturedFrequency",
                this.capturedFrequency
        );

        tag.putInt(
                "SelectedColor",
                this.selectedColor
        );

        tag.putBoolean(
                "DeliveringSpectrobe",
                this.deliveringSpectrobe
        );

        tag.putInt(
                "DeliveryTicks",
                this.deliveryTicks
        );

        if (this.deliveryPlayerUuid != null) {
            tag.putUUID(
                    "DeliveryPlayer",
                    this.deliveryPlayerUuid
            );
        }
    }

    @Override
    protected void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.loadAdditional(
                tag,
                registries
        );

        this.items.clear();

        ContainerHelper.loadAllItems(
                tag,
                this.items,
                registries
        );

        if (tag.hasUUID("AwakeningPlayer")) {
            this.awakeningPlayerUuid =
                    tag.getUUID(
                            "AwakeningPlayer"
                    );
        } else {
            this.awakeningPlayerUuid = null;
        }

        this.awakening =
                tag.getBoolean(
                        "Awakening"
                );

        this.awakeningProgress =
                tag.getInt(
                        "AwakeningProgress"
                );

        this.capturedFrequency =
                tag.getInt(
                        "CapturedFrequency"
                );

        this.selectedColor =
                tag.getInt(
                        "SelectedColor"
                );

        this.deliveringSpectrobe =
                tag.getBoolean(
                        "DeliveringSpectrobe"
                );

        this.deliveryTicks =
                tag.getInt(
                        "DeliveryTicks"
                );

        if (tag.hasUUID("DeliveryPlayer")) {
            this.deliveryPlayerUuid =
                    tag.getUUID(
                            "DeliveryPlayer"
                    );
        } else {
            this.deliveryPlayerUuid = null;
        }

        /*
         * Sound impulses and active jukebox references are temporary
         * runtime state and are recalculated after loading.
         */
        this.resetTemporarySoundState();
    }
}