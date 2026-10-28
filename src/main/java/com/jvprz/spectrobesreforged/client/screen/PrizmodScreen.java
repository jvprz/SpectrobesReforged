package com.jvprz.spectrobesreforged.client.screen;

import com.jvprz.spectrobesreforged.client.feature.prizmod.ClientPrizmodState;
import com.jvprz.spectrobesreforged.client.ui.icon.SpectrobeIcons;
import com.jvprz.spectrobesreforged.common.content.item.MineralItem;
import com.jvprz.spectrobesreforged.common.feature.prizmod.menu.PrizmodMenu;
import com.jvprz.spectrobesreforged.common.feature.training.*;
import com.jvprz.spectrobesreforged.common.network.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import com.jvprz.spectrobesreforged.common.content.entity.spectrobe.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeStage;
import com.jvprz.spectrobesreforged.common.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import java.util.*;

/** Selection and explicit actions keep the collection stable while pages/filters change. */
public final class PrizmodScreen extends AbstractContainerScreen<PrizmodMenu> {
    private static final int PER_PAGE=16;
    private record Hit(int type,int index,ClientPrizmodState.Entry entry){}
    private UUID selected;
    // A single client-only display entity, never inserted into the world or ticked for AI.
    private SpectrobeEntity preview;
    private UUID previewId;
    private String previewSpecies;
    private boolean previewAvailable;
    private int page,filter,feedPage;
    private boolean sortLevel,feeding;
    private EditBox search;
    private Button filterButton,sortButton,previous,next,companion,store,team,feed,back,feedPrevious,feedNext;
    private final List<FeedButton> feedButtons=new ArrayList<>();
    public PrizmodScreen(PrizmodMenu menu,Inventory inventory,Component title){super(menu,inventory,title);imageWidth=366;imageHeight=238;}
    private Component text(String key,Object... args){return Component.translatable("gui.spectrobesreforged.prizmod_ui."+key,args);}
    private Component name(ClientPrizmodState.Entry entry){return SpectrobeNames.name(entry.species(),entry.crowned(),entry.cooperative());}
    private List<Hit> entries(){
        String query=search==null?"":search.getValue().trim().toLowerCase(Locale.ROOT);
        List<Hit> result=new ArrayList<>();
        for(int i=0;i<ClientPrizmodState.BOX.size();i++){
            var e=ClientPrizmodState.BOX.get(i);
            if(!query.isEmpty()&&!e.species().toLowerCase(Locale.ROOT).contains(query)&&!name(e).getString().toLowerCase(Locale.ROOT).contains(query))continue;
            if(filter==4&&!e.crowned())continue;
            if(filter>0&&filter<4&&!e.element().equals(new String[]{"","corona","aurora","flash"}[filter]))continue;
            result.add(new Hit(0,i,e));
        }
        result.sort(sortLevel?Comparator.<Hit>comparingInt(h->h.entry.level()).reversed().thenComparing(h->name(h.entry).getString()):Comparator.comparing(h->name(h.entry).getString()));
        return result;
    }
    private Hit selection(){
        if(selected==null)return null;
        for(int i=0;i<ClientPrizmodState.BOX.size();i++)if(selected.equals(ClientPrizmodState.BOX.get(i).id()))return new Hit(0,i,ClientPrizmodState.BOX.get(i));
        for(int i=0;i<ClientPrizmodState.TEAM.size();i++){var e=ClientPrizmodState.TEAM.get(i);if(e!=null&&selected.equals(e.id()))return new Hit(1,i,e);}
        var baby=ClientPrizmodState.BABY;return baby!=null&&selected.equals(baby.id())?new Hit(2,0,baby):null;
    }
    private int pages(){return Math.max(1,(entries().size()+PER_PAGE-1)/PER_PAGE);}
    private int freeTeam(){for(int i=0;i<6;i++)if(i>=ClientPrizmodState.TEAM.size()||ClientPrizmodState.TEAM.get(i)==null)return i;return -1;}
    private List<Integer> minerals(){
        List<Integer> slots=new ArrayList<>();if(minecraft==null||minecraft.player==null)return slots;
        for(int i=0;i<36;i++){var stack=minecraft.player.getInventory().getItem(i);if(!stack.isEmpty()&&stack.getItem() instanceof MineralItem)slots.add(i);}return slots;
    }
    private void moveTo(int type,int index){var hit=selection();if(hit==null)return;PacketDistributor.sendToServer(new C2SMoveSpectrobe(hit.type,hit.index,type,index,hit.entry.id()));feeding=false;}
    private Button button(String key,int x,int y,int width,Button.OnPress action){return addRenderableWidget(Button.builder(text(key),action).bounds(leftPos+x,topPos+y,width,16).build());}
    @Override protected void init(){
        String query=search==null?"":search.getValue();super.init();feedButtons.clear();
        search=addRenderableWidget(new EditBox(font,leftPos+10,topPos+26,98,18,text("search")));search.setMaxLength(64);search.setHint(text("search"));search.setValue(query);search.setResponder(v->page=0);
        filterButton=button("all",112,26,42,b->{filter=(filter+1)%5;page=0;});
        sortButton=button("sort_name",158,26,26,b->{sortLevel=!sortLevel;page=0;});
        previous=button("previous",10,186,22,b->page=Math.max(0,page-1));
        next=button("next",162,186,22,b->page=Math.min(pages()-1,page+1));
        companion=button("companion",196,158,76,b->moveTo(2,0));
        store=button("store",276,158,76,b->moveTo(0,Integer.MAX_VALUE));
        team=button("team",196,179,76,b->{int slot=freeTeam();if(slot>=0)moveTo(1,slot);});
        feed=button("feed",276,179,76,b->{feeding=true;feedPage=0;});
        back=button("back",196,184,78,b->feeding=false);
        feedPrevious=button("previous",280,184,32,b->feedPage=Math.max(0,feedPage-1));
        feedNext=button("next",316,184,36,b->feedPage++);
        for(int i=0;i<6;i++)feedButtons.add(addRenderableWidget(new FeedButton(leftPos+196,topPos+53+i*21,i)));
        refresh();
    }
    private final class FeedButton extends Button {
        private final int row;
        FeedButton(int x,int y,int row){super(x,y,156,20,text("feed"),b->sendFeed(row),DEFAULT_NARRATION);this.row=row;}
        private ItemStack stack(){var slots=minerals();int at=feedPage*6+row;return at<slots.size()?minecraft.player.getInventory().getItem(slots.get(at)):ItemStack.EMPTY;}
        @Override protected void renderWidget(GuiGraphics g,int mx,int my,float partial){
            var stack=stack();g.fill(getX(),getY(),getX()+width,getY()+height,isHoveredOrFocused()?0xff3b5865:0xff293d48);g.renderItem(stack,getX()+2,getY()+2);
            g.drawString(font,font.plainSubstrByWidth(stack.getHoverName().getString(),104),getX()+23,getY()+6,0xffe5edf0,false);
            g.drawString(font,Component.literal("×"+stack.getCount()),getX()+132,getY()+6,0xff9dc8db,false);
        }
    }
    private void sendFeed(int row){
        var hit=selection();var slots=minerals();int at=feedPage*6+row;if(hit==null||at>=slots.size())return;
        int slot=slots.get(at);var stack=minecraft.player.getInventory().getItem(slot);
        PacketDistributor.sendToServer(new C2SFeedSpectrobe(hit.entry.id(),slot,BuiltInRegistries.ITEM.getId(stack.getItem())));
    }
    private void refresh(){
        page=Math.min(page,pages()-1);var selected=selection();if(selected==null)feeding=false;
        filterButton.setMessage(filter==0?text("all"):filter==4?Component.literal("♛"):Component.translatable("tooltip.spectrobesreforged.type."+new String[]{"","corona","aurora","flash"}[filter]));
        filterButton.setTooltip(Tooltip.create(text("filter_hint")));
        sortButton.setMessage(text(sortLevel?"sort_level":"sort_name"));sortButton.setTooltip(Tooltip.create(text("sort_hint")));
        previous.active=page>0;next.active=page<pages()-1;
        for(var button:List.of(companion,store,team,feed))button.visible=!feeding;
        boolean child=selected!=null&&selected.entry.stage().equalsIgnoreCase("CHILD");
        companion.active=selected!=null&&child&&selected.type!=2&&selected.entry.hpCur()>0;
        store.active=selected!=null&&selected.type!=0;
        team.active=selected!=null&&!child&&selected.type!=1&&freeTeam()>=0&&selected.entry.hpCur()>0;
        team.setTooltip(Tooltip.create(text("team_hint")));
        feed.active=selected!=null&&selected.entry.hpCur()>0&&!minerals().isEmpty();
        back.visible=feedPrevious.visible=feedNext.visible=feeding;
        int count=minerals().size(),last=Math.max(0,(count-1)/6);feedPage=Math.min(feedPage,last);
        feedPrevious.active=feedPage>0;feedNext.active=feedPage<last;
        for(int i=0;i<feedButtons.size();i++){feedButtons.get(i).visible=feeding&&feedPage*6+i<count;feedButtons.get(i).active=selected!=null;}
    }
    @Override protected void containerTick(){
        super.containerTick();refresh();
        if(preview!=null){preview.setPreviewAnimationFrozen(com.jvprz.spectrobesreforged.common.config.ClientConfig.reducedMotion());
            if(!com.jvprz.spectrobesreforged.common.config.ClientConfig.reducedMotion())preview.tickCount++;}
    }
    @Override public void removed(){
        preview=null;previewId=null;previewSpecies=null;
        super.removed();
    }
    private boolean inside(double mx,double my,int x,int y,int w,int h){return mx>=leftPos+x&&mx<leftPos+x+w&&my>=topPos+y&&my<topPos+y+h;}
    private Hit hit(double mx,double my){
        var list=entries();for(int slot=0;slot<16;slot++){int at=page*16+slot;if(at<list.size()&&inside(mx,my,10+slot%4*44,58+slot/4*30,42,28))return list.get(at);}
        if(inside(mx,my,10,207,174,27)&&ClientPrizmodState.BABY!=null)return new Hit(2,0,ClientPrizmodState.BABY);
        for(int i=0;i<6;i++)if(i<ClientPrizmodState.TEAM.size()&&ClientPrizmodState.TEAM.get(i)!=null&&inside(mx,my,196+i*26,216,22,18))return new Hit(1,i,ClientPrizmodState.TEAM.get(i));return null;
    }
    @Override public boolean mouseClicked(double x,double y,int button){
        if(button==0){var hit=hit(x,y);if(hit!=null){selected=hit.entry.id();feeding=false;search.setFocused(false);refresh();return true;}}
        return super.mouseClicked(x,y,button);
    }
    @Override public boolean keyPressed(int key,int scanCode,int modifiers){
        if(search.isFocused()){if(key==GLFW.GLFW_KEY_ESCAPE){search.setFocused(false);return true;}search.keyPressed(key,scanCode,modifiers);return true;}
        if(feeding&&key==GLFW.GLFW_KEY_ESCAPE){feeding=false;return true;}
        return super.keyPressed(key,scanCode,modifiers);
    }
    private void crown(GuiGraphics g,int x,int y){
        g.fill(x,y+2,x+8,y+6,0xffffd35c);g.fill(x,y,x+2,y+3,0xffffeaa0);g.fill(x+3,y-1,x+5,y+3,0xffffeaa0);g.fill(x+6,y,x+8,y+3,0xffffeaa0);g.fill(x+1,y+6,x+7,y+7,0xffc48d30);
    }
    private void icon(GuiGraphics g,ClientPrizmodState.Entry e,int x,int y,int size){if(e!=null)g.blit(SpectrobeIcons.icon(e.species(),e.color()),x,y,0,0,size,size,size,size);}
    private void renderPreview(GuiGraphics g,ClientPrizmodState.Entry entry,int x,int y,int mouseX,int mouseY){
        g.fill(x,y,x+56,y+40,0xff15232d);
        g.fill(x+10,y+36,x+46,y+38,0xff304959);
        if(minecraft==null||minecraft.level==null)return;
        if(!entry.id().equals(previewId)||!entry.species().equals(previewSpecies)
                ||(preview!=null&&preview.level()!=minecraft.level)){
            preview=null;previewId=entry.id();previewSpecies=entry.species();previewAvailable=false;
            // Unknown/datapack-only species may not have client models: keep a safe portrait fallback.
            String prefix="spectrobesreforged:";
            var resources=minecraft.getResourceManager();
            previewAvailable=ResourceLocation.tryParse(prefix+"geo/entity/spectrobe/"+entry.species()+".geo.json")!=null
                    && resources.getResource(ResourceLocation.parse(prefix+"geo/entity/spectrobe/"+entry.species()+".geo.json")).isPresent()
                    && resources.getResource(ResourceLocation.parse(prefix+"animations/entity/spectrobe/"+entry.species()+".animation.json")).isPresent();
            if(previewAvailable){
                preview=ModEntities.SPECTROBE.get().create(minecraft.level);
                if(preview!=null){
                    preview.setSpeciesKey(entry.species());preview.setNoAi(true);preview.setSilent(true);
                    preview.setNoGravity(true);preview.setOnGround(true);
                    preview.tickCount=minecraft.player==null?0:minecraft.player.tickCount;
                }
            }
        }
        var texture=ResourceLocation.tryParse("spectrobesreforged:textures/entity/spectrobe/"+entry.species()+"/"+entry.species()+"_color_0.png");
        if(preview==null||!previewAvailable||texture==null||minecraft.getResourceManager().getResource(texture).isEmpty()){
            icon(g,entry,x+12,y+4,32);return;
        }
        boolean reduced=com.jvprz.spectrobesreforged.common.config.ClientConfig.reducedMotion();
        preview.setPreviewAnimationFrozen(reduced);
        preview.setTextureVariant(entry.color());
        try{preview.setStage(SpectrobeStage.valueOf(entry.stage().toUpperCase(Locale.ROOT)));}
        catch(IllegalArgumentException unknownStage){preview.setStage(SpectrobeStage.CHILD);}
        // Vanilla's inventory renderer supplies lighting, clipping and mouse-following orientation.
        // Clamp the cursor to avoid extreme poses while clicking controls elsewhere on the screen.
        InventoryScreen.renderEntityInInventoryFollowsMouse(g,x+1,y+1,x+55,y+39,36,-0.08F,
                reduced?x+28:Mth.clamp(mouseX,x-12,x+68),reduced?y+20:Mth.clamp(mouseY,y-8,y+48),preview);
    }
    private void panel(GuiGraphics g,int x,int y,int w,int h){g.fill(leftPos+x,topPos+y,leftPos+x+w,topPos+y+h,0xff1c2a34);g.renderOutline(leftPos+x,topPos+y,w,h,0xff394e5b);}
    private void card(GuiGraphics g,ClientPrizmodState.Entry e,int x,int y){
        panel(g,x,y,42,28);if(e==null)return;
        if(e.id().equals(selected))g.renderOutline(leftPos+x,topPos+y,42,28,0xff83d2f2);
        icon(g,e,leftPos+x+3,topPos+y+2,20);
        g.drawString(font,Component.literal(""+e.level()),leftPos+x+25,topPos+y+17,0xffc4dae4,false);
        if(e.crowned())crown(g,leftPos+x+29,topPos+y+4);
        if(e.cooperative())g.drawString(font,"⚒",leftPos+x+5,topPos+y+4,0xff7ee8e3,false);
        int color=e.hpCur()==0?0xffd36b67:0xff73cfa9;g.fill(leftPos+x+3,topPos+y+24,leftPos+x+3+(int)(35L*e.hpCur()/Math.max(1,e.hp())),topPos+y+26,color);
    }
    @Override protected void renderBg(GuiGraphics g,float partial,int mouseX,int mouseY){
        g.fill(leftPos-2,topPos-2,leftPos+imageWidth+2,topPos+imageHeight+2,0xff101921);g.fill(leftPos,topPos,leftPos+imageWidth,topPos+imageHeight,0xff25343f);
        g.drawString(font,Component.translatable("gui.spectrobesreforged.prizmod"),leftPos+10,topPos+8,0xffd7edf6,false);
        int total=ClientPrizmodState.BOX.size()+(int)ClientPrizmodState.TEAM.stream().filter(Objects::nonNull).count()+(ClientPrizmodState.BABY==null?0:1);
        g.drawString(font,text("count",total),leftPos+196,topPos+8,0xff9eb6c5,false);
        g.drawString(font,text("collection"),leftPos+10,topPos+48,0xff9eb6c5,false);
        var entries=entries();for(int slot=0;slot<16;slot++){int at=page*16+slot;card(g,at<entries.size()?entries.get(at).entry:null,10+slot%4*44,58+slot/4*30);}
        if(entries.isEmpty())g.drawWordWrap(font,text("no_results"),leftPos+19,topPos+101,151,0xffa8bbc6);
        g.drawCenteredString(font,text("page",page+1,pages()),leftPos+96,topPos+190,0xffc3d8e3);
        panel(g,190,24,168,178);
        var selection=selection();
        if(selection==null)g.drawWordWrap(font,text("select"),leftPos+203,topPos+64,141,0xffb5c8d4);
        else renderDetails(g,selection.entry,mouseX,mouseY);
        panel(g,10,207,174,27);g.drawString(font,text("companion"),leftPos+15,topPos+210,0xff8db7cc,false);
        var baby=ClientPrizmodState.BABY;
        g.drawString(font,font.plainSubstrByWidth(baby==null?text("empty").getString():name(baby).getString(),130),leftPos+15,topPos+222,0xffdbe8ee,false);icon(g,baby,leftPos+161,topPos+213,16);
        g.drawString(font,text("team_label"),leftPos+196,topPos+205,0xff8db7cc,false);
        for(int i=0;i<6;i++){int x=196+i*26;panel(g,x,216,22,18);var entry=i<ClientPrizmodState.TEAM.size()?ClientPrizmodState.TEAM.get(i):null;icon(g,entry,leftPos+x+3,topPos+217,16);if(entry!=null&&entry.crowned())crown(g,leftPos+x+13,topPos+216);}
    }
    private void renderDetails(GuiGraphics g,ClientPrizmodState.Entry e,int mouseX,int mouseY){
        int x=leftPos+198,y=topPos;
        String title=font.plainSubstrByWidth(SpectrobeNames.name(e.species(),false).getString(),135);
        g.drawString(font,title,x,y+29,0xffe1f0f5,false);if(e.crowned())crown(g,x+font.width(title)+4,y+30);
        if(e.cooperative())g.drawString(font,"⚒",x+font.width(title)+(e.crowned()?17:4),y+30,0xff7ee8e3,false);
        if(feeding){g.drawString(font,text("choose_mineral"),x,y+42,0xffa3c8da,false);if(minerals().isEmpty())g.drawWordWrap(font,text("no_minerals"),x,y+73,145,0xffb5c8d4);return;}
        g.drawString(font,Component.empty().append(Component.translatable("spectrobe.stage."+e.stage().toLowerCase(Locale.ROOT))).append(" · ").append(e.element().equals("unknown")?Component.literal("?"):Component.translatable("tooltip.spectrobesreforged.type."+e.element())),x,y+43,0xff9eb6c5,false);
        renderPreview(g,e,x,y+52,mouseX,mouseY);
        g.drawString(font,text("level",e.level()),x+60,y+59,0xff9cdbf7,false);
        g.drawString(font,e.quality()<0?text("quality_unknown"):text("quality",SpectrobeLevels.qualityPercent(e.quality())),x+60,y+73,0xffdfcf8e,false);
        g.drawString(font,text("health",e.hpCur(),e.hp()),x,y+94,0xff9fd8b9,false);
        g.drawString(font,text("stats",e.atk(),e.def()),x,y+107,0xffd1dce4,false);
        int needed=SpectrobeLevels.required(e.level());
        g.drawString(font,needed==0?text("max_level"):text("xp",e.xp(),needed),x,y+120,0xffa8cfe4,false);
        g.fill(x,y+131,x+150,y+135,0xff0e1b24);g.fill(x,y+131,x+(needed==0?150:(int)Math.min(150,150L*e.xp()/needed)),y+135,0xff69bce5);
        g.drawString(font,text("counters",e.mineralsFed(),e.battles()),x,y+142,0xff93a9b7,false);
    }
    @Override protected void renderLabels(GuiGraphics g,int x,int y){}
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partial){
        super.render(g,mouseX,mouseY,partial);
        var hit=hit(mouseX,mouseY);if(hit!=null)g.renderTooltip(font,List.of(name(hit.entry),text("level",hit.entry.level()),text("health",hit.entry.hpCur(),hit.entry.hp()),text("quality",SpectrobeLevels.qualityPercent(hit.entry.quality())),text("evolution_pending")),Optional.empty(),mouseX,mouseY);
        for(var button:feedButtons)if(button.visible&&button.isHovered()){
            var stack=button.stack();var selected=selection();
            List<Component> lines=new ArrayList<>();lines.add(stack.getHoverName());
            lines.add(selected!=null&&selected.entry.level()>=100?text("max_level"):text("mineral_xp",SpectrobeLevels.mineralPercent(BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath())));
            lines.add(text("one_mineral"));g.renderTooltip(font,lines,Optional.empty(),mouseX,mouseY);
        }
    }
}
