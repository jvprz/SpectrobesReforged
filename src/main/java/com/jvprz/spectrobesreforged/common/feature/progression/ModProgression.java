package com.jvprz.spectrobesreforged.common.feature.progression;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Grants action criteria only after the corresponding server-side operation succeeds. */
public final class ModProgression {
    private ModProgression() {}

    public static void award(ServerPlayer player, String advancement) {
        award(player, advancement, "completed");
    }

    public static void awakened(ServerPlayer player, String species) {
        award(player, "awakened");
        award(player, "collection", "species_" + species);
    }

    public static void excavated(ServerPlayer player,boolean crown,int terrain,int skillLevel,int count) {
        award(player,"excavate");
        if(crown)award(player,"excavation_perfect");
        if(terrain==2)award(player,"excavation_ice");
        award(player,"excavation_variety","terrain_"+terrain);
        if(skillLevel>=5)award(player,"excavation_master");
        if(count>=10)award(player,"excavation_ten");
        if(count>=100)award(player,"excavation_hundred");
    }

    /** One successful manual specimen credits each eligible worker once, with no extra item. */
    public static void completedExcavation(java.util.Collection<ServerPlayer> workers, String result, int earned, boolean crown, int terrain) {
        if (earned <= 0) return; // Automatic extraction never grants manual discovery milestones.
        var seen = new java.util.HashSet<java.util.UUID>();
        for (var player : workers) {
            if (!seen.add(player.getUUID()) || !player.isAlive() || player.isSpectator()) continue;
            var skill = player.getData(com.jvprz.spectrobesreforged.common.registry.ModAttachments.EXCAVATION.get());
            skill.complete(result, earned);
            player.setData(com.jvprz.spectrobesreforged.common.registry.ModAttachments.EXCAVATION.get(), skill);
            excavated(player, crown, terrain, skill.level(), skill.completed());
        }
    }

    private static void award(ServerPlayer player, String name, String criterion) {
        var holder = player.server.getAdvancements().get(
                ResourceLocation.fromNamespaceAndPath(SpectrobesReforged.MODID, name));
        if (holder != null) player.getAdvancements().award(holder, criterion);
    }
}
