package com.jvprz.spectrobesreforged.common.content.block.entity;

import com.jvprz.spectrobesreforged.common.content.block.IncubatorBlock;
import com.jvprz.spectrobesreforged.common.feature.incubator.menu.IncubatorMenu;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.feature.prizmod.PrizmodSync;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeSpecies;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.registry.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import com.jvprz.spectrobesreforged.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
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

    private final NonNullList<ItemStack> items =
            NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

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
                     * Cuando el proceso ya ha terminado y está esperando
                     * al jugador, dejamos de escuchar sonidos.
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
                            VibrationSystem.getGameEventFrequency(gameEvent);

                    if (vanillaFrequency <= 0) {
                        return false;
                    }

                    Vec3 incubatorCenter =
                            Vec3.atCenterOf(
                                    IncubatorBlockEntity.this.worldPosition
                            );

                    double distance =
                            emitterPosition.distanceTo(incubatorCenter);

                    if (distance > SOUND_LISTENER_RADIUS) {
                        return false;
                    }

                    int baseFrequency =
                            (int) Math.ceil(
                                    vanillaFrequency * 16.0 / 15.0
                            );

                    double distanceFactor =
                            1.0 - distance / SOUND_LISTENER_RADIUS;

                    distanceFactor =
                            Math.max(
                                    0.0,
                                    Math.min(1.0, distanceFactor)
                            );

                    distanceFactor = Math.sqrt(distanceFactor);

                    int finalFrequency =
                            (int) Math.round(
                                    baseFrequency * distanceFactor
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

    private final ContainerData data = new ContainerData() {

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 ->
                        IncubatorBlockEntity.this.awakening ? 1 : 0;

                case 1 ->
                        IncubatorBlockEntity.this.awakeningProgress;

                case 2 ->
                        IncubatorBlockEntity.this.capturedFrequency;

                case 3 ->
                        IncubatorBlockEntity.this.selectedColor;

                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 ->
                        IncubatorBlockEntity.this.awakening =
                                value != 0;

                case 1 ->
                        IncubatorBlockEntity.this.awakeningProgress =
                                value;

                case 2 ->
                        IncubatorBlockEntity.this.capturedFrequency =
                                value;

                case 3 ->
                        IncubatorBlockEntity.this.selectedColor =
                                value;
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
            this.dynamicGameEventListener.add(serverLevel);
        }
    }

    @Override
    public void setRemoved() {
        if (this.level instanceof ServerLevel serverLevel) {
            this.dynamicGameEventListener.remove(serverLevel);
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

        if (!incubator.awakening) {
            return;
        }

        if (!state.hasProperty(IncubatorBlock.POWERED)
                || !state.getValue(IncubatorBlock.POWERED)) {
            return;
        }

        /*
         * Si ya alcanzó el final, intentamos entregar el resultado.
         *
         * Si el jugador está desconectado, awakening se mantiene en true,
         * la barra permanece completa y se vuelve a intentar cada tick.
         */
        if (incubator.awakeningProgress >= MAX_PROGRESS) {
            incubator.tryDeliverAwakenedSpectrobe(level);
            return;
        }

        incubator.awakeningProgress++;

        incubator.updateActiveJukebox(level);
        incubator.updateImpulse();

        int jukeboxFrequency =
                incubator.calculateJukeboxFrequency(level);

        int currentSoundFrequency =
                Math.max(
                        jukeboxFrequency,
                        incubator.impulseFrequency
                );

        incubator.setCapturedFrequency(
                currentSoundFrequency
        );

        if (incubator.awakeningProgress >= MAX_PROGRESS) {
            incubator.awakeningProgress = MAX_PROGRESS;

            /*
             * El color queda fijado usando el nivel exacto
             * del último tick del proceso.
             */
            incubator.updateSelectedColor();

            incubator.tryDeliverAwakenedSpectrobe(level);
        }

        incubator.setChanged();
    }

    /**
     * Intenta crear el Spectrobe y añadirlo al Prizmod.
     *
     * Si el jugador está desconectado, devuelve false y mantiene
     * el proceso esperando en la incubadora.
     */
    private boolean tryDeliverAwakenedSpectrobe(Level level) {
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
                        .getPlayer(this.awakeningPlayerUuid);

        /*
         * No consumimos el fósil mientras el jugador no esté conectado.
         */
        if (player == null) {
            return false;
        }

        ItemStack fossilStack =
                this.getItem(FOSSIL_SLOT);

        String speciesKey =
                this.resolveSpeciesKey(fossilStack);

        if (speciesKey == null) {
            this.abortInvalidAwakening(player);
            return false;
        }

        SpectrobeSpecies species =
                SpectrobeSpeciesRegistry.getByKey(speciesKey);

        if (species == null) {
            this.abortInvalidAwakening(player);
            return false;
        }

        int hp =
                species.stats().hp().base();

        int attack =
                species.stats().attack().base();

        int defense =
                species.stats().defense().base();

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
         * Un Spectrobe Child recién despertado va primero
         * al baby slot. Si ya está ocupado, se envía a la caja.
         */
        if (prizmodData.getBabySlot().isEmpty()) {
            prizmodData.setBabySlot(newEntry);
        } else {
            prizmodData.addToBox(newEntry);
        }

        PrizmodSync.sync(player);

        /*
         * El fósil se consume únicamente después de crear
         * y entregar correctamente el Spectrobe.
         */
        this.items.set(FOSSIL_SLOT, ItemStack.EMPTY);

        player.displayClientMessage(
                Component.translatable(
                        "message.spectrobesreforged.incubator.awakened",
                        Component.literal(species.key()),
                        this.selectedColor
                ),
                false
        );

        this.finishAwakening();
        return true;
    }

    /**
     * Obtiene la especie a partir del identificador del fósil.
     *
     * spectrobesreforged:inkana_fossil -> inkana
     */
    @Nullable
    private String resolveSpeciesKey(ItemStack fossilStack) {
        if (fossilStack.isEmpty()) {
            return null;
        }

        ResourceLocation itemId =
                BuiltInRegistries.ITEM.getKey(
                        fossilStack.getItem()
                );

        String path = itemId.getPath();

        String suffix = "_fossil";

        if (!path.endsWith(suffix)) {
            return null;
        }

        String speciesKey =
                path.substring(
                        0,
                        path.length() - suffix.length()
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
         * No se consume el fósil si falta su definición.
         */
        this.awakening = false;
        this.awakeningProgress = 0;
        this.capturedFrequency = 0;
        this.selectedColor = 0;
        this.awakeningPlayerUuid = null;

        this.resetTemporarySoundState();
        this.setChanged();
    }

    private void finishAwakening() {
        this.awakening = false;
        this.awakeningProgress = 0;
        this.capturedFrequency = 0;
        this.selectedColor = 0;
        this.awakeningPlayerUuid = null;

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

    private void receiveSoundImpulse(int frequency) {
        int clamped =
                Math.max(
                        1,
                        Math.min(MAX_FREQUENCY, frequency)
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

    private void updateActiveJukebox(Level level) {
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
                this.findNearestPlayingJukebox(level);
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
                    > JUKEBOX_RADIUS * JUKEBOX_RADIUS) {
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

            nearestDistance = distanceSquared;
            nearest = candidate.immutable();
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
                > JUKEBOX_RADIUS * JUKEBOX_RADIUS) {
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
                this.getActiveJukebox(level);

        if (jukebox == null) {
            return 0;
        }

        long songTicks =
                jukebox.getSongPlayer()
                        .getTicksSinceSongStarted();

        double fastWave =
                Math.sin(songTicks * 0.42);

        double mediumWave =
                Math.sin(songTicks * 0.19 + 1.7);

        double slowWave =
                Math.sin(songTicks * 0.075 + 0.4);

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
                        Math.min(1.0, mixedWave)
                );

        /*
         * Un tocadiscos cercano suele permanecer
         * entre el 75 % y el 100 %.
         */
        double musicalLevel =
                0.75 + mixedWave * 0.25;

        double distance =
                Math.sqrt(
                        jukebox.getBlockPos()
                                .distSqr(this.worldPosition)
                );

        double normalizedDistance =
                Math.min(
                        1.0,
                        distance / JUKEBOX_RADIUS
                );

        double distanceFactor =
                1.0 - normalizedDistance * 0.70;

        int frequency =
                (int) Math.round(
                        MAX_FREQUENCY
                                * musicalLevel
                                * distanceFactor
                );

        return Math.max(
                1,
                Math.min(MAX_FREQUENCY, frequency)
        );
    }

    private void setCapturedFrequency(
            int frequency
    ) {
        int clamped =
                Math.max(
                        0,
                        Math.min(MAX_FREQUENCY, frequency)
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

    public boolean startAwakening(Player player) {
        if (this.awakening) {
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
    public ItemStack getItem(int slot) {
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
            stack = stack.copyWithCount(1);
        }

        this.items.set(slot, stack);
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
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
        super.saveAdditional(tag, registries);

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
    }

    @Override
    protected void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.loadAdditional(tag, registries);

        this.items.clear();

        ContainerHelper.loadAllItems(
                tag,
                this.items,
                registries
        );

        if (tag.hasUUID("AwakeningPlayer")) {
            this.awakeningPlayerUuid =
                    tag.getUUID("AwakeningPlayer");
        } else {
            this.awakeningPlayerUuid = null;
        }

        this.awakening =
                tag.getBoolean("Awakening");

        this.awakeningProgress =
                tag.getInt("AwakeningProgress");

        this.capturedFrequency =
                tag.getInt("CapturedFrequency");

        this.selectedColor =
                tag.getInt("SelectedColor");

        this.resetTemporarySoundState();
    }
}