package com.jvprz.spectrobesreforged.client.event;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.common.config.ClientConfig;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid=SpectrobesReforged.MODID,value=Dist.CLIENT)
public final class ClientSoundPreferences {
    private ClientSoundPreferences() {}
    @SubscribeEvent public static void sound(PlaySoundEvent event){
        SoundInstance sound=event.getSound();
        if(sound==null||!sound.getLocation().getNamespace().equals(SpectrobesReforged.MODID))return;
        if(ClientConfig.volume()<=0 || (sound.getLocation().getPath().startsWith("scanner_")&&!ClientConfig.scanSounds())){
            event.setSound(null);return;
        }
        if(!(sound instanceof Scaled))event.setSound(sound instanceof TickableSoundInstance ticking?new ScaledTicking(ticking):new Scaled(sound));
    }
    /** Delegate every property, including custom audio streams and ticking lifetimes. */
    private static class Scaled implements SoundInstance {
        protected final SoundInstance source;
        Scaled(SoundInstance source){this.source=source;}
        public ResourceLocation getLocation(){return source.getLocation();}
        public WeighedSoundEvents resolve(SoundManager manager){return source.resolve(manager);}
        public Sound getSound(){return source.getSound();}
        public SoundSource getSource(){return source.getSource();}
        public boolean isLooping(){return source.isLooping();}
        public boolean isRelative(){return source.isRelative();}
        public int getDelay(){return source.getDelay();}
        public float getVolume(){return source.getVolume()*ClientConfig.volume();}
        public float getPitch(){return source.getPitch();}
        public double getX(){return source.getX();}
        public double getY(){return source.getY();}
        public double getZ(){return source.getZ();}
        public Attenuation getAttenuation(){return source.getAttenuation();}
        public boolean canStartSilent(){return source.canStartSilent();}
        public boolean canPlaySound(){return source.canPlaySound();}
        public CompletableFuture<AudioStream> getStream(SoundBufferLibrary buffers,Sound sound,boolean looping){return source.getStream(buffers,sound,looping);}
    }
    private static final class ScaledTicking extends Scaled implements TickableSoundInstance {
        private final TickableSoundInstance ticking;
        ScaledTicking(TickableSoundInstance sound){super(sound);ticking=sound;}
        public boolean isStopped(){return ticking.isStopped();}
        public void tick(){ticking.tick();}
    }
}
