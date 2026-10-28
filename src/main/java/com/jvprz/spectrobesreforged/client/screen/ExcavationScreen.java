package com.jvprz.spectrobesreforged.client.screen;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.config.ClientConfig;
import com.jvprz.spectrobesreforged.common.feature.excavation.*;
import com.jvprz.spectrobesreforged.common.network.C2SExcavate;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import java.util.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;


public final class ExcavationScreen extends AbstractContainerScreen<ExcavationMenu> {
    private static final int BOARD_X = 89, BOARD_Y = 76, CELL = 5, BOARD_SIZE = 160;
    private static final int[] TOOL_ORDER = {0,1,4,5,6,7,8,9,12,13};
    private static final String[] TOOLS = {"drill","blower","fine","heavy","recovery","piton","fire","water","blower_s","vacuum","solvent","sonic","giga","blast"};
    private final List<Button> tools = new ArrayList<>();
    private final List<Chip> chips = new ArrayList<>();
    private final int[] previousCells = new int[ExcavationPuzzle.CELLS];
    private int tool, sendDelay, confirmation, flashTicks, previousIntegrity = 100, previousStatus;
    private boolean scanning, working, snapshotReady;
    private int cursorDelay;
    private int previousOpening=-1,layerBreakTicks;
    private String brokenLayerMaterial="stone";
    private double pointerX, pointerY;
    private Button extract, scanner, inventoryToggle;
    private static final class Chip {
        float x, y, vx, vy; int life = 12; final int color;
        Chip(float x, float y, float vx, float vy, int color) { this.x=x;this.y=y;this.vx=vx;this.vy=vy;this.color=color; }
    }
    public ExcavationScreen(ExcavationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageWidth = 338; imageHeight = 248;
    }
    private Component text(String key, Object... args) { return Component.translatable("gui.spectrobesreforged.excavation."+key,args); }
    private void select(int next) { tool = next; working = false; confirmation = 0; menu.showPlayerInventory(false); sendCursor(); }
    private void toggleInventory() { menu.showPlayerInventory(!menu.playerInventoryVisible()); working = false; confirmation = 0; sendCursor(); }
    private final class ToolButton extends Button {
        private final int id;
        ToolButton(int x, int y, int id) {
            super(x,y,26,26,text(TOOLS[id]),b->select(id),DEFAULT_NARRATION); this.id=id;
        }
        @Override protected void renderWidget(GuiGraphics g,int mouseX,int mouseY,float partial) {
            int x=getX(),y=getY();
            int rim=id==tool?0xff81e6c1:isHoveredOrFocused()?0xffd5ddd8:0xff626d70;
            if(id==ExcavationPuzzle.BLAST&&id!=tool)rim=0xffb87562;
            g.fill(x+3,y,x+23,y+26,rim);g.fill(x,y+3,x+26,y+23,rim);
            g.fill(x+3,y+2,x+23,y+24,0xff202c32);g.fill(x+2,y+3,x+24,y+23,0xff202c32);
            g.fill(x+4,y+4,x+22,y+22,id==tool?0xff31534e:0xff354149);
            drawToolIcon(g,id,x+5,y+5);
            if(!active)g.fill(x+3,y+3,x+23,y+23,0x99202a30);
            if(busy(id))g.renderOutline(x+1,y+1,24,24,0xffe39767);
        }
    }
    /** The same editable sprite is used by the inventory item and its table button. */
    private static void drawToolIcon(GuiGraphics g,int id,int x,int y) {
        var texture=ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,
                "textures/item/excavation/"+TOOLS[id]+".png");
        g.blit(texture,x,y,0,0,16,16,16,16);
    }
    @Override protected void init() {
        super.init(); tools.clear(); menu.showPlayerInventory(false);
        for(int index=0;index<TOOL_ORDER.length;index++) {
            int column=index/5, row=index%5;
            tools.add(addRenderableWidget(new ToolButton(leftPos+(column==0?12:300),topPos+78+row*30,TOOL_ORDER[index])));
        }
        inventoryToggle=addRenderableWidget(Button.builder(text("inventory"),b->toggleInventory())
                .bounds(leftPos+250,topPos+6,78,16).build());
        scanner=addRenderableWidget(Button.builder(text("scanner"),b->{scanning=!scanning;working=false;})
                .bounds(leftPos+10,topPos+228,68,16).build());
        extract=addRenderableWidget(Button.builder(text("extract"),b->{
            working=false;
            if(minecraft!=null&&minecraft.gameMode!=null)minecraft.gameMode.handleInventoryButtonClick(menu.containerId,0);
        }).bounds(leftPos+260,topPos+228,68,16).build());
        refreshButtons();
    }
    private void refreshButtons() {
        for(int row=0;row<tools.size();row++) {
            var button=tools.get(row);
            int id=TOOL_ORDER[row];
            button.active=!busy(id) && menu.value(3)==1 && (id!=ExcavationPuzzle.RECOVERY||(menu.value(8)>0&&menu.value(1)<100))
                    && (id!=ExcavationPuzzle.GIGA||menu.value(13)==1);
        }
        inventoryToggle.setMessage(text(menu.playerInventoryVisible()?"excavate":"inventory"));
        scanner.active=menu.value(3)==1;
        scanner.setMessage(text(scanning?"scanner_on":"scanner"));
        extract.active=menu.value(3)==1&&menu.value(0)>=90&&menu.getSlot(1).getItem().isEmpty();
    }
    @Override protected void containerTick() {
        super.containerTick(); refreshButtons();
        if(busy(menu.value(9)>0?ExcavationPuzzle.TAP:tool))working=false;
        if(--cursorDelay<=0){cursorDelay=4;sendCursor();}
        if(layerBreakTicks>0)layerBreakTicks--;
        if(sendDelay>0)sendDelay--; if(confirmation>0)confirmation--; if(flashTicks>0)flashTicks--;
        if(working&&sendDelay==0)sendWork();
        if(menu.value(1)<previousIntegrity)flashTicks=8;
        previousIntegrity=menu.value(1);
        if(menu.value(3)!=1) { working=false; confirmation=0; scanning=false; }
        if(menu.value(3)==2 && previousStatus==1) {
            chips.clear(); flashTicks=16;
            if(!ClientConfig.reducedMotion())for(int i=0;i<24;i++)chips.add(new Chip(BOARD_SIZE/2f+(i%6-3)*7,BOARD_SIZE/2f+(i/6-2)*8,(i%7-3)*.7f,-1.4f-i%3*.4f,i%2==0?0xffd9c79a:0xff88847b));
        }
        if(menu.value(3)==1&&previousStatus==1&&previousOpening>menu.value(9)&&previousOpening>0) {
            layerBreakTicks=12;
            brokenLayerMaterial=ExcavationProfile.openingTexture(previousOpening,menu.value(10),menu.value(18));
            if(!ClientConfig.reducedMotion())for(int i=0;i<36;i++)chips.add(new Chip((i*47)%BOARD_SIZE,(i*61)%BOARD_SIZE,(i%7-3)*.5f,-1.4f-i%4*.35f,0xffc4c6c1));
        }
        if(menu.value(3)!=1)layerBreakTicks=0;
        previousOpening=menu.value(9);
        previousStatus=menu.value(3);
        for(int i=0;i<previousCells.length;i++) {
            int value=menu.cell(i);
            if(!ClientConfig.reducedMotion()&&snapshotReady&&value!=previousCells[i]&&chips.size()<100&&menu.value(3)==1&&i%3==0) {
                float vx=((i*37%11)-5)*.22f,vy=-.6f-(i%4)*.15f;
                chips.add(new Chip(i%32*CELL+2,i/32*CELL+2,vx,vy,(previousCells[i]&ExcavationPuzzle.DUST)!=0?0xffeee3bd:0xff968371));
            }
            previousCells[i]=value;
        }
        snapshotReady=menu.value(3)==1;
        if(ClientConfig.reducedMotion())chips.clear();
        chips.removeIf(c->--c.life<=0);
        for(var c:chips){c.x+=c.vx;c.y+=c.vy;c.vy+=.12f;}
    }
    private boolean onBoard(double x,double y){return menu.value(3)==1&&!menu.playerInventoryVisible()&&menu.getCarried().isEmpty()&&x>=leftPos+BOARD_X&&x<leftPos+BOARD_X+BOARD_SIZE&&y>=topPos+BOARD_Y&&y<topPos+BOARD_Y+BOARD_SIZE;}
    private boolean busy(int id){
        var owner=menu.toolOwner(id);
        return owner!=null&&minecraft!=null&&minecraft.player!=null&&!owner.player().equals(minecraft.player.getUUID());
    }
    private void sendCursor(){
        if(minecraft==null||minecraft.player==null)return;
        boolean inside=onBoard(pointerX,pointerY);
        int x=inside?(int)(pointerX-leftPos-BOARD_X):-1,y=inside?(int)(pointerY-topPos-BOARD_Y):-1;
        PacketDistributor.sendToServer(new com.jvprz.spectrobesreforged.common.network.C2SExcavationCursor(
                menu.containerId,x,y,menu.value(9)>0?ExcavationPuzzle.TAP:tool,working&&inside));
    }
    private void renderCoop(GuiGraphics g){
        if(minecraft==null||minecraft.player==null)return;
        int bx=leftPos+BOARD_X,by=topPos+BOARD_Y;
        if(!menu.playerInventoryVisible()){
            g.enableScissor(bx,by,bx+BOARD_SIZE,by+BOARD_SIZE);
            int[] colors={0xff7ee8e3,0xffffbd75,0xffce9fff,0xff96ed9b,0xffff90b4,0xfff0e68a};
            for(var cursor:menu.coop()){
                if(cursor.player().equals(minecraft.player.getUUID())||cursor.x()<0||cursor.y()<0)continue;
                int color=colors[Math.floorMod(cursor.player().hashCode(),colors.length)],x=bx+cursor.x(),y=by+cursor.y();
                g.fill(x-4,y-2,x+5,y+3,0xdd10151c);g.fill(x-2,y-4,x+3,y+5,0xdd10151c);
                g.fill(x-3,y,x+4,y+1,color);g.fill(x,y-3,x+1,y+4,color);
                if(cursor.using()){
                    g.renderOutline(x-5,y-5,11,11,color);
                    if(cursor.tool()<TOOLS.length){g.pose().pushPose();g.pose().translate(x+6,y+4,0);g.pose().scale(.5F,.5F,1);drawToolIcon(g,cursor.tool(),0,0);g.pose().popPose();}
                }
                int labelX=Math.clamp(x+7,bx+2,bx+BOARD_SIZE-82),labelY=Math.clamp(y-12,by+2,by+BOARD_SIZE-10);
                String name=font.plainSubstrByWidth(cursor.name(),78);
                g.fill(labelX-1,labelY-1,labelX+font.width(name)+2,labelY+9,0xc010151c);g.drawString(font,name,labelX,labelY,color,false);
            }
            g.disableScissor();
        }
        if(menu.coop().size()>1)g.drawCenteredString(font,text("coop_count",menu.coop().size()),leftPos+169,topPos+233,0xffa3e3d0);
    }

    private void sendWork(){
        if(!onBoard(pointerX,pointerY)||menu.value(3)!=1||sendDelay>0)return;
        if(busy(menu.value(9)>0?ExcavationPuzzle.TAP:tool)){working=false;return;}
        int x=(int)(pointerX-leftPos-BOARD_X)/CELL,y=(int)(pointerY-topPos-BOARD_Y)/CELL;
        int action=menu.value(9)>0?ExcavationPuzzle.TAP:tool;
        PacketDistributor.sendToServer(new C2SExcavate(menu.containerId,x,y,action)); sendDelay=2;
        if(action==ExcavationPuzzle.TAP||action==ExcavationPuzzle.RECOVERY||action==ExcavationPuzzle.BLAST||action==ExcavationPuzzle.GIGA)working=false;
    }
    @Override public boolean mouseClicked(double x,double y,int button){
        pointerX=x;pointerY=y;
        if(button==0&&onBoard(x,y)){
            if(menu.value(9)==0&&tool==ExcavationPuzzle.BLAST&&confirmation==0){confirmation=100;working=false;return true;}
            working=true;sendWork();return true;
        }
        return super.mouseClicked(x,y,button);
    }
    @Override public boolean mouseDragged(double x,double y,int button,double dx,double dy){pointerX=x;pointerY=y;if(working)return true;return super.mouseDragged(x,y,button,dx,dy);}
    @Override public boolean mouseReleased(double x,double y,int button){if(button==0){working=false;sendCursor();}return super.mouseReleased(x,y,button);}
    @Override public boolean keyPressed(int key,int scanCode,int modifiers){
        if(key==GLFW.GLFW_KEY_I){toggleInventory();return true;}
        if(key==GLFW.GLFW_KEY_R){scanning=!scanning;working=false;return true;}
        
        if(key==GLFW.GLFW_KEY_1){select(ExcavationPuzzle.DRILL);return true;}
        if(key==GLFW.GLFW_KEY_2){select(ExcavationPuzzle.BLOWER);return true;}
        return super.keyPressed(key,scanCode,modifiers);
    }
    private void meter(GuiGraphics g,int px,int py,String key,int value,int color) {
        int x=leftPos+px,y=topPos+py;
        g.drawString(font,text(key,value),x,y,0xffc6d9d6,false);
        g.fill(x,y+9,x+78,y+12,0xff0a141a);g.fill(x,y+9,x+value*78/100,y+12,color);
    }
    private TextureAtlasSprite materialSprite(String path) {
        return minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.withDefaultNamespace("block/"+path));
    }
    /** Batch the atlas quads; vanilla materials retain resource-pack textures and animated fluids. */
    private void renderMaterials(GuiGraphics g,int bx,int by) {
        if(menu.value(3)!=1)return;
        TextureAtlasSprite stone=materialSprite(ExcavationProfile.rockTexture(menu.value(18))), dirt=materialSprite("dirt");
        TextureAtlasSprite openingSprite=materialSprite(ExcavationProfile.openingTexture(menu.value(9),menu.value(10),menu.value(18)));
        TextureAtlasSprite cover=materialSprite(switch(menu.value(10)) {
            case 1 -> menu.value(18)==ExcavationProfile.RED_SANDSTONE?"red_sand":"sand"; case 2 -> "packed_ice"; case 3 -> "lava_still";
            case 4 -> "water_still"; case 5 -> "mud"; default -> "stone";
        });
        g.flush();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0,InventoryMenu.BLOCK_ATLAS);RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();
        var vertices=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);
        var matrix=g.pose().last().pose();
        for(int i=0;i<ExcavationPuzzle.CELLS;i++) {
            int cell=menu.cell(i),depth=cell&7;
            boolean opening=menu.value(9)>0, covered=!opening&&(cell&ExcavationPuzzle.COVER)!=0;
            if(!opening&&!covered&&depth==0)continue;
            var sprite=opening?openingSprite:covered?cover:depth==1?dirt:stone;
            float shade=covered?Math.min(1f,.82f+.06f*ExcavationPuzzle.coverLayers(cell)):opening||depth==1?1f:depth==2?.86f:.94f;
            int px=i%32*CELL,py=i/32*CELL;
            // Split at native 16-pixel tile boundaries instead of stretching each cell's texture.
            for(int oy=0;oy<CELL;) {
                int sy=(py+oy)%16,h=Math.min(CELL-oy,16-sy);
                for(int ox=0;ox<CELL;) {
                    int sx=(px+ox)%16,w=Math.min(CELL-ox,16-sx);
                    float u0=sprite.getU(sx/16f),u1=sprite.getU((sx+w)/16f),v0=sprite.getV(sy/16f),v1=sprite.getV((sy+h)/16f);
                    int xx=bx+px+ox,yy=by+py+oy;
                    vertices.addVertex(matrix,xx,yy,0).setUv(u0,v0).setColor(shade,shade,shade,1f);
                    vertices.addVertex(matrix,xx,yy+h,0).setUv(u0,v1).setColor(shade,shade,shade,1f);
                    vertices.addVertex(matrix,xx+w,yy+h,0).setUv(u1,v1).setColor(shade,shade,shade,1f);
                    vertices.addVertex(matrix,xx+w,yy,0).setUv(u1,v0).setColor(shade,shade,shade,1f);
                    ox+=w;
                }
                oy+=h;
            }
        }
        var mesh=vertices.build();if(mesh!=null)BufferUploader.drawWithShader(mesh);
        RenderSystem.disableBlend();
    }
    private void renderLayerBreak(GuiGraphics g,int bx,int by,float partial) {
        if(layerBreakTicks<=0||menu.value(3)!=1||menu.playerInventoryVisible())return;
        float remaining=Math.max(0,layerBreakTicks-partial),progress=(12-remaining)/12f;
        var sprite=materialSprite(brokenLayerMaterial);
        g.flush();RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0,InventoryMenu.BLOCK_ATLAS);RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();
        var vertices=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);
        var matrix=g.pose().last().pose();
        int size=BOARD_SIZE/4;
        for(int row=0;row<4;row++)for(int col=0;col<4;col++) {
            float shiftX=(col-1.5f)*(2+progress*18),shiftY=(row-1.5f)*(2+progress*12)+progress*progress*12;
            for(int oy=0;oy<size;) {
                int sy=(row*size+oy)%16,h=Math.min(size-oy,16-sy);
                for(int ox=0;ox<size;) {
                    int sx=(col*size+ox)%16,w=Math.min(size-ox,16-sx);
                    float u0=sprite.getU(sx/16f),u1=sprite.getU((sx+w)/16f),v0=sprite.getV(sy/16f),v1=sprite.getV((sy+h)/16f);
                    float xx=bx+col*size+ox+shiftX,yy=by+row*size+oy+shiftY,alpha=remaining/12f;
                    vertices.addVertex(matrix,xx,yy,0).setUv(u0,v0).setColor(1f,1f,1f,alpha);
                    vertices.addVertex(matrix,xx,yy+h,0).setUv(u0,v1).setColor(1f,1f,1f,alpha);
                    vertices.addVertex(matrix,xx+w,yy+h,0).setUv(u1,v1).setColor(1f,1f,1f,alpha);
                    vertices.addVertex(matrix,xx+w,yy,0).setUv(u1,v0).setColor(1f,1f,1f,alpha);
                    ox+=w;
                }
                oy+=h;
            }
        }
        BufferUploader.drawWithShader(vertices.buildOrThrow());RenderSystem.disableBlend();
    }
    private String statusKey() {
        if(menu.value(3)==0)return "insert";
        if(menu.value(3)==2)return "broken";
        if(menu.value(3)==3)return "finished";
        if(confirmation>0)return "confirm_blast";
        if(menu.value(17)==1)return "giga_locked";
        if(menu.value(7)>=85)return "gas_warning";
        if(menu.value(6)>=85)return "heat_warning";
        if(onBoard(pointerX,pointerY)&&menu.value(9)==0) {
            int cx=(int)(pointerX-leftPos-BOARD_X)/CELL,cy=(int)(pointerY-topPos-BOARD_Y)/CELL;
            int value=menu.cell(cy*32+cx);
            if((value&ExcavationPuzzle.COVER)!=0)return "terrain_"+menu.value(10);
            if((value&ExcavationPuzzle.DUST)!=0)return "dust_hint";
            if((value&7)>1)return "rock_hint";
            if((value&7)==1)return "soil_hint";
            return (value&ExcavationPuzzle.FOSSIL)!=0?"exposed_hint":"clear_hint";
        }
        return "terrain_"+menu.value(10);
    }
    private void renderOutcome(GuiGraphics g,int bx,int by) {
        if(menu.value(3)==1||menu.playerInventoryVisible())return;
        int status=menu.value(3),cx=bx+BOARD_SIZE/2;
        g.fill(bx+7,by+38,bx+BOARD_SIZE-7,by+122,0xeb17222a);
        if(status==2) {
            for(int i=0;i<7;i++) {
                int xx=cx-24+i*7,yy=by+23+(i*11%16);
                g.fill(xx,yy,xx+4+(i%3),yy+3+(i%2),i%2==0?0xffb6aa8c:0xff777b7e);
            }
            for(int i=0;i<8;i++) {
                g.fill(cx-8+i*2,by+45+i*2,cx-5+i*2,by+48+i*2,0xffe3756d);
                g.fill(cx+5-i*2,by+45+i*2,cx+8-i*2,by+48+i*2,0xffe3756d);
            }
        }
        String title=status==2?"broken":status==3?"finished":"insert";
        g.drawCenteredString(font,text(title),cx,by+69,status==2?0xffffa096:0xffb7e5d2);
        g.drawWordWrap(font,text(status==2?"lost_hint":status==3?"collected_hint":"inventory_hint"),bx+17,by+85,BOARD_SIZE-34,0xffc6d1d3);
        if(status==3)g.drawCenteredString(font,text("xp_award",menu.value(14)),cx,by+112,0xffa7df92);
    }
    @Override protected void renderBg(GuiGraphics g,float partial,int mouseX,int mouseY){
        int x=leftPos,y=topPos,bx=x+BOARD_X,by=y+BOARD_Y;
        g.fill(x-2,y-2,x+imageWidth+2,y+imageHeight+2,0xff10181d);
        g.fill(x,y,x+imageWidth,y+imageHeight,0xff28343b);
        g.fill(x+3,y+3,x+imageWidth-3,y+imageHeight-3,0xff1a252d);
        g.fill(x+5,y+5,x+imageWidth-5,y+23,0xff30434a);
        g.fill(x+8,y+26,x+76,y+65,0xff26363d);
        g.fill(x+81,y+26,x+253,y+65,0xff12272d);
        g.fill(x+258,y+26,x+330,y+65,0xff223b40);
        for(int gx=82;gx<252;gx+=8)g.fill(x+gx,y+27,x+gx+1,y+64,0x183bbd9b);
        for(int gy=27;gy<65;gy+=8)g.fill(x+82,y+gy,x+252,y+gy+1,0x183bbd9b);
        g.drawString(font,text("slots"),x+10,y+29,0xffc2d5d3,false);
        g.drawString(font,text("skill",menu.value(11),menu.value(12)),x+85,y+27,0xff91ddc4,false);
        meter(g,85,39,"integrity",menu.value(1),0xff70d6ae);
        meter(g,169,39,"stress",menu.value(5),menu.value(5)>=75?0xffdf625a:0xffe0bb61);
        meter(g,85,52,"heat",menu.value(6),0xffe38b54);
        meter(g,169,52,"gas",menu.value(7),0xffb2b9be);
        g.pose().pushPose();g.pose().translate(x+266,y+28,0);g.pose().scale(1.5f,1.5f,1);
        g.drawString(font,Component.literal(String.format(java.util.Locale.ROOT,"%03d%%",menu.value(0))),0,0,0xffa0efd5,false);g.pose().popPose();
        g.drawString(font,Component.literal(String.format(java.util.Locale.ROOT,"%02d:%02d",menu.value(2)/60,menu.value(2)%60)),x+272,y+49,0xffc3d5d1,false);
        g.drawCenteredString(font,text(TOOLS[tool]),x+imageWidth/2,y+67,0xffc8e8df);
        g.fill(bx-2,by-2,bx+BOARD_SIZE+2,by+BOARD_SIZE+2,flashTicks>0?0xffe45656:0xff111116);
        g.fill(bx,by,bx+BOARD_SIZE,by+BOARD_SIZE,0xff20282a);
        for(int line=0;line<BOARD_SIZE;line+=10) {
            g.fill(bx+line,by,bx+line+1,by+BOARD_SIZE,0xff2a3538);
            g.fill(bx,by+line,bx+BOARD_SIZE,by+line+1,0xff2a3538);
        }
        int itemId=menu.value(4);var id=BuiltInRegistries.ITEM.getKey(BuiltInRegistries.ITEM.byId(itemId));
        boolean fossil=id.getPath().endsWith("_fossil");
        if(menu.value(3)==1&&itemId>0&&id.getNamespace().equals(SpectrobesReforged.MODID)){
            var texture=ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID,"textures/item/"+(fossil?"fossil/":"minerals/")+id.getPath()+".png");
            g.blit(texture,bx,by,0,0,BOARD_SIZE,BOARD_SIZE,BOARD_SIZE,BOARD_SIZE);
        }
        g.enableScissor(bx,by,bx+BOARD_SIZE,by+BOARD_SIZE);
        renderMaterials(g,bx,by);
        if(menu.value(3)==1&&menu.value(9)==0)for(int i=0;i<ExcavationPuzzle.CELLS;i++) {
            int value=menu.cell(i),depth=value&7,xx=bx+i%32*CELL,yy=by+i/32*CELL;
            if((value&ExcavationPuzzle.COVER)==0&&depth>0) {
                if(i<32||(menu.cell(i-32)&7)<depth)g.fill(xx,yy,xx+CELL,yy+1,0x55ffffff);
                if(i%32==31||(menu.cell(i+1)&7)<depth)g.fill(xx+CELL-1,yy,xx+CELL,yy+CELL,0x77000000);
            }
            if((value&ExcavationPuzzle.DUST)!=0&&(value&ExcavationPuzzle.COVER)==0) {
                // Pale loose deposits, with holes and separate grains, contrast with solid dirt/stone.
                if(depth==0)g.fill(xx,yy,xx+CELL,yy+CELL,0x88e7dbb1);
                int offset=(i*13)%3;
                g.fill(xx+offset,yy+1,xx+offset+2,yy+3,0xffeee3bd);
                g.fill(xx+3,yy+3,xx+5,yy+5,0xffc7b586);
                g.fill(xx+1,yy+4,xx+2,yy+5,0xfff7edd1);
            }
            if(scanning&&(value&ExcavationPuzzle.FOSSIL)!=0)g.fill(xx,yy,xx+CELL,yy+CELL,0x804ccfdd);
        }
        if(menu.value(3)==1&&menu.value(7)>25){int alpha=Math.min(130,menu.value(7));g.fill(bx,by,bx+BOARD_SIZE,by+BOARD_SIZE,alpha<<24|0x9e967b);}
        if(!ClientConfig.reducedMotion())renderLayerBreak(g,bx,by,partial);
        renderOutcome(g,bx,by);
        for(var c:chips)g.fill(bx+(int)c.x,by+(int)c.y,bx+(int)c.x+2,by+(int)c.y+2,c.color);
        g.disableScissor();
        if(menu.value(3)==1&&menu.value(9)>0){g.fill(bx+12,by+58,bx+148,by+96,0xdd171b22);g.drawCenteredString(font,text("surface",menu.value(9)),bx+80,by+63,0xfff1eee6);g.drawCenteredString(font,text("tap"),bx+80,by+78,0xffb6dbe8);}
        if(menu.value(3)==1&&onBoard(mouseX,mouseY)&&menu.value(9)==0){
            int radius=ExcavationPuzzle.radius(tool),xx=bx+(mouseX-bx)/CELL*CELL,yy=by+(mouseY-by)/CELL*CELL;
            int l=Math.max(bx,xx-radius*CELL),t=Math.max(by,yy-radius*CELL);
            g.renderOutline(l,t,Math.min(bx+BOARD_SIZE,xx+(radius+1)*CELL)-l,Math.min(by+BOARD_SIZE,yy+(radius+1)*CELL)-t,0xffe6cf9a);
        }
        if(menu.playerInventoryVisible()) {
            g.fill(x+81,y+103,x+257,y+220,0xff0e171e);g.renderOutline(x+81,y+103,176,117,0xff73b9aa);
            g.drawCenteredString(font,text("inventory"),x+169,y+113,0xffc4e1d9);
        }
        for(var slot:menu.slots)if(slot.isActive()){
            g.fill(x+slot.x-1,y+slot.y-1,x+slot.x+17,y+slot.y+17,0xff0c171c);
            g.fill(x+slot.x,y+slot.y,x+slot.x+16,y+slot.y+16,0xff4d6268);
        }
        String state=statusKey();
        var stateLines=font.split(text(state),46);
        for(int line=0;line<Math.min(5,stateLines.size());line++)g.drawString(font,stateLines.get(line),x+42,y+116+line*9,0xffc2c8d2,false);
        if(confirmation>0&&menu.value(3)==1&&!menu.playerInventoryVisible()) {
            g.fill(bx+2,by+BOARD_SIZE-29,bx+BOARD_SIZE-2,by+BOARD_SIZE-2,0xee361b20);
            g.drawWordWrap(font,text("confirm_blast"),bx+7,by+BOARD_SIZE-25,BOARD_SIZE-14,0xffffc2b6);
        }
        if(layerBreakTicks>0&&!menu.playerInventoryVisible()) {
            g.fill(bx+13,by+BOARD_SIZE-20,bx+BOARD_SIZE-13,by+BOARD_SIZE-3,0xd91a2d2a);
            g.drawCenteredString(font,text("layer_broken"),bx+BOARD_SIZE/2,by+BOARD_SIZE-16,0xffb4f0d5);
        }
        if(menu.value(16)==1)g.drawString(font,Component.literal("★"),x+315,y+49,0xffffd75c,false);
    }
    @Override protected void renderLabels(GuiGraphics g,int mouseX,int mouseY){g.drawString(font,title,10,8,0xfff2f3f5,false);}
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partial){
        pointerX=mouseX;pointerY=mouseY;super.render(g,mouseX,mouseY,partial);renderCoop(g);renderTooltip(g,mouseX,mouseY);
        for(int row=0;row<tools.size();row++)if(tools.get(row).visible&&tools.get(row).isHovered()){
            int id=TOOL_ORDER[row];
            var tip=Component.empty().append(text(TOOLS[id]));
            if(ClientConfig.excavationHints())tip.append(" — ").append(text(TOOLS[id]+"_hint"));
            var owner=menu.toolOwner(id);if(owner!=null&&busy(id))tip.append(" ").append(text("tool_busy",owner.name()));
            if(id==ExcavationPuzzle.RECOVERY)tip=Component.empty().append(tip).append(" ").append(text("repairs",menu.value(8)));
            g.renderTooltip(font,font.split(tip,220),mouseX,mouseY);
        }
        if(onBoard(mouseX,mouseY)&&menu.value(4)>0&&scanning)
            g.renderTooltip(font,BuiltInRegistries.ITEM.byId(menu.value(4)).getDescription(),mouseX,mouseY);
    }
}
