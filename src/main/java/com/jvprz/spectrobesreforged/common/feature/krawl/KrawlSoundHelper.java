package com.jvprz.spectrobesreforged.common.feature.krawl;

import com.jvprz.spectrobesreforged.common.content.entity.KrawlEntity;
import com.jvprz.spectrobesreforged.common.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public final class KrawlSoundHelper {

    private static final float DEFAULT_VOLUME = 0.35F;
    private static final float ATTACK_VOLUME = 0.45F;
    private static final float HURT_VOLUME = 0.45F;
    private static final float DEATH_VOLUME = 0.55F;

    private KrawlSoundHelper() {
    }

    public static void playIdle(KrawlEntity krawl) {
        play(krawl, ModSounds.KRAWL_IDLE, DEFAULT_VOLUME, 0.04F);
    }

    public static void playAttack(KrawlEntity krawl) {
        play(krawl, ModSounds.KRAWL_ATTACK, ATTACK_VOLUME, 0.035F);
    }

    public static void playHurt(KrawlEntity krawl) {
        play(krawl, ModSounds.KRAWL_HURT, HURT_VOLUME, 0.04F);
    }

    public static void playDeath(KrawlEntity krawl) {
        play(krawl, ModSounds.KRAWL_DEATH, DEATH_VOLUME, 0.025F);
    }

    private static void play(
            KrawlEntity krawl,
            Supplier<SoundEvent> sound,
            float volume,
            float pitchVariation
    ) {
        float pitch = krawl.getVoicePitch() * randomPitch(krawl, pitchVariation);
        krawl.playSound(sound.get(), volume, pitch);
    }

    private static float randomPitch(KrawlEntity krawl, float variation) {
        return 1.0F - variation + krawl.getRandom().nextFloat() * variation * 2.0F;
    }
}
