package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.feature.progression.ModProgression;
import com.jvprz.spectrobesreforged.common.content.entity.spectrobe.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeSpecies;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.registry.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.UUID;

public class MineralItem extends Item {

    private final Integer health;
    private final Integer attack;
    private final Integer defense;

    private final boolean healthMax;
    private final boolean attackMax;
    private final boolean defenseMax;

    private final String description;

    public MineralItem(Properties properties,
                       Integer health, boolean healthMax,
                       Integer attack, boolean attackMax,
                       Integer defense, boolean defenseMax,
                       String description) {

        super(properties);
        this.health = health;
        this.attack = attack;
        this.defense = defense;

        this.healthMax = healthMax;
        this.attackMax = attackMax;
        this.defenseMax = defenseMax;

        this.description = description;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {

        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer sp) || !(player.level() instanceof ServerLevel)) {
            return InteractionResult.PASS;
        }

        if (!(target instanceof SpectrobeEntity spectrobe)) {
            return InteractionResult.PASS;
        }

        var owner = spectrobe.getOwner().orElse(null);
        if (owner == null || !owner.getUUID().equals(sp.getUUID())) {
            sp.displayClientMessage(
                    Component.translatable("message.spectrobesreforged.not_your_spectrobe")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResult.CONSUME;
        }

        if (!spectrobe.getPersistentData().hasUUID("SpectrobeId")) {
            sp.displayClientMessage(
                    Component.translatable("message.spectrobesreforged.not_linked")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResult.CONSUME;
        }

        UUID spectrobeId = spectrobe.getPersistentData().getUUID("SpectrobeId");

        feedTo(sp,spectrobeId,stack,spectrobe);
        return InteractionResult.CONSUME;
    }

    public boolean feedTo(ServerPlayer sp,UUID spectrobeId,ItemStack stack,SpectrobeEntity spectrobe) {
        if(stack.isEmpty()||stack.getItem()!=this)return false;
        PrizmodData data = sp.getData(ModAttachments.PRIZMOD.get());
        var found = data.findAnywhere(spectrobeId);
        if (found.isEmpty()) {
            sp.displayClientMessage(
                    Component.translatable("message.spectrobesreforged.data_not_found")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return false;
        }

        SpectrobeEntry entry = found.get();
        SpectrobeSpecies species = SpectrobeSpeciesRegistry.getByKey(entry.species());
        if (species == null) {
            sp.displayClientMessage(
                    Component.translatable("message.spectrobesreforged.unknown_species", entry.species())
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return false;
        }

        int xpGain=com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels.mineralXp(entry,com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels.mineralPercent(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this).getPath()));
        int oldHp = entry.hp();
        int oldAtk = entry.atk();
        int oldDef = entry.def();

        int baseHp = oldHp - entry.mineralHpBonus();
        int baseAtk = oldAtk - entry.mineralAtkBonus();
        int baseDef = oldDef - entry.mineralDefBonus();

        int newHp = applyStat(
                oldHp,
                com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels.boosted(species.stats().hp().base(),entry.excavationQuality()),
                com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels.boosted(species.stats().hp().max(),entry.excavationQuality()),
                health,
                healthMax
        );

        int newAtk = applyStat(
                oldAtk,
                com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels.boosted(species.stats().attack().base(),entry.excavationQuality()),
                com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels.boosted(species.stats().attack().max(),entry.excavationQuality()),
                attack,
                attackMax
        );

        int newDef = applyStat(
                oldDef,
                com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels.boosted(species.stats().defense().base(),entry.excavationQuality()),
                com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels.boosted(species.stats().defense().max(),entry.excavationQuality()),
                defense,
                defenseMax
        );

        int newHpBonus = newHp - baseHp;
        int newAtkBonus = newAtk - baseAtk;
        int newDefBonus = newDef - baseDef;

        boolean changed =
                newHp != oldHp ||
                        newAtk != oldAtk ||
                        newDef != oldDef;

        if (!changed && xpGain==0) {
            sp.displayClientMessage(
                    Component.translatable("message.spectrobesreforged.no_effect")
                            .withStyle(ChatFormatting.YELLOW),
                    true
            );
            return false;
        }

        int hpDelta = newHp - oldHp;
        int newHpCur = entry.isDead()?0:clamp(entry.hpCur() + hpDelta, 0, newHp);

        SpectrobeEntry updated = new SpectrobeEntry(
                entry.id(),
                entry.species(),
                entry.color(),
                entry.stage(),
                entry.level(),
                newHp,
                newHpCur,
                newAtk,
                newDef,
                entry.mineralsFed() + 1,
                newHpBonus,
                newAtkBonus,
                newDefBonus, entry.progress()
        );

        updated=com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels.award(updated,xpGain,species);
        data.replaceEntry(entry.id(), updated);
        ModProgression.award(sp, "feed");

        com.jvprz.spectrobesreforged.common.feature.training.SpectrobeTraining.syncActive(sp,updated);
        double fx=spectrobe==null?sp.getX():spectrobe.getX(),fy=spectrobe==null?sp.getY():spectrobe.getY(),fz=spectrobe==null?sp.getZ():spectrobe.getZ();
        if (sp.level() instanceof ServerLevel sl) {

            // sonido de comer
            float pitch = 1.15F + sl.random.nextFloat() * 0.2F;

            sl.playSound(
                    null,
                    sp.blockPosition(),
                    SoundEvents.AMETHYST_BLOCK_STEP,
                    SoundSource.PLAYERS,
                    0.9F,
                    pitch
            );

            // partículas del mineral
            sl.sendParticles(
                    new ItemParticleOption(
                            ParticleTypes.ITEM,
                            stack.copyWithCount(1)
                    ),
                    fx,
                    fy + 0.7,
                    fz,
                    10,
                    0.2, 0.2, 0.2,
                    0.02
            );

            // pequeño brillo mágico
            sl.sendParticles(
                    ParticleTypes.GLOW,
                    fx,
                    fy + 0.8,
                    fz,
                    4,
                    0.25, 0.25, 0.25,
                    0.01
            );
        }

        if (!sp.getAbilities().instabuild) {
            stack.shrink(1);
        }

        com.jvprz.spectrobesreforged.common.feature.prizmod.PrizmodSync.sync(sp,data);
        com.jvprz.spectrobesreforged.common.feature.training.SpectrobeTraining.levelled(sp,entry,updated);
        return true;
    }

    private static int applyStat(int currentValue,
                                 int baseValue,
                                 int maxValue,
                                 Integer delta,
                                 boolean setToMax) {

        if (setToMax) {
            return Math.max(currentValue,maxValue);
        }

        if (delta == null) {
            return currentValue;
        }

        return clamp(currentValue + delta, baseValue, Math.max(maxValue,currentValue));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override public Component getName(ItemStack stack) {
        var name=Component.empty().append(super.getName(stack));
        if(RawFossilItem.data(stack).getBoolean("ExcavationCooperative"))name.append(Component.literal(" ⚒").withStyle(ChatFormatting.AQUA));
        return name;
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if(RawFossilItem.data(stack).getBoolean("ExcavationCooperative"))tooltip.add(Component.translatable("tooltip.spectrobesreforged.excavation_cooperative").withStyle(ChatFormatting.AQUA));

        addStatLine(tooltip, health, healthMax, "tooltip.spectrobesreforged.minergy.health", ChatFormatting.GREEN);
        addStatLine(tooltip, attack, attackMax, "tooltip.spectrobesreforged.minergy.attack", ChatFormatting.RED);
        addStatLine(tooltip, defense, defenseMax, "tooltip.spectrobesreforged.minergy.defense", ChatFormatting.BLUE);

        tooltip.add(Component.translatable("tooltip.spectrobesreforged.training_xp",com.jvprz.spectrobesreforged.common.feature.training.SpectrobeLevels.mineralPercent(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this).getPath())).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable(description).withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltip, flag);
    }

    private static void addStatLine(List<Component> tooltip,
                                    Integer value,
                                    boolean isMax,
                                    String label,
                                    ChatFormatting positiveColor) {

        if (value == null && !isMax) return;

        if (isMax) {
            tooltip.add(Component.translatable("tooltip.spectrobesreforged.mineral_max", Component.translatable(label)).withStyle(ChatFormatting.GOLD));
            return;
        }

        if (value == 0) return;

        String sign = value > 0 ? "+" : "";
        ChatFormatting color = value > 0 ? positiveColor : ChatFormatting.DARK_GRAY;

        tooltip.add(Component.translatable("tooltip.spectrobesreforged.mineral_delta", sign + value, Component.translatable(label)).withStyle(color));
    }
}