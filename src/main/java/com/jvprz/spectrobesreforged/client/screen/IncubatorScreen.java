package com.jvprz.spectrobesreforged.client.screen;

import com.jvprz.spectrobesreforged.common.feature.incubator.menu.IncubatorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import com.jvprz.spectrobesreforged.common.config.ClientConfig;
import com.jvprz.spectrobesreforged.common.registry.ModSounds;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import java.util.List;
import java.util.Optional;

/** BTP-inspired signal bands and countdown, adapted to nearby Minecraft sounds. */
public class IncubatorScreen extends AbstractContainerScreen<IncubatorMenu> {
    private static final int[] COLORS={0xff6abfe8,0xff9b9de9,0xffdb93dc};
    private Button action;
    private float displayed;
    private int previousSeconds=-1,successTicks;
    private boolean wasAwakening;
    public IncubatorScreen(IncubatorMenu menu,Inventory inventory,Component title){
        super(menu,inventory,title);imageWidth=216;imageHeight=248;
    }
    private Component text(String key,Object... args){return Component.translatable("gui.spectrobesreforged.awakening."+key,args);}
    private int seconds(){return Math.max(0,(200-menu.getAwakeningProgress()+19)/20);}
    private Component status(){
        if(menu.isDelivering()||successTicks>0)return text("complete");
        if(!menu.isPowered())return text(menu.isAwakening()?"paused":"no_power");
        if(menu.isAwakening()&&menu.getAwakeningProgress()>=200)return text("waiting");
        if(menu.isAwakening())return text(menu.getCapturedFrequency()>0?"keep_signal":"silence");
        return text(menu.getSlot(0).hasItem()?"ready":"insert");
    }
    @Override protected void init(){
        super.init();
        action=addRenderableWidget(new Button(leftPos+64,topPos+134,88,18,text("start"),b->{
            if(minecraft!=null&&minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,
                    menu.isAwakening()?IncubatorMenu.STOP_BUTTON_ID:IncubatorMenu.START_BUTTON_ID);
        },narration->narration.get()){
            @Override protected void renderWidget(GuiGraphics g,int mx,int my,float partial){
                int border=!active?0xff3c4b58:isHoveredOrFocused()?0xffd5f8ff:menu.isAwakening()?0xffd2a77a:0xff75cce5;
                int top=!active?0xff263440:menu.isAwakening()?0xff554438:0xff294e63;
                g.fill(getX()+2,getY(),getX()+width-2,getY()+height,border);
                g.fill(getX(),getY()+2,getX()+width,getY()+height-2,border);
                g.fillGradient(getX()+2,getY()+2,getX()+width-2,getY()+height-2,top,0xff1c303f);
                g.fill(getX()+4,getY()+2,getX()+width-4,getY()+3,active?0xff91c5d7:0xff455663);
                g.drawCenteredString(font,getMessage(),getX()+width/2,getY()+5,active?0xffe6f7ff:0xff8193a2);
            }
        });
        refresh();
    }
    private void refresh(){
        action.setMessage(text(menu.isAwakening()?"stop":"start"));
        action.active=menu.canStart()||(menu.isAwakening()&&menu.getAwakeningProgress()<200);
        action.setTooltip(Tooltip.create(text(menu.isAwakening()?"stop_hint":"start_hint")));
    }
    @Override protected void containerTick(){
        super.containerTick();refresh();
        displayed=ClientConfig.reducedMotion()?menu.getCapturedFrequency():displayed+(menu.getCapturedFrequency()-displayed)*0.5F;
        if(successTicks>0)successTicks--;
        int seconds=seconds();
        if(menu.isAwakening()&&menu.isPowered()&&seconds>0&&seconds<=3&&seconds!=previousSeconds&&minecraft!=null)
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.AWAKENING_CUE.get(),1.2F));
        if(wasAwakening&&menu.isDelivering())successTicks=30;
        previousSeconds=seconds;wasAwakening=menu.isAwakening();
    }
    private void panel(GuiGraphics g,int x,int y,int width,int height,int color){
        g.fill(leftPos+x,topPos+y,leftPos+x+width,topPos+y+height,color);
        g.renderOutline(leftPos+x,topPos+y,width,height,0xff405b6b);
    }
    private void bracket(GuiGraphics g,int x,int y,int w,int h,int color){
        int xx=leftPos+x,yy=topPos+y;
        for(int side=0;side<2;side++)for(int bottom=0;bottom<2;bottom++){
            int px=xx+side*(w-1),py=yy+bottom*(h-1);
            g.fill(px-(side==1?6:0),py,px+(side==0?7:1),py+1,color);
            g.fill(px,py-(bottom==1?6:0),px+1,py+(bottom==0?7:1),color);
        }
    }
    @Override protected void renderBg(GuiGraphics g,float partial,int mouseX,int mouseY){
        int color=COLORS[Math.clamp(menu.getSelectedColor(),0,2)];
        boolean running=menu.isAwakening()&&menu.isPowered();
        float time=minecraft==null||minecraft.level==null?0:minecraft.level.getGameTime()+partial;
        // A restrained metal frame, with a dark glass work area and a separate inventory tray.
        g.fill(leftPos-3,topPos+1,leftPos+219,topPos+247,0xff080e16);
        g.fill(leftPos+1,topPos-3,leftPos+215,topPos+251,0xff080e16);
        g.fillGradient(leftPos,topPos,leftPos+216,topPos+248,0xff2b4052,0xff15232f);
        g.renderOutline(leftPos,topPos,216,248,0xff536d80);
        g.fill(leftPos+2,topPos+2,leftPos+214,topPos+3,0xff7b96a6);
        panel(g,8,6,200,148,0xff101d29);
        g.fillGradient(leftPos+9,topPos+7,leftPos+207,topPos+34,0xff22394b,0xff152735);
        g.fill(leftPos+14,topPos+12,leftPos+17,topPos+20,menu.isPowered()?0xff83dbe7:0xffca9b64);
        g.drawString(font,title,leftPos+23,topPos+10,0xffe4f2f7,false);
        g.drawString(font,font.plainSubstrByWidth(text("source_"+menu.getSignalSource()).getString(),180),leftPos+14,topPos+24,0xff89aabd,false);

        // The specimen sits inside a small luminous chamber; the real container slot stays usable.
        g.fillGradient(leftPos+14,topPos+39,leftPos+86,topPos+88,0xff0b141e,0xff203c4a);
        bracket(g,14,39,72,49,running?color:0xff496678);
        g.renderOutline(leftPos+28,topPos+46,44,34,0xff294757);
        g.fill(leftPos+23,topPos+83,leftPos+77,topPos+85,0xff557f91);
        g.fill(leftPos+31,topPos+81,leftPos+69,topPos+83,running?color:0xff314e60);
        g.fill(leftPos+40,topPos+53,leftPos+60,topPos+73,0xff091621);
        if(running&&!ClientConfig.reducedMotion()){
            int scan=43+(int)(time%38);
            g.fill(leftPos+17,topPos+scan,leftPos+83,topPos+scan+1,0x305bd7ee);
            for(int i=0;i<6;i++){
                double angle=time*0.045+i*Math.PI/3;
                int xx=leftPos+50+(int)(Math.cos(angle)*28),yy=topPos+64+(int)(Math.sin(angle)*18);
                g.fill(xx,yy,xx+2,yy+2,color);
            }
        }
        var fossil=menu.getSlot(0).getItem();
        g.drawString(font,font.plainSubstrByWidth(fossil.isEmpty()?text("fossil").getString():fossil.getHoverName().getString(),104),leftPos+96,topPos+40,0xffdceaf0,false);
        g.drawString(font,text("variant",menu.getSelectedColor()),leftPos+96,topPos+53,color,false);
        for(int band=0;band<3;band++){
            int x=96+band*38;boolean selected=menu.getSelectedColor()==band;
            panel(g,x,67,30,17,selected?0xff294353:0xff162835);
            if(selected){g.fill(leftPos+x,topPos+67,leftPos+x+30,topPos+69,COLORS[band]);g.fill(leftPos+x+1,topPos+83,leftPos+x+29,topPos+84,COLORS[band]);}
            g.drawCenteredString(font,Component.literal(""+band),leftPos+x+15,topPos+72,selected?COLORS[band]:0xff738e9f);
        }
        Component remaining=text("seconds",seconds());
        g.drawString(font,remaining,leftPos+14,topPos+92,0xffbdd4df,false);
        if(menu.isAwakening()&&seconds()>0&&seconds()<=3){
            g.pose().pushPose();g.pose().translate(leftPos+76,topPos+43,0);g.pose().scale(1.5F,1.5F,1);
            g.drawCenteredString(font,Component.literal(""+seconds()),0,0,0xffe9faff);g.pose().popPose();
        }
        g.drawString(font,text("signal",menu.getCapturedFrequency()),leftPos+96,topPos+92,0xffa9c4d4,false);
        panel(g,14,102,188,12,0xff08141f);
        float level=ClientConfig.reducedMotion()?menu.getCapturedFrequency():Mth.lerp(partial,displayed,menu.getCapturedFrequency());
        for(int i=0;i<16;i++){
            int band=i<5?0:i<10?1:2,x=leftPos+21+i*11;
            boolean lit=i<Math.round(level);
            g.fill(x,topPos+105,x+9,topPos+111,lit?COLORS[band]:0xff263a4a);
            if(lit)g.fill(x,topPos+105,x+9,topPos+106,0xffddf5ff);
            if(i==4||i==9)g.fill(x+10,topPos+103,x+11,topPos+113,0xff658196);
        }
        int progress=Math.clamp(menu.getAwakeningProgress(),0,200);
        g.fill(leftPos+14,topPos+128,leftPos+202,topPos+130,0xff263e4e);
        g.fill(leftPos+14,topPos+128,leftPos+14+188*progress/200,topPos+130,menu.isPowered()?color:0xffd6a55e);
        String statusLine=font.plainSubstrByWidth(status().getString(),184);
        g.drawString(font,statusLine,leftPos+108-font.width(statusLine)/2,topPos+117,
                menu.isPowered()?0xffc7dce6:0xffffc888,false);

        g.drawString(font,playerInventoryTitle,leftPos+28,topPos+155,0xffa9c0cd,false);
        for(int row=0;row<3;row++)for(int col=0;col<9;col++){
            panel(g,27+col*18,165+row*18,18,18,0xff101d29);
            g.fill(leftPos+28+col*18,topPos+166+row*18,leftPos+44+col*18,topPos+167+row*18,0xff08121b);
        }
        for(int col=0;col<9;col++)panel(g,27+col*18,223,18,18,0xff1b3040);
        // Four screws anchor the panel without competing with the useful information.
        for(int x:new int[]{3,210})for(int y:new int[]{4,241}){
            g.fill(leftPos+x,topPos+y,leftPos+x+3,topPos+y+3,0xff6b8393);
            g.fill(leftPos+x+1,topPos+y,leftPos+x+2,topPos+y+3,0xff263c4b);
        }
    }
    @Override protected void renderLabels(GuiGraphics g,int x,int y){}
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partial){
        super.render(g,mouseX,mouseY,partial);renderTooltip(g,mouseX,mouseY);
        if((isHovering(94,49,110,36,mouseX,mouseY)||isHovering(14,102,188,12,mouseX,mouseY)))g.renderTooltip(font,List.of(text("bands"),text("capture_hint"),text("final_hint")),Optional.empty(),mouseX,mouseY);
        else if(isHovering(14,116,188,11,mouseX,mouseY))g.renderTooltip(font,status(),mouseX,mouseY);
    }
}
