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
    private int toolbarOffset;
    private double grabX,grabY;
    public HudLayoutScreen(Screen parent) { super(ModSettingsScreen.label("hud")); this.parent=parent; }
    private float scale() { return ClientConfig.hudScale(); }
    private float panelWidth() { return 166*scale(); }
    private float panelHeight() { return ClientConfig.hudHeight()*scale(); }
    private float x() { return 8+Math.max(0,width-16-panelWidth())*ClientConfig.hudX()/100f; }
    private float y() { return 8+Math.max(0,height-16-panelHeight())*ClientConfig.hudY()/100f; }
    @Override protected void init() {
        toolbarOffset=0;
        int cx=width/2;
        addRenderableWidget(Button.builder(Component.translatable("spectrobesreforged.configuration.hudVertical"),b->{
            ClientConfig.HUD_VERTICAL.set(!ClientConfig.hudVertical()); ClientConfig.SPEC.save();
            b.setMessage(verticalLabel());
        }).bounds(cx-155,height-102,150,20).build()).setMessage(verticalLabel());
        addRenderableWidget(Button.builder(Component.literal("−"),b->resizeHud(-5)).bounds(cx+5,height-102,70,20).build());
        addRenderableWidget(Button.builder(Component.literal("+"),b->resizeHud(5)).bounds(cx+85,height-102,70,20).build());
        addRenderableWidget(Button.builder(Component.translatable("spectrobesreforged.configuration.hudEnabled"),b->{
            ClientConfig.HUD_ENABLED.set(!ClientConfig.hudEnabled()); ClientConfig.SPEC.save();
            b.setMessage(toggleLabel("spectrobesreforged.ui.hudVisible",ClientConfig.hudEnabled()));
        }).bounds(cx-155,height-52,150,20).build()).setMessage(toggleLabel("spectrobesreforged.ui.hudVisible",ClientConfig.hudEnabled()));
        addRenderableWidget(Button.builder(Component.translatable("spectrobesreforged.configuration.hudTeam"),b->{
            ClientConfig.HUD_TEAM.set(!ClientConfig.hudTeam()); ClientConfig.SPEC.save();
            b.setMessage(toggleLabel("spectrobesreforged.ui.hudTeamToggle",ClientConfig.hudTeam()));
        }).bounds(cx+5,height-52,150,20).build()).setMessage(toggleLabel("spectrobesreforged.ui.hudTeamToggle",ClientConfig.hudTeam()));
        addRenderableWidget(Button.builder(ModSettingsScreen.label("reset"),b->{
            ClientConfig.HUD_X.set(0);ClientConfig.HUD_Y.set(0);ClientConfig.HUD_SCALE.set(85);
            ClientConfig.HUD_VERTICAL.set(false);ClientConfig.HUD_ENABLED.set(true);ClientConfig.HUD_TEAM.set(true);ClientConfig.SPEC.save();rebuildWidgets();
        }).bounds(cx-155,height-27,150,20).build());
        addRenderableWidget(Button.builder(ModSettingsScreen.label("done"),b->onClose()).bounds(cx+5,height-27,150,20).build());
    }
    // The world remains visible as a reference; blur must never run after the preview.
    @Override public void renderBackground(GuiGraphics g,int mx,int my,float partial) {
        if(minecraft.level==null)renderPanorama(g,partial);
        g.fill(0,0,width,height,0x55071018);
    }
    private boolean overPanel(double mx,double my) {
        return mx>=x()&&mx<=x()+panelWidth()&&my>=y()&&my<=y()+panelHeight();
    }
    @Override public void render(GuiGraphics g,int mx,int my,float partial) {
        renderBackground(g,mx,my,partial);
        // Draw the widgets below without Screen.render applying its background a second time.
        boolean highlighted=dragging||overPanel(mx,my);
        int edge=highlighted?0xffa4f1ff:0xff75cde8;
        int px=Math.round(x()),py=Math.round(y()),pw=Math.round(panelWidth()),ph=Math.round(panelHeight());
        if(dragging) {
            g.fill(px+pw/2,0,px+pw/2+1,height,0x4075cde8);
            g.fill(0,py+ph/2,width,py+ph/2+1,0x4075cde8);
        }
        g.fill(px-3,py-3,px+pw+3,py+ph+3,0x90000812);
        g.renderOutline(px-2,py-2,pw+4,ph+4,edge);
        for(int dx:new int[]{0,pw-4})for(int dy:new int[]{0,ph-4})g.fill(px+dx,py+dy,px+dx+4,py+dy+4,edge);
        g.pose().pushPose(); g.pose().translate(x(),y(),0);g.pose().scale(scale(),scale(),1);
        int h=ClientConfig.hudHeight();
        g.fill(0,0,166,h,0xe0111b24);g.renderOutline(0,0,166,h,0xff80bde8);
        g.fill(1,1,165,18,highlighted?0xff2a556b:0xff203d50);
        g.drawString(font,"PRIZMOD",9,5,0xffe6eff5,false);
        for(int i=0;i<3;i++)g.fill(145,5+i*3,157,6+i*3,edge);
        g.fill(8,22,42,56,0xff283b47);
        g.blit(com.jvprz.spectrobesreforged.client.ui.icon.SpectrobeIcons.icon("komainu",0),10,24,0,0,30,30,30,30);
        g.drawString(font,"Komainu",49,24,0xffe6eff5,false);
        g.drawString(font,"Lv. 1",49,37,0xff92a6b5,false);
        g.fill(49,50,156,53,0xff80bde8);
        if(ClientConfig.hudTeam()) for(int i=0;i<6;i++)
            g.renderOutline(ClientConfig.hudVertical()?9:9+i*25,ClientConfig.hudVertical()?70+i*24:70,ClientConfig.hudVertical()?148:22,19,0xff4b6475);
        g.pose().popPose();
        // Keep controls opposite the preview; hide them while dragging to expose the whole screen.
        if(!dragging) {
        int nextOffset=y()+panelHeight()/2>height/2f?128-height:0;
        if(nextOffset!=toolbarOffset) {
            for(var widget:renderables)if(widget instanceof net.minecraft.client.gui.components.AbstractWidget button)
                button.setY(button.getY()+nextOffset-toolbarOffset);
            toolbarOffset=nextOffset;
        }
        g.fill(0,height-128+toolbarOffset,width,height+toolbarOffset,0xee0b1721);
        g.fill(0,height-129+toolbarOffset,width,height-128+toolbarOffset,0xff386477);
        var help=font.split(ModSettingsScreen.label("drag"),Math.max(40,width-16));
        for(int i=0;i<Math.min(2,help.size());i++)
            g.drawCenteredString(font,help.get(i),width/2,height-124+toolbarOffset+i*10,0xffe6eff5);
        for(var widget:renderables)widget.render(g,mx,my,partial);
        g.drawCenteredString(font,Component.translatable("spectrobesreforged.ui.hudStatus",ClientConfig.HUD_SCALE.get(),ClientConfig.hudX(),ClientConfig.hudY()),width/2,height-78+toolbarOffset,0xffb7d6e5);
        g.drawCenteredString(font,ModSettingsScreen.label("hudKeyboard"),width/2,height-65+toolbarOffset,0xff92b9cc);
        }
        if(highlighted && (dragging || my<height-128+toolbarOffset || my>height+toolbarOffset)) {
            var label=ModSettingsScreen.label(dragging?"hudDragging":"hudGrab");
            int labelX=Mth.clamp(px,4,Math.max(4,width-font.width(label)-12));
            int labelY=py>=18?py-17:py+ph+5;
            labelY=Mth.clamp(labelY,2,Math.max(2,height-144));
            g.fill(labelX-3,labelY-3,labelX+font.width(label)+4,labelY+12,0xf0081721);
            g.drawString(font,label,labelX,labelY,0xffa4f1ff,false);
        }
    }
    @Override public boolean mouseClicked(double mx,double my,int button) {
        if(super.mouseClicked(mx,my,button))return true;
        if(button==0&&overPanel(mx,my)) {
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
    @Override public boolean keyPressed(int key,int scan,int modifiers) {
        int step=hasShiftDown()?5:1;
        int dx=key==org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT?-step:key==org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT?step:0;
        int dy=key==org.lwjgl.glfw.GLFW.GLFW_KEY_UP?-step:key==org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN?step:0;
        if(dx!=0||dy!=0){
            ClientConfig.HUD_X.set(Mth.clamp(ClientConfig.hudX()+dx,0,100));
            ClientConfig.HUD_Y.set(Mth.clamp(ClientConfig.hudY()+dy,0,100));return true;
        }
        return super.keyPressed(key,scan,modifiers);
    }
    private Component toggleLabel(String key,boolean enabled){return Component.translatable(key).append(": ").append(Component.translatable(enabled?"options.on":"options.off"));}
    private Component verticalLabel() { return Component.translatable("spectrobesreforged.configuration.hudVertical").append(": ").append(Component.translatable(ClientConfig.hudVertical()?"options.on":"options.off")); }
    private void resizeHud(int delta) {
        ClientConfig.HUD_SCALE.set(Mth.clamp(ClientConfig.HUD_SCALE.get()+delta,50,150));ClientConfig.SPEC.save();
    }
    @Override public void removed(){dragging=false;ClientConfig.SPEC.save();}
    @Override public void onClose(){ClientConfig.SPEC.save();minecraft.setScreen(parent);}
}
