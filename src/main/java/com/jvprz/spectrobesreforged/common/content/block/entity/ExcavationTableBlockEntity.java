package com.jvprz.spectrobesreforged.common.content.block.entity;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.content.item.*;
import com.jvprz.spectrobesreforged.common.feature.excavation.*;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import com.jvprz.spectrobesreforged.common.registry.ModBlockEntities;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.UUID;

public final class ExcavationTableBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int HEADERS = 19, DATA_COUNT = HEADERS + ExcavationPuzzle.CELLS;
    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private ExcavationPuzzle puzzle;
    private String specimenKey = "", resultPath = "";
    private UUID viewer;
    private int status, lastReward, feedback;
    private final ExcavationCoop coop=new ExcavationCoop();
    private final java.util.Set<UUID> contributors = new java.util.HashSet<>();
    private final ContainerData data = new ContainerData() {
        @Override public int get(int i) {
            ensurePuzzle();
            if (i >= HEADERS) return status != 1 || puzzle == null ? 0 : puzzle.cell(i - HEADERS);
            return switch (i) {
                case 0 -> status == 2 || puzzle == null ? 0 : puzzle.percent();
                case 1 -> puzzle == null ? 0 : puzzle.integrity();
                case 2 -> puzzle == null ? 0 : Math.min(32000, puzzle.elapsedTicks() / 20);
                case 3 -> !items.get(1).isEmpty() ? 3 : status;
                case 4 -> status != 1 || resultPath.isEmpty() ? 0 : BuiltInRegistries.ITEM.getId(result().getItem());
                case 5 -> puzzle == null ? 0 : puzzle.stress();
                case 6 -> puzzle == null ? 0 : puzzle.heat();
                case 7 -> puzzle == null ? 0 : puzzle.gas();
                case 8 -> puzzle == null ? 4 : puzzle.repairsLeft();
                case 9 -> puzzle == null ? 0 : puzzle.overburden();
                case 10 -> puzzle == null ? 0 : puzzle.terrain();
                case 11 -> progress() == null ? 1 : progress().level();
                case 12 -> progress() == null ? 0 : progress().progress();
                case 13 -> progress() != null && progress().knows(resultPath) ? 1 : 0;
                case 14 -> lastReward;
                case 15 -> puzzle != null && puzzle.automatic() ? 1 : 0;
                case 16 -> puzzle != null && puzzle.crowned() && resultPath.endsWith("_fossil") ? 1 : 0;
                case 17 -> feedback;
                case 18 -> puzzle == null ? 0 : puzzle.rock();
                default -> 0;
            };
        }
        @Override public void set(int i, int value) { }
        @Override public int getCount() { return DATA_COUNT; }
    };
    private ItemStack displayStack = ItemStack.EMPTY;
    /** Only the visible single item is synchronized to chunk watchers, never the excavation board. */
    private ItemStack visibleStack() {
        ItemStack source = items.get(1).isEmpty() ? items.get(0) : items.get(1);
        if (source.isEmpty()) return ItemStack.EMPTY;
        ItemStack copy = source.copyWithCount(1);
        copy.remove(DataComponents.CUSTOM_DATA);
        return copy;
    }
    public ItemStack displayStack() { return level != null && level.isClientSide ? displayStack : visibleStack(); }
    @Override public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            ItemStack visible = visibleStack();
            if (!ItemStack.matches(displayStack, visible)) {
                displayStack = visible;
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
            }
        }
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ItemStack visible = visibleStack();
        // Chunk-load synchronization must seed the comparison cache too. Otherwise
        // collecting a loaded item compares EMPTY to EMPTY and leaves a ghost on clients.
        displayStack = visible.copy();
        if (!visible.isEmpty()) tag.put("DisplayItem", visible.save(registries));
        return tag;
    }
    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        displayStack = tag.contains("DisplayItem") ? ItemStack.parseOptional(registries, tag.getCompound("DisplayItem")) : ItemStack.EMPTY;
    }
    @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
    @Override public void onDataPacket(net.minecraft.network.Connection connection, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        handleUpdateTag(packet.getTag(), registries);
    }
    public ExcavationTableBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.EXCAVATION_TABLE.get(), pos, state); }
    public ContainerData data() { return data; }
    private ExcavationProgress progress() {
        Player player = level == null || viewer == null ? null : level.getPlayerByUUID(viewer);
        return player == null ? null : player.getData(ModAttachments.EXCAVATION.get());
    }
    public boolean claim(Player player){
        if(level==null||!player.isAlive()||player.isSpectator()||!stillValid(player))return false;
        if(!coop.add(player.getUUID(),player.getGameProfile().getName()))return false;
        if(viewer==null)viewer=player.getUUID();return true;
    }
    public void release(Player player){
        flush();coop.leave(player.getUUID());
        if(player.getUUID().equals(viewer))viewer=coop.viewers().stream().findFirst().orElse(null);
    }
    public void cursor(ServerPlayer player,int x,int y,int tool,boolean using){
        if(level==null||!coop.contains(player.getUUID())||!stillValid(player))return;
        if(status!=1||!items.get(1).isEmpty()||(tool==ExcavationPuzzle.GIGA&&!player.getData(ModAttachments.EXCAVATION.get()).knows(resultPath)))using=false;
        coop.touch(player.getUUID(),x,y,tool,using,level.getGameTime());
    }
    public ContainerData dataFor(Player player){
        return new ContainerData(){
            public int get(int index){
                var skill=player.getData(ModAttachments.EXCAVATION.get());
                return switch(index){case 11->skill.level();case 12->skill.progress();case 13->skill.knows(resultPath)?1:0;default->data.get(index);};
            }
            public void set(int index,int value){}public int getCount(){return DATA_COUNT;}
        };
    }
    public static void serverTick(Level level,BlockPos pos,BlockState state,ExcavationTableBlockEntity table){
        for(var id:table.coop.viewers()){
            var player=level.getPlayerByUUID(id);
            if(player==null||!player.isAlive()||!(player.containerMenu instanceof ExcavationMenu menu)||menu.table()!=table||!table.stillValid(player)){
                table.coop.leave(id);if(id.equals(table.viewer))table.viewer=table.coop.viewers().stream().findFirst().orElse(null);
            }
        }
        if(table.coop.viewers().isEmpty()){table.flush();return;}
        table.ensurePuzzle();
        if(table.status==1&&table.puzzle!=null&&table.items.get(1).isEmpty()){table.puzzle.tick();if(level.getGameTime()%20==0)table.flush();}
        if(level.getGameTime()%4==0){
            var snapshot=table.coop.snapshot(level.getGameTime());
            for(var id:table.coop.viewers())if(level.getPlayerByUUID(id) instanceof ServerPlayer player&&player.containerMenu instanceof ExcavationMenu menu)
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,new com.jvprz.spectrobesreforged.common.network.S2CExcavationCoop(menu.containerId,snapshot));
        }
    }
    private void ensurePuzzle() {
        if (!items.get(1).isEmpty() || puzzle != null || level == null || level.isClientSide || !ExcavationSpecimen.accepts(items.getFirst())) return;
        var stack = items.getFirst();
        var tag = RawFossilItem.data(stack);
        boolean fossil = stack.getItem() instanceof RawFossilItem;
        specimenKey = tag.getString(fossil ? "Fossil" : "Mineral");
        var candidates = ExcavationSpecimen.candidates(stack);
        if (specimenKey.isEmpty()) specimenKey = candidates.get(level.random.nextInt(candidates.size()));
        // Both the result and element/grade must match this raw specimen, including edited item data.
        if (!candidates.contains(specimenKey)) { specimenKey = ""; return; }
        resultPath = specimenKey + (fossil ? "_fossil" : "");
        boolean[] mask = ExcavationMasks.get(specimenKey);
        if (mask == null || result().isEmpty()) { reset(); return; }
        try {
            if (tag.contains("Excavation")) {
                var saved = tag.getCompound("Excavation");
                puzzle = ExcavationPuzzle.restore(new ExcavationPuzzle.State(saved.getByteArray("Cells"), saved.getInt("Integrity"),
                        saved.getInt("Ticks"), saved.getBoolean("Damaged"), saved.getInt("Stress"), saved.getInt("Heat"),
                        saved.getInt("Gas"), saved.getInt("Repairs"), saved.getInt("Overburden"), saved.getInt("Terrain"),
                        saved.getBoolean("Automatic"), saved.getLong("Random"), saved.getInt("Strokes"), saved.getInt("Rock")));
            } else {
                int terrain = tag.getInt("ExcavationTerrain");
                puzzle = new ExcavationPuzzle(mask, level.random.nextLong(), terrain, ExcavationProfile.forSpecimen(specimenKey,terrain,tag.getInt("ExcavationRock")));
            }
        } catch (IllegalArgumentException invalid) { reset(); return; }
        var helpers = tag.getCompound("Excavation").getList("Contributors", net.minecraft.nbt.Tag.TAG_COMPOUND);
        for (int i=0;i<Math.min(helpers.size(),64);i++) if(helpers.getCompound(i).hasUUID("Id")) contributors.add(helpers.getCompound(i).getUUID("Id"));
        status = puzzle.integrity() > 0 ? 1 : 2;
        flush();
    }
    private ItemStack result() {
        if (resultPath.isEmpty()) return ItemStack.EMPTY;
        return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, resultPath)));
    }
    public void flush() {
        if (puzzle == null || items.getFirst().isEmpty()) return;
        var state = puzzle.state(); var saved = new CompoundTag();
        saved.putByteArray("Cells", state.cells()); saved.putInt("Integrity", state.integrity());
        saved.putInt("Ticks", state.ticks()); saved.putBoolean("Damaged", state.damaged());
        saved.putInt("Stress", state.stress()); saved.putInt("Heat", state.heat()); saved.putInt("Gas", state.gas());
        saved.putInt("Repairs", state.repairs()); saved.putInt("Overburden", state.overburden()); saved.putInt("Terrain", state.terrain());
        saved.putInt("Rock",state.rock());
        saved.putBoolean("Automatic", state.automatic()); saved.putLong("Random", state.randomState()); saved.putInt("Strokes", state.strokes());
        var helpers = new net.minecraft.nbt.ListTag();
        for (UUID id : contributors) { var helper = new CompoundTag(); helper.putUUID("Id", id); helpers.add(helper); }
        saved.put("Contributors", helpers);
        String key = items.getFirst().getItem() instanceof RawFossilItem ? "Fossil" : "Mineral";
        CustomData.update(DataComponents.CUSTOM_DATA, items.getFirst(), tag -> { tag.putString(key, specimenKey); tag.put("Excavation", saved); });
        setChanged();
    }
    public void work(ServerPlayer player, int x, int y, int tool) {
        if (!coop.contains(player.getUUID()) || !player.isAlive() || player.isSpectator() || !stillValid(player) || level == null) return;
        ensurePuzzle();
        if(!items.get(1).isEmpty()||status!=1||puzzle==null||x<0||x>=32||y<0||y>=32)return;
        var skill=player.getData(ModAttachments.EXCAVATION.get());
        if(tool==ExcavationPuzzle.GIGA&&!skill.knows(resultPath)){feedback=1;return;}
        if(!coop.stroke(player.getUUID(),tool,level.getGameTime()))return;
        coop.touch(player.getUUID(),x*5+2,y*5+2,tool,true,level.getGameTime());feedback=0;
        int before = puzzle.integrity(), beforeOpening=puzzle.overburden();
        if (!puzzle.work(x, y, tool, skill.level(), skill.knows(resultPath))) return;
        if (contributors.size()<64) contributors.add(player.getUUID());
        flush();
        if (puzzle.integrity() == 0) {
            items.set(0, ItemStack.EMPTY); status = 2; setChanged();
            level.playSound(null, worldPosition, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, .5F, .6F);
            player.displayClientMessage(Component.translatable("message.spectrobesreforged.excavation_broken"), true);
        } else if(beforeOpening>puzzle.overburden()) {
            var sound=puzzle.terrain()==ExcavationPuzzle.ICE?SoundEvents.GLASS_BREAK:puzzle.terrain()==ExcavationPuzzle.SAND?SoundEvents.SAND_BREAK:SoundEvents.STONE_BREAK;
            level.playSound(null,worldPosition,sound,SoundSource.BLOCKS,.45F,1.1F+(4-beforeOpening)*.12F);
        } else if (tool == ExcavationPuzzle.RECOVERY) {
            com.jvprz.spectrobesreforged.common.feature.progression.ModProgression.award(player,"excavation_repair");
            level.playSound(null, worldPosition, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, .35F, 1.5F);
        } else if (puzzle.integrity() < before) {
            level.playSound(null, worldPosition, SoundEvents.GLASS_HIT, SoundSource.BLOCKS, .5F, .6F);
        } else if (tool == ExcavationPuzzle.BLAST) {
            level.playSound(null, worldPosition, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, .35F, 1.2F);
        } else if (level.getGameTime() % 4 == 0) {
            boolean drill = tool == ExcavationPuzzle.DRILL || tool == ExcavationPuzzle.FINE || tool == ExcavationPuzzle.HEAVY || tool == ExcavationPuzzle.PITON || tool == ExcavationPuzzle.TAP;
            level.playSound(null, worldPosition, drill ? SoundEvents.STONE_HIT : SoundEvents.SAND_BREAK, SoundSource.BLOCKS, .15F, drill ? 1.5F : .8F);
        }
    }
    public boolean extract(Player player) {
        ensurePuzzle();
        if (!player.isAlive() || player.isSpectator() || !coop.contains(player.getUUID()) || !stillValid(player) || status != 1 || puzzle == null
                || !puzzle.canExtract() || !items.get(1).isEmpty() || items.getFirst().isEmpty()) return false;
        ItemStack reward = result(); if (reward.isEmpty()) return false;
        boolean crown = puzzle.crowned() && resultPath.endsWith("_fossil");
        CustomData.update(DataComponents.CUSTOM_DATA, reward, tag -> {
            tag.putBoolean("ExcavationCooperative", contributors.size()>=2 && !puzzle.automatic());
            tag.putBoolean("ExcavationCrown", crown); tag.putInt("ExcavationIntegrity", puzzle.integrity());
            tag.putInt("ExcavationSeconds", puzzle.elapsedTicks() / 20); tag.putBoolean("ExcavationAutomatic", puzzle.automatic());
        });
        // All rewards and catalogue updates occur only after extraction succeeds, never per click.
        items.set(0, ItemStack.EMPTY); items.set(1, reward); status = 3;
        lastReward = puzzle.experience();
        if (player instanceof ServerPlayer extractor) {
            var recipients = new java.util.LinkedHashMap<UUID, ServerPlayer>();
            recipients.put(extractor.getUUID(), extractor);
            for (UUID id : contributors) {
                var helper = extractor.server.getPlayerList().getPlayer(id);
                if (helper != null && helper.level() == level && stillValid(helper)) recipients.put(id, helper);
            }
            com.jvprz.spectrobesreforged.common.feature.progression.ModProgression.completedExcavation(
                    recipients.values(), resultPath, lastReward, crown, puzzle.terrain());
        }
        setChanged();
        if (level != null) level.playSound(null, worldPosition, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, .45F, 1.4F);
        player.displayClientMessage(Component.translatable(crown ? "message.spectrobesreforged.excavation_perfect" : "message.spectrobesreforged.excavation_done"), true);
        return true;
    }
    private void reset() { contributors.clear(); coop.releaseAll(); puzzle = null; specimenKey = ""; resultPath = ""; status = 0; lastReward = 0; feedback = 0; }
    @Override public Component getDisplayName() { return Component.translatable("block.spectrobesreforged.excavation_table"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new ExcavationMenu(id, inventory, this, dataFor(player)); }
    @Override public int getContainerSize() { return 2; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) {
        if (slot == 0) flush();
        var stack = ContainerHelper.removeItem(items, slot, amount);
        if ((slot == 0 && items.getFirst().isEmpty()) || (items.getFirst().isEmpty() && items.get(1).isEmpty())) reset();
        if (!stack.isEmpty()) setChanged(); return stack;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) { return removeItem(slot, 64); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); if (slot == 0 || (items.getFirst().isEmpty() && items.get(1).isEmpty())) reset(); setChanged(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot == 0 && ExcavationSpecimen.accepts(stack); }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getCenter()) <= 64; }
    @Override public void clearContent() { items.clear(); reset(); setChanged(); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        flush(); super.saveAdditional(tag, registries); ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("ExcavationOutcome", status == 2 || status == 3 ? status : 0);
        tag.putInt("ExcavationReward", lastReward);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries); ContainerHelper.loadAllItems(tag, items, registries); reset(); viewer = null; coop.clear();
        if (items.getFirst().isEmpty()) {
            int outcome = tag.getInt("ExcavationOutcome");
            if (outcome == 2 || outcome == 3) status = outcome;
            if (status == 3) lastReward = Math.max(0, tag.getInt("ExcavationReward"));
        }
    }
}
