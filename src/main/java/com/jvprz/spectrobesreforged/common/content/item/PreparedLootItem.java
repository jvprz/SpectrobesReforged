package com.jvprz.spectrobesreforged.common.content.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import java.util.List;

/** Reserved content has an honest tooltip and no accidental right-click rewards. */
public final class PreparedLootItem extends Item {
    private final boolean placeholder;
    public PreparedLootItem(){this(false);}
    public PreparedLootItem(boolean placeholder){this(new Properties(),placeholder);}
    public PreparedLootItem(Properties properties,boolean placeholder){super(properties);this.placeholder=placeholder;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        lines.add(Component.translatable("tooltip.spectrobesreforged.prepared_loot"));
        if(placeholder)lines.add(Component.translatable("tooltip.spectrobesreforged.placeholder_icon"));
    }
}
