package com.jvprz.spectrobesreforged.common.feature.training;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public final class SpectrobeNames {
    private SpectrobeNames(){}
    public static Component name(String species,boolean crown){return name(species,crown,false);}
    public static Component name(String species,boolean crown,boolean cooperative){
        String fallback=species.isEmpty()?"?":Character.toUpperCase(species.charAt(0))+species.substring(1);
        var text=Component.empty().append(Component.translatableWithFallback("spectrobesreforged.spectrobe."+species,fallback));
        if(crown)text.append(Component.literal(" ♛").withStyle(ChatFormatting.GOLD));if(cooperative)text.append(Component.literal(" ⚒").withStyle(ChatFormatting.AQUA));return text;
    }
}
