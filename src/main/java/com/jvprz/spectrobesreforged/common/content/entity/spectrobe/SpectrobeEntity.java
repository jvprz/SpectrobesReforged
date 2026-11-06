package com.jvprz.spectrobesreforged.common.content.entity.spectrobe;

import com.jvprz.spectrobesreforged.common.feature.spectrobe.data.SpectrobeStage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Locale;
import java.util.EnumSet;
import com.jvprz.spectrobesreforged.common.config.Config;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.CompanionLifeGoals;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.CompanionTemperament;
import java.util.Optional;
import java.util.UUID;

public class SpectrobeEntity extends PathfinderMob implements GeoEntity {

    private static final EntityDataAccessor<String> SPECIES_KEY =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<String> STAGE =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Boolean> RANGE_PAUSED =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> RANGE_PAUSE_TICKS =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> LIFE_GESTURE =
            SynchedEntityData.defineId(SpectrobeEntity.class, EntityDataSerializers.INT);
    public int getLifeGesture() { return entityData.get(LIFE_GESTURE); }
    public void setLifeGesture(int gesture) { int old=getLifeGesture();entityData.set(LIFE_GESTURE, Math.clamp(gesture,0,3));if(gesture==2&&old!=2)companionCry(1.02F); }

    private static final EntityDataAccessor<Integer> REACTION = SynchedEntityData.defineId(SpectrobeEntity.class,EntityDataSerializers.INT);
    private int reactionTicks;
    public boolean hasDetailedCompanionRig() { return "komainu".equals(getSpeciesKey()) || "spiko".equals(getSpeciesKey()); }
    private net.minecraft.sounds.SoundEvent companionCrySound() {
        return ("spiko".equals(getSpeciesKey()) ? com.jvprz.spectrobesreforged.common.registry.ModSounds.SPIKO_CRY : com.jvprz.spectrobesreforged.common.registry.ModSounds.KOMAINU_CRY).get();
    }
    private int nextCompanionCry;
    private boolean companionRunning,blinkPlaying;
    private double nextBlinkAt=Double.NaN,blinkUntil;
    private static final RawAnimation RUN=RawAnimation.begin().thenLoop("run"), BLINK=RawAnimation.begin().thenPlay("blink"),
        DETAILED_INTEREST=RawAnimation.begin().thenPlayAndHold("interested"),
        DETAILED_JUMP=RawAnimation.begin().thenPlay("jump"), DETAILED_CRY=RawAnimation.begin().thenPlay("cry");
    public int getReaction() {return entityData.get(REACTION);}
    public void reactToCare(boolean eating) {entityData.set(REACTION,eating?2:1);reactionTicks=eating?24:(hasDetailedCompanionRig()?25:20);companionCry(eating?1.04F:1.14F);}
    public void companionCry(float pitch) {companionCry(pitch,.32F);}
    private void companionCry(float pitch,float volume) {
        if(!hasDetailedCompanionRig() || level().isClientSide || isSilent() || tickCount<nextCompanionCry)return;
        nextCompanionCry=tickCount+80;
        triggerAnim("voice","cry");
        level().playSound(null,getX(),getY(),getZ(),companionCrySound(),
                net.minecraft.sounds.SoundSource.NEUTRAL,volume,pitch+(random.nextFloat()-.5F)*.10F);
    }
    @Override public void playAmbientSound() {
        if(!hasDetailedCompanionRig()){super.playAmbientSound();return;}
        if(!isRangePaused()&&getReaction()==0)companionCry(.96F,.22F);
    }
    @Override protected net.minecraft.sounds.SoundEvent getAmbientSound() {
        if(!hasDetailedCompanionRig())return super.getAmbientSound();
        return tickCount<nextCompanionCry || isRangePaused() || getReaction()!=0?null:companionCrySound();
    }
    @Override public int getAmbientSoundInterval() {return hasDetailedCompanionRig()?600:super.getAmbientSoundInterval();}
    @Override protected float getSoundVolume() {return hasDetailedCompanionRig()?.24F:super.getSoundVolume();}
    @Override public float getVoicePitch() {return hasDetailedCompanionRig()?.94F+random.nextFloat()*.12F:super.getVoicePitch();}
    private static final java.util.Set<String> EXPRESSIVE=java.util.Set.of("komainu","spiko","vilar","segu","inkana","shakin","zoza");
    private static final RawAnimation CURIOUS=RawAnimation.begin().thenLoop("curious"), INTERESTED=RawAnimation.begin().thenLoop("interested"),
        CARRY=RawAnimation.begin().thenLoop("carry"), HAPPY=RawAnimation.begin().thenLoop("happy"), EAT=RawAnimation.begin().thenLoop("eat");

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation SNIFF = RawAnimation.begin().thenLoop("sniff");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private UUID ownerUUID;
    private int carriedWait;
    private int nextAffectionTick;

