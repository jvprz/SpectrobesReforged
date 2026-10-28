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
    public static final ModConfigSpec.BooleanValue HUD_ENABLED = B.translation("spectrobesreforged.configuration.hudEnabled").define("hudEnabled",true);
    public static final ModConfigSpec.IntValue HUD_X = B.translation("spectrobesreforged.configuration.hudX").comment("Horizontal position as a percentage of available screen space.").defineInRange("hudX",0,0,100);
    public static final ModConfigSpec.IntValue HUD_Y = B.translation("spectrobesreforged.configuration.hudY").comment("Vertical position as a percentage of available screen space.").defineInRange("hudY",0,0,100);
    public static final ModConfigSpec.IntValue HUD_SCALE = B.translation("spectrobesreforged.configuration.hudScale").defineInRange("hudScale",85,50,150);
    public static final ModConfigSpec.BooleanValue HUD_TEAM = B.translation("spectrobesreforged.configuration.hudTeam").define("hudTeam",true);
    public static final ModConfigSpec.BooleanValue HUD_VERTICAL = B.translation("spectrobesreforged.configuration.hudVertical").define("hudVertical",false);
    public static boolean hudVertical() { return get(HUD_VERTICAL); }
    public static int hudHeight() { return !hudTeam() ? 62 : hudVertical() ? 220 : 94; }
    public static boolean hudEnabled() { return get(HUD_ENABLED); }
    public static int hudX() { return get(HUD_X); }
    public static int hudY() { return get(HUD_Y); }
    public static float hudScale() { return get(HUD_SCALE)/100f; }
    public static boolean hudTeam() { return get(HUD_TEAM); }
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
