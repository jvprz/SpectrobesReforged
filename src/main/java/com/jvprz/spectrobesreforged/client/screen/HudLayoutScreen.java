package com.jvprz.spectrobesreforged.client.screen;

import com.jvprz.spectrobesreforged.common.config.ClientConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/** Resolution-independent drag-and-scroll layout editor with a persistent preview. */
public final class HudLayoutScreen extends Screen {
    private final Screen parent;
    private boolean dragging;
    private double grabX,grabY;
    public HudLayoutScreen(Screen parent) { super(ModSettingsScreen.label("hud")); this.parent=parent; }
    private float scale() { return ClientConfig.hudScale(); }
    private float panelWidth() { return 166*scale(); }
    private float panelHeight() { return ClientConfig.hudHeight()*scale(); }
    private float x() { return 8+Math.max(0,width-16-panelWidth())*ClientConfig.hudX()/100f; }
    private float y() { return 8+Math.max(0,height-16-panelHeight())*ClientConfig.hudY()/100f; }
    @Override protected void init() {
        int cx=width/2;
        addRenderableWidget(Button.builder(Component.translatable("spectrobesreforged.configuration.hudVertical"),b->{
            ClientConfig.HUD_VERTICAL.set(!ClientConfig.hudVertical()); ClientConfig.SPEC.save();
            b.setMessage(verticalLabel());
        }).bounds(cx-155,height-102,150,20).build()).setMessage(verticalLabel());
        addRenderableWidget(Button.builder(Component.literal("−"),b->resizeHud(-5)).bounds(cx+5,height-102,70,20).build());
        addRenderableWidget(Button.builder(Component.literal("+"),b->resizeHud(5)).bounds(cx+85,height-102,70,20).build());
        addRenderableWidget(Button.builder(Component.translatable("spectrobesreforged.configuration.hudEnabled"),b->{
            ClientConfig.HUD_ENABLED.set(!ClientConfig.hudEnabled()); ClientConfig.SPEC.save();
        }).bounds(cx-155,height-52,150,20).build());
        addRenderableWidget(Button.builder(Component.translatable("spectrobesreforged.configuration.hudTeam"),b->{
            ClientConfig.HUD_TEAM.set(!ClientConfig.hudTeam()); ClientConfig.SPEC.save();
        }).bounds(cx+5,height-52,150,20).build());
        addRenderableWidget(Button.builder(ModSettingsScreen.label("reset"),b->{
            ClientConfig.HUD_X.set(0);ClientConfig.HUD_Y.set(0);ClientConfig.HUD_SCALE.set(85);
            ClientConfig.HUD_VERTICAL.set(false);ClientConfig.HUD_ENABLED.set(true);ClientConfig.HUD_TEAM.set(true);ClientConfig.SPEC.save();rebuildWidgets();
        }).bounds(cx-155,height-27,150,20).build());
        addRenderableWidget(Button.builder(ModSettingsScreen.label("done"),b->onClose()).bounds(cx+5,height-27,150,20).build());
    }
    @Override public void render(GuiGraphics g,int mx,int my,float partial) {
        g.drawCenteredString(font,ModSettingsScreen.label("drag"),width/2,8,0xffe6eff5);
        g.pose().pushPose(); g.pose().translate(x(),y(),0);g.pose().scale(scale(),scale(),1);
        int h=ClientConfig.hudHeight();
        g.fill(0,0,166,h,0xe0111b24);g.renderOutline(0,0,166,h,0xff80bde8);
        g.drawString(font,"PRIZMOD",9,5,0xffb8d9e9,false);
        g.fill(8,22,42,56,0xff283b47);
        g.drawString(font,"Spectrobe",49,24,0xffe6eff5,false);
        g.drawString(font,"Lv. 1",49,37,0xff92a6b5,false);
        g.fill(49,50,156,53,0xff80bde8);
        if(ClientConfig.hudTeam()) for(int i=0;i<6;i++)
            g.renderOutline(ClientConfig.hudVertical()?9:9+i*25,ClientConfig.hudVertical()?70+i*24:70,ClientConfig.hudVertical()?148:22,19,0xff4b6475);
        g.pose().popPose();
        super.render(g,mx,my,partial);
        String status=Component.translatable(ClientConfig.hudEnabled()?"options.on":"options.off").getString()+" | "+ClientConfig.HUD_SCALE.get()+"% | "+ClientConfig.hudX()+"%, "+ClientConfig.hudY()+"%";
        g.drawCenteredString(font,status,width/2,height-66,0xff92b9cc);
    }
    @Override public boolean mouseClicked(double mx,double my,int button) {
        if(super.mouseClicked(mx,my,button))return true;
        if(button==0&&mx>=x()&&mx<=x()+panelWidth()&&my>=y()&&my<=y()+panelHeight()) {
            dragging=true;grabX=mx-x();grabY=my-y();return true;
        }return false;
    }
    @Override public boolean mouseDragged(double mx,double my,int button,double dx,double dy) {
        if(dragging&&button==0){
            ClientConfig.HUD_X.set(Mth.clamp((int)Math.round((mx-grabX-8)/Math.max(1,width-16-panelWidth())*100),0,100));
            ClientConfig.HUD_Y.set(Mth.clamp((int)Math.round((my-grabY-8)/Math.max(1,height-16-panelHeight())*100),0,100));
            return true;
        }return super.mouseDragged(mx,my,button,dx,dy);
    }
    @Override public boolean mouseReleased(double mx,double my,int button) {
        if(dragging){dragging=false;ClientConfig.SPEC.save();return true;}
        return super.mouseReleased(mx,my,button);
    }
    @Override public boolean mouseScrolled(double mx,double my,double horizontal,double vertical) {
        resizeHud((int)Math.signum(vertical)*5); return true;
    }
    private Component verticalLabel() { return Component.translatable("spectrobesreforged.configuration.hudVertical").append(": ").append(Component.translatable(ClientConfig.hudVertical()?"options.on":"options.off")); }
    private void resizeHud(int delta) {
        ClientConfig.HUD_SCALE.set(Mth.clamp(ClientConfig.HUD_SCALE.get()+delta,50,150));ClientConfig.SPEC.save();
    }
    @Override public void onClose(){ClientConfig.SPEC.save();minecraft.setScreen(parent);}
}
