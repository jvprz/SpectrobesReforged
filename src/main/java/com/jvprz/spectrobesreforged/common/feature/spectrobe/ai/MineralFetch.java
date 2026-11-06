package com.jvprz.spectrobesreforged.common.feature.spectrobe.ai;

import com.jvprz.spectrobesreforged.common.config.Config;
import com.jvprz.spectrobesreforged.common.content.entity.spectrobe.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.content.item.MineralItem;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeStage;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.registry.SpectrobeSpeciesRegistry;
import com.jvprz.spectrobesreforged.common.registry.ModAttachments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import java.util.Comparator;
import java.util.EnumSet;

/** Single-item fetching, validated exclusively on the server thread. */
public final class MineralFetch {
    private MineralFetch() {}
    public static ServerPlayer linkedOwner(SpectrobeEntity mob) {
        if(mob.getStage()!=SpectrobeStage.CHILD || !(mob.getOwner().orElse(null) instanceof ServerPlayer owner)
                || !owner.isAlive() || owner.isSpectator() || !mob.getPersistentData().hasUUID("SpectrobeId"))return null;
        var id=mob.getPersistentData().getUUID("SpectrobeId");
        return owner.getData(ModAttachments.PRIZMOD.get()).getBabySlot().filter(e->e.id().equals(id)).isPresent()?owner:null;
    }
    public static double range(SpectrobeEntity mob) {
        var species=SpectrobeSpeciesRegistry.getByKey(mob.getSpeciesKey());return species==null?0:species.range()*2.0;
    }
    public static boolean eligible(ItemEntity item,ServerPlayer owner) {
        return item.isAlive() && !item.hasPickUpDelay() && !item.getItem().isEmpty() && item.getItem().getItem() instanceof MineralItem
            && (item.getTarget()==null || item.getTarget().equals(owner.getUUID()));
    }
    public static boolean pickUp(SpectrobeEntity mob,ItemEntity item) {
        if(mob.level().isClientSide || !Config.livingCompanions() || !Config.fetchMinerals() || mob.isRangePaused() || mob.hasCarriedMineral())return false;
        var owner=linkedOwner(mob);if(owner==null || !eligible(item,owner) || mob.distanceToSqr(item)>2.25)return false;
        if(mob.level().clip(new ClipContext(mob.getEyePosition(),item.position().add(0,.1,0),ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,mob)).getType()!=HitResult.Type.MISS)return false;
        var remainder=item.getItem().copy();var taken=remainder.split(1);
        if(remainder.isEmpty())item.discard();else item.setItem(remainder);
        mob.setItemSlot(EquipmentSlot.MAINHAND,taken);mob.setDropChance(EquipmentSlot.MAINHAND,0);
        mob.resetCarriedWait();react(mob,false);mob.companionCry(1.06F);return true;
    }
    public static void react(SpectrobeEntity mob,boolean hearts) {
        if(hearts && !mob.level().isClientSide)mob.reactToCare(false);
        if(mob.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,mob.getX(),mob.getY()+.7,mob.getZ(),5,.22,.18,.22,.01);
            if(hearts)level.sendParticles(ParticleTypes.HEART,mob.getX(),mob.getY()+.85,mob.getZ(),2,.15,.12,.15,0);
        }
    }
    public static final class FetchGoal extends Goal {
        private final SpectrobeEntity mob;private ItemEntity target;private int nextSearch,repath,timeout;
        public FetchGoal(SpectrobeEntity mob) {this.mob=mob;setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK));}
        @Override public boolean requiresUpdateEveryTick() {return true;}
        @Override public boolean canUse() {
            var owner=linkedOwner(mob);
            if(!Config.livingCompanions() || !Config.fetchMinerals() || mob.isRangePaused() || owner==null || mob.distanceToSqr(owner)>1024)return false;
            if(mob.hasCarriedMineral())return true;
            if(mob.tickCount<nextSearch)return false;nextSearch=mob.tickCount+10;
            double range=range(mob);target=mob.level().getEntitiesOfClass(ItemEntity.class,mob.getBoundingBox().inflate(range),e->eligible(e,owner)&&mob.distanceToSqr(e)<=range*range)
                .stream().sorted(Comparator.comparingDouble(mob::distanceToSqr)).limit(8).filter(e->{var path=mob.getNavigation().createPath(e,0);return path!=null&&path.canReach();}).findFirst().orElse(null);
            return target!=null;
        }
        @Override public boolean canContinueToUse() {
            var owner=linkedOwner(mob);if(mob.isRangePaused() || !Config.livingCompanions() || !Config.fetchMinerals() || owner==null || mob.distanceToSqr(owner)>1024)return false;
            return mob.hasCarriedMineral() || timeout>0&&target!=null&&eligible(target,owner)&&mob.distanceToSqr(target)<=range(mob)*range(mob);
        }
        @Override public void start(){repath=0;timeout=160;}
        @Override public void tick() {
            var owner=linkedOwner(mob);if(owner==null)return;
            if(mob.hasCarriedMineral()) {
                mob.getLookControl().setLookAt(owner,20,15);
                if(mob.distanceToSqr(owner)<=4){mob.getNavigation().stop();return;}
                if(--repath<=0){repath=8;mob.getNavigation().moveTo(owner,.95);}return;
            }
            timeout--;if(target==null)return;
            mob.getLookControl().setLookAt(target,20,15);
            if(pickUp(mob,target)){target=null;repath=0;return;}
            if(--repath<=0){repath=8;mob.getNavigation().moveTo(target,.85);}
        }
        @Override public void stop(){target=null;mob.getNavigation().stop();}
    }
}
