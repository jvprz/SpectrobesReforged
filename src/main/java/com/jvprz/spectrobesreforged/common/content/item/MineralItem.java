package com.jvprz.spectrobesreforged.common.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

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