    private double pausedX;
    private double pausedY;
    private double pausedZ;

    public static final int DEFAULT_RANGE_PAUSE_TICKS = 66;

    private boolean clientScanHasTarget = false;
    private boolean lastSniffingState = false;

    public SpectrobeEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    /** Children are exploration companions; only combat forms can take damage. */
    @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        return getStage() != SpectrobeStage.CHILD && super.hurt(source, amount);
    }
    @Override public boolean isInvulnerableTo(net.minecraft.world.damagesource.DamageSource source) {
        return getStage() == SpectrobeStage.CHILD || super.isInvulnerableTo(source);
    }
    @Override public boolean fireImmune() { return getStage() == SpectrobeStage.CHILD || super.fireImmune(); }
    @Override public boolean isOnFire() { return getStage() != SpectrobeStage.CHILD && super.isOnFire(); }
    @Override public boolean causeFallDamage(float distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return getStage() != SpectrobeStage.CHILD && super.causeFallDamage(distance, multiplier, source);
    }

    protected String getDefaultSpeciesKey() {
        return "unknown";
    }

    protected SpectrobeStage getDefaultStage() {
        return SpectrobeStage.CHILD;
    }

    protected int getTextureVariantCount() {
        return 3;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPECIES_KEY, safeKey(getDefaultSpeciesKey()));
        builder.define(TEXTURE_VARIANT, 0);
        builder.define(STAGE, getDefaultStage().name());
        builder.define(RANGE_PAUSED, false);
        builder.define(RANGE_PAUSE_TICKS, 0);
        builder.define(LIFE_GESTURE, 0);
        builder.define(REACTION,0);
    }

    public String getSpeciesKey() {
        return this.entityData.get(SPECIES_KEY);
    }

    public void setSpeciesKey(String key) {
        this.entityData.set(SPECIES_KEY, safeKey(key));
    }

    public int getTextureVariant() {
        return this.entityData.get(TEXTURE_VARIANT);
    }

    public void setTextureVariant(int variant) {
        int maxIdx = Math.max(0, getTextureVariantCount() - 1);
        this.entityData.set(TEXTURE_VARIANT, Mth.clamp(variant, 0, maxIdx));
    }

    public SpectrobeStage getStage() {
        String raw = this.entityData.get(STAGE);
        try {
            return SpectrobeStage.valueOf(raw);
        } catch (Exception ignored) {
            return SpectrobeStage.CHILD;
        }
    }

    public void setStage(SpectrobeStage stage) {
        this.entityData.set(STAGE, (stage == null ? SpectrobeStage.CHILD : stage).name());
    }

    public boolean isRangePaused() {
        return this.entityData.get(RANGE_PAUSED);
    }

    public void setRangePaused(boolean paused) {
        this.entityData.set(RANGE_PAUSED, paused);
    }

    public int getRangePauseTicks() {
        return this.entityData.get(RANGE_PAUSE_TICKS);
    }

    public void setRangePauseTicks(int ticks) {
        this.entityData.set(RANGE_PAUSE_TICKS, Math.max(0, ticks));
    }

    public boolean shouldRenderRangeCircle() {
        return isRangePaused() && getRangePauseTicks() > 0;
    }

    public Vec3 getPausedCenter() {
        return new Vec3(pausedX, pausedY, pausedZ);
    }

    public void triggerRangePause() {
        triggerRangePause(DEFAULT_RANGE_PAUSE_TICKS);
    }

    public void triggerRangePause(int ticks) {
        this.pausedX = getX();
        this.pausedY = getY();
        this.pausedZ = getZ();

        setLifeGesture(0);
        setRangePaused(true);
        setRangePauseTicks(ticks);

        getNavigation().stop();
        setDeltaMovement(Vec3.ZERO);
    }

    public void stopRangePause() {
        setRangePaused(false);
        setRangePauseTicks(0);
    }

    public void setOwner(Player player) {
        this.ownerUUID = player == null ? null : player.getUUID();
    }

    public Optional<Player> getOwner() {
        if (ownerUUID == null) return Optional.empty();
        return Optional.ofNullable(level().getPlayerByUUID(ownerUUID));
    }

    public void setClientScanHasTarget(boolean hasTarget) {
        if (!level().isClientSide) {
            return;
        }
        this.clientScanHasTarget = hasTarget;
    }

    public boolean isClientScanDetecting() {
        return level().isClientSide && clientScanHasTarget;
    }

    private boolean isSniffingNow() {
        return this.isRangePaused() && this.isClientScanDetecting();
    }

    private static String safeKey(String key) {
        if (key == null) return "unknown";
        String k = key.trim().toLowerCase(Locale.ROOT);
        return k.isEmpty() ? "unknown" : k;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        configureStageGoals(getStage());
    }

    protected void configureStageGoals(SpectrobeStage stage) {
        if (stage == null) stage = SpectrobeStage.CHILD;

        switch (stage) {
            case CHILD -> configureChildGoals();
            case ADULT -> configureAdultGoals();
            case EVOLVED -> configureEvolvedGoals();
        }
    }

    protected void configureChildGoals() {
        this.goalSelector.addGoal(2, new HoldRangePauseGoal(this));
        this.goalSelector.addGoal(2, new CompanionLifeGoals.KomainuEatingPause(this));
        this.goalSelector.addGoal(2, new com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.FetchGoal(this));
        this.goalSelector.addGoal(3, new CompanionLifeGoals.WatchMineral(this));
        this.goalSelector.addGoal(3, new FollowOwnerSideGoal(this, 1.2));
        this.goalSelector.addGoal(4, new CompanionLifeGoals.KomainuPlayfulJump(this));
        this.goalSelector.addGoal(4, new CompanionLifeGoals.CuriousPause(this));
        this.goalSelector.addGoal(5, new CompanionLifeGoals.NearbyStroll(this));
        this.goalSelector.addGoal(4, new SpectrobeLookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new SpectrobeRandomLookAroundGoal(this));
    }

    protected void configureAdultGoals() {
        configureChildGoals();
    }

    protected void configureEvolvedGoals() {
        configureAdultGoals();
    }

    private static class SpectrobeLookAtPlayerGoal extends LookAtPlayerGoal {
        private final SpectrobeEntity spectrobe;

        public SpectrobeLookAtPlayerGoal(SpectrobeEntity spectrobe, Class<? extends Player> lookAtType, float lookDistance) {
            super(spectrobe, lookAtType, lookDistance);
            this.spectrobe = spectrobe;
        }

        @Override
        public boolean canUse() {
            return !spectrobe.isRangePaused() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !spectrobe.isRangePaused() && super.canContinueToUse();
        }

        @Override
        public void stop() {
            super.stop();
            spectrobe.setXRot(0.0F);
        }
    }

    private static class SpectrobeRandomLookAroundGoal extends RandomLookAroundGoal {
        private final SpectrobeEntity spectrobe;

        public SpectrobeRandomLookAroundGoal(SpectrobeEntity spectrobe) {
            super(spectrobe);
            this.spectrobe = spectrobe;
        }

        @Override
        public boolean canUse() {
            return !spectrobe.isRangePaused() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !spectrobe.isRangePaused() && super.canContinueToUse();
        }
    }

    public void refreshGoalsForCurrentStage() {
        this.goalSelector.getAvailableGoals().clear();
        this.targetSelector.getAvailableGoals().clear();
        registerGoals();
    }

    @Override
    public void tick() {
        if (getStage() == SpectrobeStage.CHILD) { clearFire(); setAirSupply(getMaxAirSupply()); }
        super.tick();
        if(!level().isClientSide){if(reactionTicks>0 && --reactionTicks==0)entityData.set(REACTION,0);tickCarriedMineral();}

        if (level().isClientSide && !isRangePaused()) {
            clientScanHasTarget = false;
        }

        if (!level().isClientSide && isRangePaused()) {
            int ticks = getRangePauseTicks();

            if (ticks > 0) {
                setRangePauseTicks(ticks - 1);
                getNavigation().stop();
                setDeltaMovement(Vec3.ZERO);
            }

            if (getRangePauseTicks() <= 0) {
                stopRangePause();
            }
        }
    }

    public boolean hasCarriedMineral() {return !getMainHandItem().isEmpty() && getMainHandItem().getItem() instanceof com.jvprz.spectrobesreforged.common.content.item.MineralItem;}
    public void resetCarriedWait() {carriedWait=0;}
    private void tickCarriedMineral() {
        if(!hasCarriedMineral()){carriedWait=0;return;}
        if(isRangePaused() || !Config.livingCompanions() || !Config.fetchMinerals() || carriedWait<0)return;
        var owner=com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.linkedOwner(this);
        if(owner==null || distanceToSqr(owner)>9)return;
        if(++carriedWait<Config.mineralPatienceTicks())return;
        var food=getMainHandItem().copyWithCount(1);
        var mineral=(com.jvprz.spectrobesreforged.common.content.item.MineralItem)food.getItem();
        if(mineral.feedTo(owner,getPersistentData().getUUID("SpectrobeId"),food,this)) {
            setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,net.minecraft.world.item.ItemStack.EMPTY);
            carriedWait=0;
        }else carriedWait=-1; // No effect: keep it for retrieval instead of destroying it or spamming retries.
    }
    public boolean claimCarriedMineral(net.minecraft.server.level.ServerPlayer player) {
        if(!hasCarriedMineral() || getOwner().map(p->!p.getUUID().equals(player.getUUID())).orElse(true) || distanceToSqr(player)>16)return false;
        carriedWait=0;
        var copy=getMainHandItem().copy();boolean room=player.getInventory().getFreeSlot()>=0;
        for(int i=0;i<36&&!room;i++){var slot=player.getInventory().getItem(i);room=net.minecraft.world.item.ItemStack.isSameItemSameComponents(slot,copy)&&slot.getCount()<slot.getMaxStackSize();}
        if(!room || !player.getInventory().add(copy)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.spectrobesreforged.carried_inventory_full"),true);
            if(tickCount>=nextAffectionTick){nextAffectionTick=tickCount+40;com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.react(this,true);}
            return false;
        }
        setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,net.minecraft.world.item.ItemStack.EMPTY);carriedWait=0;
        player.getInventory().setChanged();com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.react(this,true);return true;
    }
    @Override protected net.minecraft.world.InteractionResult mobInteract(Player player,net.minecraft.world.InteractionHand hand) {
        if(hasCarriedMineral()) {
            if(!level().isClientSide && player instanceof net.minecraft.server.level.ServerPlayer server)claimCarriedMineral(server);
            return net.minecraft.world.InteractionResult.sidedSuccess(level().isClientSide);
        }
        if(Config.livingCompanions() && hand==net.minecraft.world.InteractionHand.MAIN_HAND && player.getItemInHand(hand).isEmpty()
            && getOwner().map(p->p.getUUID().equals(player.getUUID())).orElse(false)) {
            if(!level().isClientSide && tickCount>=nextAffectionTick){nextAffectionTick=tickCount+40;com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.MineralFetch.react(this,true);}
            return net.minecraft.world.InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player,hand);
    }
    @Override public void remove(net.minecraft.world.entity.Entity.RemovalReason reason) {
        if(!level().isClientSide && reason.shouldDestroy() && hasCarriedMineral()) {
            var stack=getMainHandItem().copy();setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,net.minecraft.world.item.ItemStack.EMPTY);
            var dropped=new net.minecraft.world.entity.item.ItemEntity(level(),getX(),getY()+.2,getZ(),stack);
            dropped.setTarget(ownerUUID);dropped.setPickUpDelay(10);level().addFreshEntity(dropped);
        }
        super.remove(reason);
    }

    private boolean previewAnimationFrozen;
    public boolean isPreviewAnimationFrozen() { return previewAnimationFrozen; }
    public void setPreviewAnimationFrozen(boolean frozen) { previewAnimationFrozen=frozen; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 5, state -> {
            if(previewAnimationFrozen)return PlayState.STOP;
            if(hasDetailedCompanionRig())return animateDetailedCompanion(state);
            boolean sniffing = isSniffingNow();

            if (sniffing != lastSniffingState) {
                state.getController().forceAnimationReset();
                lastSniffingState = sniffing;
            }

            if (isRangePaused()) {
                state.getController().setAnimationSpeed(1);
                state.setAnimation(sniffing ? SNIFF : IDLE);
                return PlayState.CONTINUE;
            }

            boolean expressive=Config.livingCompanions() && EXPRESSIVE.contains(getSpeciesKey());
            state.getController().setAnimationSpeed(1);
            if (expressive && getReaction()==2)state.setAnimation(EAT);
            else if (state.isMoving()) {
                state.getController().setAnimationSpeed(Config.livingCompanions() ? Mth.clamp(state.getLimbSwingAmount()*2+.7,.75,1.5) : 1);
                state.setAnimation(WALK);
            } else if(expressive && getReaction()==1)state.setAnimation(HAPPY);
            else if(Config.livingCompanions() && getLifeGesture()==1)state.setAnimation(expressive?CURIOUS:SNIFF);
            else if(expressive && hasCarriedMineral())state.setAnimation(CARRY);
            else if(expressive && getLifeGesture()==2)state.setAnimation(INTERESTED);
            else state.setAnimation(IDLE);

            return PlayState.CONTINUE;
        }));
        controllers.add(new AnimationController<>(this,"blink",0,state->{
            if(!hasDetailedCompanionRig() || previewAnimationFrozen)return PlayState.STOP;
            double now=level().getGameTime()+state.getPartialTick();
            if(getReaction()==1){blinkPlaying=false;nextBlinkAt=now+60;return PlayState.STOP;}
            if(Double.isNaN(nextBlinkAt))nextBlinkAt=now+40+random.nextInt(80);
            if(!blinkPlaying && now>=nextBlinkAt){blinkPlaying=true;blinkUntil=now+4;state.getController().forceAnimationReset();}
            if(blinkPlaying && now<blinkUntil){state.setAnimation(BLINK);return PlayState.CONTINUE;}
            if(blinkPlaying){blinkPlaying=false;nextBlinkAt=now+(random.nextInt(9)==0?9:60+random.nextInt(90));}
            return PlayState.STOP;
        }));
        var voice = new AnimationController<SpectrobeEntity>(this,"voice",0,state->PlayState.STOP)
                .triggerableAnim("cry",DETAILED_CRY);
        voice.setAnimationSpeed("spiko".equals(getSpeciesKey()) ? .84 : 1);
        controllers.add(voice);
    }
    private PlayState animateDetailedCompanion(software.bernie.geckolib.animation.AnimationState<SpectrobeEntity> state) {
        boolean sniffing=isSniffingNow();
        if(sniffing!=lastSniffingState){state.getController().forceAnimationReset();lastSniffingState=sniffing;}
        state.getController().setAnimationSpeed(1);
        if(isRangePaused()){companionRunning=false;state.setAnimation(sniffing?SNIFF:IDLE);return PlayState.CONTINUE;}
        boolean life=Config.livingCompanions();
        if(life && getReaction()==2){state.setAnimation(EAT);return PlayState.CONTINUE;}
        if(life && getLifeGesture()==3){companionRunning=false;state.setAnimation(DETAILED_JUMP);return PlayState.CONTINUE;}
        if(state.isMoving()){
            companionRunning=com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.KomainuMotion.running(companionRunning,state.getLimbSwingAmount());
            state.getController().setAnimationSpeed(companionRunning?Mth.clamp(.8+state.getLimbSwingAmount()*.25,.85,1.15):Mth.clamp(.75+state.getLimbSwingAmount()*.5,.75,1.15));
            state.setAnimation(companionRunning?RUN:WALK);return PlayState.CONTINUE;
        }
        companionRunning=false;
        if(life && getReaction()==1)state.setAnimation(HAPPY);
        else if(life && getLifeGesture()==1)state.setAnimation(CURIOUS);
        else if(life && getLifeGesture()==2 && !hasCarriedMineral())state.setAnimation("spiko".equals(getSpeciesKey()) ? INTERESTED : DETAILED_INTEREST);
        else state.setAnimation(IDLE); // Carrying is normal locomotion/idle with the item attached, never a missing clip.
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (ownerUUID != null) {
            tag.putUUID("Owner", ownerUUID);
        }

        tag.putInt("CarriedMineralWait",carriedWait);
        tag.putString("SpeciesKey", getSpeciesKey());
        tag.putInt("TextureVariant", getTextureVariant());
        tag.putString("Stage", getStage().name());

        tag.putBoolean("RangePaused", isRangePaused());
        tag.putInt("RangePauseTicks", getRangePauseTicks());
        tag.putDouble("PausedX", pausedX);
        tag.putDouble("PausedY", pausedY);
        tag.putDouble("PausedZ", pausedZ);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        carriedWait=Math.clamp(tag.getInt("CarriedMineralWait"),-1,6000);
        setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND,0);

        if (tag.hasUUID("Owner")) {
            ownerUUID = tag.getUUID("Owner");
        }

        if (tag.contains("SpeciesKey")) {
            setSpeciesKey(tag.getString("SpeciesKey"));
        }

        if (tag.contains("TextureVariant")) {
            setTextureVariant(tag.getInt("TextureVariant"));
        }

        if (tag.contains("Stage")) {
            try {
                setStage(SpectrobeStage.valueOf(tag.getString("Stage")));
            } catch (Exception ignored) {
                setStage(SpectrobeStage.CHILD);
            }
        }

        if (tag.contains("RangePaused")) {
            setRangePaused(tag.getBoolean("RangePaused"));
        }

        if (tag.contains("RangePauseTicks")) {
            setRangePauseTicks(tag.getInt("RangePauseTicks"));
        }

        if (tag.contains("PausedX")) pausedX = tag.getDouble("PausedX");
        if (tag.contains("PausedY")) pausedY = tag.getDouble("PausedY");
        if (tag.contains("PausedZ")) pausedZ = tag.getDouble("PausedZ");
        var oldName=getCustomName();
        if(oldName!=null && getPersistentData().getBoolean("PrizmodBaby")) {
            var text=oldName.getString();
            if(text.contains("♛")||text.contains("⚒"))setCustomName(com.jvprz.spectrobesreforged.common.feature.training.SpectrobeNames.name(getSpeciesKey(),text.contains("♛"),text.contains("⚒")));
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType reason,
                                        SpawnGroupData spawnData) {

        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData);

        if (!this.level().isClientSide) {
            if ("unknown".equals(getSpeciesKey())) {
                setSpeciesKey(getDefaultSpeciesKey());
            }

            if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND) {
                int count = Math.max(1, getTextureVariantCount());
                setTextureVariant(this.random.nextInt(count));
            }

            if (ownerUUID == null && (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND)) {
                Player nearest = this.level().getNearestPlayer(this, 6.0);
                if (nearest != null) {
                    setOwner(nearest);
                }
            }
        }

        return data;
    }

    private static class HoldRangePauseGoal extends Goal {
        private final SpectrobeEntity mob;

        public HoldRangePauseGoal(SpectrobeEntity mob) {
            this.mob = mob;
            setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK,Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return mob.isRangePaused();
        }

        @Override
        public boolean canContinueToUse() {
            return mob.isRangePaused() && mob.getRangePauseTicks() > 0;
        }

        @Override
        public void start() {
            mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            mob.getNavigation().stop();
            mob.setDeltaMovement(Vec3.ZERO);

            Vec3 center = mob.getPausedCenter();
            if (mob.distanceToSqr(center.x, center.y, center.z) > 0.09D) {
                mob.teleportTo(center.x, center.y, center.z);
            }
        }

        @Override
        public void stop() {
            mob.getNavigation().stop();
        }
    }

    private static class FollowOwnerSideGoal extends Goal {
        private final SpectrobeEntity mob;
        private final double speed;
        private Player owner;
        private int recalcTime;

        public FollowOwnerSideGoal(SpectrobeEntity mob, double speed) {
            this.mob = mob;
            this.speed = speed;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (mob.isRangePaused()) return false;

            this.owner = mob.getOwner().orElse(null);
            if (owner == null || !owner.isAlive() || owner.isSpectator()) return false;
            return mob.distanceTo(owner) > (Config.livingCompanions() ? 4.0F : 3.0F);
        }

        @Override
        public boolean canContinueToUse() {
            if (mob.isRangePaused()) return false;
            if (owner == null || !owner.isAlive()) return false;
            return mob.distanceTo(owner) > (Config.livingCompanions() ? 2.6F : 2.0F);
        }

        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void start() { recalcTime=0; }
        @Override public void stop() { mob.getNavigation().stop(); owner=null; }

        @Override
        public void tick() {
            if (owner == null || mob.isRangePaused()) return;

            if (mob.distanceTo(owner) > 20.0F) {
                mob.teleportTo(owner.getX(), owner.getY(), owner.getZ());
                return;
            }

            if (--recalcTime <= 0) {
                recalcTime = Config.livingCompanions() ? 8 : 10;

                Vec3 look = Vec3.directionFromRotation(0,owner.getYRot());
                Vec3 side = new Vec3(-look.z, 0, look.x);
                Vec3 targetPos = owner.position().add(side.scale(1.5));

                mob.getNavigation().moveTo(
                        targetPos.x,
                        targetPos.y,
                        targetPos.z,
                        Config.livingCompanions() ? CompanionTemperament.of(mob.getSpeciesKey()).followSpeed(mob.distanceTo(owner)) : speed
                );
            }
        }
    }
}