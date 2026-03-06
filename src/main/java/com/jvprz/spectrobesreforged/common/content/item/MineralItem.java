package com.jvprz.spectrobesreforged.common.content.item;

import com.jvprz.spectrobesreforged.common.content.entity.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeSpecies;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.common.network.ModSnapshotSender;
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
                    Component.literal("This Spectrobe is not linked to Prizmod data.")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResult.CONSUME;
        }

        UUID spectrobeId = spectrobe.getPersistentData().getUUID("SpectrobeId");

        PrizmodData data = sp.getData(ModAttachments.PRIZMOD.get());
        var found = data.findAnywhere(spectrobeId);
        if (found.isEmpty()) {
            sp.displayClientMessage(
                    Component.literal("Spectrobe data not found in Prizmod.")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResult.CONSUME;
        }

        SpectrobeEntry entry = found.get();
        SpectrobeSpecies species = SpectrobeSpeciesRegistry.getByKey(entry.species());
        if (species == null) {
            sp.displayClientMessage(
                    Component.literal("Unknown Spectrobe species: " + entry.species())
                            .withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResult.CONSUME;
        }

        int oldHp = entry.hp();
        int oldAtk = entry.atk();
        int oldDef = entry.def();

        int baseHp = oldHp - entry.mineralHpBonus();
        int baseAtk = oldAtk - entry.mineralAtkBonus();
        int baseDef = oldDef - entry.mineralDefBonus();

        int newHp = applyStat(
                oldHp,
                species.stats().hp().base(),
                species.stats().hp().max(),
                health,
                healthMax
        );

        int newAtk = applyStat(
                oldAtk,
                species.stats().attack().base(),
                species.stats().attack().max(),
                attack,
                attackMax
        );

        int newDef = applyStat(
                oldDef,
                species.stats().defense().base(),
                species.stats().defense().max(),
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

        if (!changed) {
            sp.displayClientMessage(
                    Component.literal("This mineral has no effect.")
                            .withStyle(ChatFormatting.YELLOW),
                    true
            );
            return InteractionResult.CONSUME;
        }

        int hpDelta = newHp - oldHp;
        int newHpCur = clamp(entry.hpCur() + hpDelta, 0, newHp);

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
                newDefBonus
        );

        data.replaceEntry(entry.id(), updated);

        var maxHpAttr = spectrobe.getAttribute(Attributes.MAX_HEALTH);
        if (maxHpAttr != null) {
            maxHpAttr.setBaseValue(Math.max(1, newHp));
        }
        spectrobe.setHealth(Math.max(0.0F, Math.min(newHpCur, newHp)));

        if (sp.level() instanceof ServerLevel sl) {

            // sonido de comer
            float pitch = 1.15F + sl.random.nextFloat() * 0.2F;

            sl.playSound(
                    null,
                    spectrobe.blockPosition(),
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
                    spectrobe.getX(),
                    spectrobe.getY() + 0.7,
                    spectrobe.getZ(),
                    10,
                    0.2, 0.2, 0.2,
                    0.02
            );

            // pequeño brillo mágico
            sl.sendParticles(
                    ParticleTypes.GLOW,
                    spectrobe.getX(),
                    spectrobe.getY() + 0.8,
                    spectrobe.getZ(),
                    4,
                    0.25, 0.25, 0.25,
                    0.01
            );
        }

        if (!sp.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.CONSUME;
    }

    private static int applyStat(int currentValue,
                                 int baseValue,
                                 int maxValue,
                                 Integer delta,
                                 boolean setToMax) {

        if (setToMax) {
            return clamp(maxValue, baseValue, maxValue);
        }

        if (delta == null) {
            return clamp(currentValue, baseValue, maxValue);
        }

        return clamp(currentValue + delta, baseValue, maxValue);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

        addStatLine(tooltip, health, healthMax, "Health Minergy", ChatFormatting.GREEN);
        addStatLine(tooltip, attack, attackMax, "Attack Minergy", ChatFormatting.RED);
        addStatLine(tooltip, defense, defenseMax, "Defense Minergy", ChatFormatting.BLUE);

        tooltip.add(Component.empty());
        tooltip.add(Component.literal(description).withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltip, flag);
    }

    private static void addStatLine(List<Component> tooltip,
                                    Integer value,
                                    boolean isMax,
                                    String label,
                                    ChatFormatting positiveColor) {

        if (value == null && !isMax) return;

        if (isMax) {
            tooltip.add(Component.literal("MAX " + label).withStyle(ChatFormatting.GOLD));
            return;
        }

        if (value == 0) return;

        String sign = value > 0 ? "+" : "";
        ChatFormatting color = value > 0 ? positiveColor : ChatFormatting.DARK_GRAY;

        tooltip.add(Component.literal(sign + value + " " + label).withStyle(color));
    }
}