package com.jvprz.spectrobesreforged.common.config;

import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;

/** Client preferences only. No gameplay authority or client-only class references. */
public final class ClientConfig {
    private ClientConfig() {}
    public enum ParticleDensity implements TranslatableEnum {
        LOW(0.25), MEDIUM(0.5), HIGH(1.0);
        private final double factor;
        ParticleDensity(double factor) { this.factor=factor; }
        public int scale(int count) { return Math.max(1,(int)Math.round(count*factor)); }
        @Override public Component getTranslatedName() { return Component.translatable("spectrobesreforged.configuration.density."+name().toLowerCase(Locale.ROOT)); }
    }
    public enum ScanFeedback implements TranslatableEnum {
        BOTH, VISUAL, SOUND;
        public boolean visuals() { return this!=SOUND; }
        public boolean sounds() { return this!=VISUAL; }
        @Override public Component getTranslatedName() { return Component.translatable("spectrobesreforged.configuration.feedback."+name().toLowerCase(Locale.ROOT)); }
    }
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();
    static { B.translation("spectrobesreforged.configuration.preferences").push("preferences"); }
    public static final ModConfigSpec.EnumValue<ParticleDensity> PARTICLES = B.translation("spectrobesreforged.configuration.particles")
            .comment("Vortex particle density; Minecraft particle settings still apply.").defineEnum("vortexParticles",ParticleDensity.HIGH);
    public static final ModConfigSpec.IntValue EFFECT_VOLUME = B.translation("spectrobesreforged.configuration.effectVolume")
            .comment("Volume percentage of mod sound effects. Minecraft sound-category volumes also apply.").defineInRange("effectVolume",100,0,100);
    public static final ModConfigSpec.EnumValue<ScanFeedback> SCAN_FEEDBACK = B.translation("spectrobesreforged.configuration.scanFeedback")
            .comment("Scanner feedback: visual, sound, or both. Does not change the server scan.").defineEnum("scanFeedback",ScanFeedback.BOTH);
    public static final ModConfigSpec.BooleanValue EXCAVATION_HINTS = B.translation("spectrobesreforged.configuration.excavationHints")
            .comment("Show extended excavation tool explanations; names, warnings and status remain visible.").define("excavationHints",true);
    public static final ModConfigSpec.BooleanValue REDUCED_MOTION = B.translation("spectrobesreforged.configuration.reducedMotion")
            .comment("Reduce decorative interface motion, flying excavation debris and the Prizmod preview animation.").define("reducedMotion",false);
    static { B.pop(); }
    public static final ModConfigSpec SPEC = B.build();
    private static <T> T get(ModConfigSpec.ConfigValue<T> value) { return SPEC.isLoaded()?value.get():value.getDefault(); }
    public static int particles(int count) { return get(PARTICLES).scale(count); }
    public static float volume() { return get(EFFECT_VOLUME)/100.0F; }
    public static boolean scanVisuals() { return get(SCAN_FEEDBACK).visuals(); }
    public static boolean scanSounds() { return get(SCAN_FEEDBACK).sounds(); }
    public static boolean excavationHints() { return get(EXCAVATION_HINTS); }
    public static boolean reducedMotion() { return get(REDUCED_MOTION); }
}
