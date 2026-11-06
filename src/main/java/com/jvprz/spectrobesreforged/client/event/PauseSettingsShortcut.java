package com.jvprz.spectrobesreforged.client.event;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.client.screen.ModSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

/** Share NeoForge's existing Mods row, preserving the pause menu's height. */
@EventBusSubscriber(modid=SpectrobesReforged.MODID,value=Dist.CLIENT)
public final class PauseSettingsShortcut {
    private PauseSettingsShortcut() {}
    @SubscribeEvent public static void init(ScreenEvent.Init.Post event) {
        if(!(event.getScreen() instanceof PauseScreen screen))return;
        for(var listener:event.getListenersList()) {
            if(listener instanceof Button mods && mods.getMessage().getContents() instanceof TranslatableContents text
                    && text.getKey().equals("fml.menu.mods") && mods.getWidth()>=200) {
                int width=mods.getWidth(),left=(width-4)/2;
                mods.setWidth(left);
                var shortcut=Button.builder(Component.translatable("spectrobesreforged.ui.pauseSettings"),button ->
                    ModList.get().getModContainerById(SpectrobesReforged.MODID).ifPresent(container ->
                        Minecraft.getInstance().setScreen(new ModSettingsScreen(container,screen))))
                    .bounds(mods.getX()+left+4,mods.getY(),width-left-4,mods.getHeight()).build();
                event.addListener(shortcut);
                break;
            }
        }
    }
}
