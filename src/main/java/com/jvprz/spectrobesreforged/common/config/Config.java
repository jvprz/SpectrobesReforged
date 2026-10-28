package com.jvprz.spectrobesreforged.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** World/server-owned balance. NeoForge synchronizes this SERVER config to connecting clients. */
public final class Config {
    private Config() {}
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();
    static { B.translation("spectrobesreforged.configuration.vortices").push("vortices"); }
    public static final ModConfigSpec.BooleanValue NATURAL_VORTICES = B.translation("spectrobesreforged.configuration.naturalVortices")
            .comment("Enable natural vortex discovery. Summoned vortices remain available.").define("naturalVortices", true);
    public static final ModConfigSpec.IntValue DISCOVERY_SECONDS = B.translation("spectrobesreforged.configuration.discoverySeconds")
            .comment("Seconds between eligible discovery checks, at 20 TPS.").defineInRange("discoverySeconds",20,5,600);
    public static final ModConfigSpec.DoubleValue DISCOVERY_CHANCE = B.translation("spectrobesreforged.configuration.discoveryChance")
            .comment("Attempt probability for each eligible check, from 0 to 1.").defineInRange("discoveryChance",0.5,0,1);
    public static final ModConfigSpec.IntValue COOLDOWN_SECONDS = B.translation("spectrobesreforged.configuration.cooldownSeconds")
            .comment("Personal pause after discovery or a finished/cancelled encounter.").defineInRange("cooldownSeconds",60,0,3600);
    public static final ModConfigSpec.DoubleValue SPEED = B.translation("spectrobesreforged.configuration.vortexSpeed")
            .comment("Vortex speed in blocks per second on clear level terrain, at 20 TPS.").defineInRange("vortexSpeed",5.0,0.5,10.0);
    public static final ModConfigSpec.IntValue MAX_VORTICES = B.translation("spectrobesreforged.configuration.maxVortices")
            .comment("Loaded vortices per dimension before natural discovery stops; includes active encounters. Does not remove existing entities.").defineInRange("maxVortices",32,1,256);
    static { B.pop(); }
    public static final ModConfigSpec SPEC = B.build();
    private static <T> T get(ModConfigSpec.ConfigValue<T> value) { return SPEC.isLoaded()?value.get():value.getDefault(); }
    public static boolean naturalVortices() { return get(NATURAL_VORTICES); }
    public static int discoveryTicks() { return get(DISCOVERY_SECONDS)*20; }
    public static double discoveryChance() { return get(DISCOVERY_CHANCE); }
    public static int cooldownTicks() { return get(COOLDOWN_SECONDS)*20; }
    public static double vortexSpeed() { return get(SPEED)/20.0; }
    public static int maxVortices() { return get(MAX_VORTICES); }
}
