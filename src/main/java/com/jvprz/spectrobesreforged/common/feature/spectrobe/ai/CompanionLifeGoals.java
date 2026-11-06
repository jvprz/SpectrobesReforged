package com.jvprz.spectrobesreforged.common.feature.spectrobe.ai;

import com.jvprz.spectrobesreforged.common.content.entity.spectrobe.SpectrobeEntity;
import com.jvprz.spectrobesreforged.common.config.Config;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;

/** Server-owned cosmetic behavior. Scanner pause always wins over these goals. */
public final class CompanionLifeGoals {
    private CompanionLifeGoals() {}
    private static final TagKey<Item> MINERALS=TagKey.create(Registries.ITEM,ResourceLocation.fromNamespaceAndPath("spectrobesreforged","minerals"));
    public static boolean offeredMineral(Player owner) {
        return owner!=null && (owner.getMainHandItem().is(MINERALS)||owner.getOffhandItem().is(MINERALS));
    }
    private static Player nearbyOwner(SpectrobeEntity mob, double range) {
        Player owner=mob.getOwner().orElse(null);
        return Config.livingCompanions() && !mob.isRangePaused() && owner!=null && owner.isAlive()
            && !owner.isSpectator() && mob.distanceToSqr(owner)<=range*range ? owner : null;
    }
    private static boolean resting(Player owner) { return owner!=null && owner.getDeltaMovement().horizontalDistanceSqr()<.003 && !owner.isSprinting(); }

    public static final class KomainuEatingPause extends Goal {
        private final SpectrobeEntity mob;
        public KomainuEatingPause(SpectrobeEntity mob){this.mob=mob;setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK));}
        @Override public boolean canUse(){return Config.livingCompanions()&&mob.hasDetailedCompanionRig()&&mob.getReaction()==2&&!mob.isRangePaused();}
        @Override public boolean canContinueToUse(){return canUse();}
        @Override public void start(){mob.getNavigation().stop();}
        @Override public void tick(){mob.getNavigation().stop();}
    }
    public static final class WatchMineral extends Goal {
        private final SpectrobeEntity mob; private Player owner;
        public WatchMineral(SpectrobeEntity mob) { this.mob=mob;setFlags(EnumSet.of(Flag.LOOK)); }
        @Override public boolean canUse() { owner=nearbyOwner(mob,8);return offeredMineral(owner); }
        @Override public boolean canContinueToUse() { return nearbyOwner(mob,8)==owner && offeredMineral(owner); }
        @Override public void start() {mob.setLifeGesture(2);}
        @Override public void tick() {mob.getLookControl().setLookAt(owner,25,20);}
        @Override public void stop() {if(mob.getLifeGesture()==2)mob.setLifeGesture(0);owner=null;}
    }
    /** A single authored hop, with no extra physical impulse or terrain/path changes. */
    public static final class KomainuPlayfulJump extends Goal {
        private final SpectrobeEntity mob;
        private int remaining,nextAllowed;
        public KomainuPlayfulJump(SpectrobeEntity mob) {
            this.mob=mob;
            nextAllowed=400+mob.getRandom().nextInt(401);
            setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK));
        }
        private boolean safeToPlay() {
            var owner=nearbyOwner(mob,4);
            return mob.hasDetailedCompanionRig() && resting(owner) && !offeredMineral(owner)
                    && mob.onGround() && !mob.isInWater() && !mob.isInLava() && !mob.isPassenger()
                    && !mob.hasCarriedMineral() && mob.getReaction()==0 && mob.getNavigation().isDone();
        }
        @Override public boolean canUse() {
            return mob.tickCount>=nextAllowed && safeToPlay() && mob.getLifeGesture()==0 && mob.getRandom().nextInt(40)==0;
        }
        @Override public boolean requiresUpdateEveryTick(){return true;}
        @Override public boolean canContinueToUse(){return remaining>0 && safeToPlay();}
        @Override public void start(){remaining=25;mob.getNavigation().stop();mob.setLifeGesture(3);}
        @Override public void tick(){remaining--;}
        @Override public void stop(){
            if(mob.getLifeGesture()==3)mob.setLifeGesture(0);
            nextAllowed=mob.tickCount+600+mob.getRandom().nextInt(601);
        }
    }
    public static final class CuriousPause extends Goal {
        private final SpectrobeEntity mob; private int remaining,nextAllowed;private Vec3 target;
        public CuriousPause(SpectrobeEntity mob) {this.mob=mob;setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK));}
        @Override public boolean canUse() {
            Player owner=nearbyOwner(mob,4.5);
            return mob.tickCount>=nextAllowed && resting(owner) && !offeredMineral(owner)
                && mob.getNavigation().isDone() && !mob.isInWater() && mob.getRandom().nextInt(40)==0;
        }
        @Override public boolean canContinueToUse() {
            Player owner=nearbyOwner(mob,5);return remaining>0 && resting(owner) && !offeredMineral(owner);
        }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void start() {
            remaining=mob.hasDetailedCompanionRig()?50:24+mob.getRandom().nextInt(20);if(mob.hasDetailedCompanionRig()&&mob.getRandom().nextInt(3)==0)mob.companionCry(.98F);mob.getNavigation().stop();mob.setLifeGesture(1);
            target=mob.position().add(mob.getRandom().nextDouble()*3-1.5,.25,mob.getRandom().nextDouble()*3-1.5);
        }
        @Override public void tick() {remaining--;mob.getLookControl().setLookAt(target.x,target.y,target.z,12,12);}
        @Override public void stop() {
            if(mob.getLifeGesture()==1)mob.setLifeGesture(0);
            nextAllowed=mob.tickCount+CompanionTemperament.of(mob.getSpeciesKey()).curiosityInterval()+mob.getRandom().nextInt(80);
        }
    }
    public static final class NearbyStroll extends Goal {
        private final SpectrobeEntity mob;private Vec3 target;private int remaining,nextAllowed;
        public NearbyStroll(SpectrobeEntity mob) {this.mob=mob;setFlags(EnumSet.of(Flag.MOVE));}
        @Override public boolean canUse() {
            Player owner=nearbyOwner(mob,4);
            if(mob.tickCount<nextAllowed || !resting(owner) || offeredMineral(owner) || mob.isInWater()
                || !mob.getNavigation().isDone() || mob.getRandom().nextInt(45)!=0)return false;
            target=LandRandomPos.getPos(mob,3,2);
            return target!=null && target.distanceToSqr(owner.position())<=16 && target.distanceToSqr(mob.position())>.8
                && mob.level().getFluidState(net.minecraft.core.BlockPos.containing(target)).isEmpty();
        }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void start() {
            remaining=70;mob.getNavigation().moveTo(target.x,target.y,target.z,.65*CompanionTemperament.of(mob.getSpeciesKey()).pace());
        }
        @Override public boolean canContinueToUse() {
            Player owner=nearbyOwner(mob,5);return remaining>0 && resting(owner) && !offeredMineral(owner) && !mob.getNavigation().isDone();
        }
        @Override public void tick() {remaining--;}
        @Override public void stop() {
            mob.getNavigation().stop();nextAllowed=mob.tickCount+CompanionTemperament.of(mob.getSpeciesKey()).strollInterval()+mob.getRandom().nextInt(80);
        }
    }
}
