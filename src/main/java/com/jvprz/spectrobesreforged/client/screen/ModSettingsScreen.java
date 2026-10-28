package com.jvprz.spectrobesreforged.client.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.Util;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

/** Explicit community links: no automatic prompts or server chat announcements. */
public final class ModSettingsScreen extends Screen {
    private final ModContainer container;
    private final Screen parent;
    public ModSettingsScreen(ModContainer container, Screen parent) {
        super(label("settings")); this.container=container; this.parent=parent;
    }
    static Component label(String key) { return Component.translatable("spectrobesreforged.ui."+key); }
    @Override protected void init() {
        int x=width/2-110, y=height/2-65;
        addRenderableWidget(Button.builder(label("hud"),b->minecraft.setScreen(new HudLayoutScreen(this))).bounds(x,y,220,20).build());
        addRenderableWidget(Button.builder(label("preferences"),b->minecraft.setScreen(new ConfigurationScreen(container,this))).bounds(x,y+26,220,20).build());
        addRenderableWidget(Button.builder(label("discord"),b->minecraft.setScreen(new ConfirmLinkScreen(accepted->{
            if(accepted) Util.getPlatform().openUri("https://discord.gg/7QkScUWBe9");
            minecraft.setScreen(this);
        },"https://discord.gg/7QkScUWBe9",true))).bounds(x,y+86,220,20).build());
        addRenderableWidget(Button.builder(label("done"),b->onClose()).bounds(x,y+118,220,20).build());
    }
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partial) {
        super.render(g,mouseX,mouseY,partial);
        g.drawCenteredString(font,title,width/2,height/2-95,0xffe6eff5);
        g.drawCenteredString(font,label("feedback"),width/2,height/2+1,0xff92b9cc);
    }
    @Override public void onClose() { minecraft.setScreen(parent); }
}
