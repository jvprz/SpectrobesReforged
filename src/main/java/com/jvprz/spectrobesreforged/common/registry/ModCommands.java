package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;

import com.jvprz.spectrobesreforged.common.feature.prizmod.logic.SpectrobeManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public final class ModCommands {
    private ModCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("givespectrobe")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.argument("species", StringArgumentType.word())
                                .executes(ctx -> {
                                    String species = StringArgumentType.getString(ctx, "species");
                                    return giveSpectrobe(ctx.getSource(), species, true);
                                })
                                .then(Commands.argument("baby", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            String species = StringArgumentType.getString(ctx, "species");
                                            boolean baby = BoolArgumentType.getBool(ctx, "baby");
                                            return giveSpectrobe(ctx.getSource(), species, baby);
                                        })
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("equipbaby")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.literal("last")
                                .executes(ctx -> equipBabyLast(ctx.getSource()))
                        )
                        .then(Commands.argument("uuid", StringArgumentType.word())
                                .executes(ctx -> {
                                    String raw = StringArgumentType.getString(ctx, "uuid");
                                    return equipBabyUuid(ctx.getSource(), raw);
                                })
                        )
        );

        dispatcher.register(
                Commands.literal("prizmod")
                        .requires(cs -> cs.hasPermission(2))
                        .executes(ctx -> showStatus(ctx.getSource()))
        );
    }

    private static int showStatus(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        PrizmodData data = player.getData(ModAttachments.PRIZMOD.get());

        source.sendSuccess(() -> Component.literal("=== Prizmod ===").withStyle(ChatFormatting.GOLD), false);
        source.sendSuccess(() -> Component.literal("Box: " + data.getBox().size() + " spectrobes"), false);
        source.sendSuccess(() -> Component.literal(
                "Baby slot: " + data.getBabySlot()
                        .map(e -> e.species() + " | " + e.id())
                        .orElse("(empty)")
        ), false);

        // Lista corta (hasta 10)
        data.getBox().stream().limit(10).forEach(e -> {
            source.sendSuccess(() -> Component.literal("- " + e.species() + " | " + e.id() + (e.baby() ? " (baby)" : "")), false);
        });

        return 1;
    }

    private static int giveSpectrobe(CommandSourceStack source, String species, boolean baby) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        PrizmodData data = player.getData(ModAttachments.PRIZMOD.get());

        UUID id = UUID.randomUUID();
        SpectrobeEntry entry = new SpectrobeEntry(id, species.toLowerCase(), baby);

        data.addToBox(entry);

        source.sendSuccess(() -> Component.literal("Added to Prizmod box: " + entry.species() + " | " + entry.id())
                .withStyle(ChatFormatting.GREEN), false);

        return 1;
    }

    private static int equipBabyLast(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        PrizmodData data = player.getData(ModAttachments.PRIZMOD.get());

        Optional<SpectrobeEntry> lastBaby = data.getBox().stream()
                .filter(SpectrobeEntry::baby)
                .max(Comparator.comparing(SpectrobeEntry::id)); // no es “tiempo”, pero sirve como “último” para testing

        if (lastBaby.isEmpty()) {
            source.sendFailure(Component.literal("No baby spectrobes in box."));
            return 0;
        }

        return equipBaby(source, player, lastBaby.get().id());
    }

    private static int equipBabyUuid(CommandSourceStack source, String rawUuid) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        UUID id;
        try {
            id = UUID.fromString(rawUuid);
        } catch (Exception e) {
            source.sendFailure(Component.literal("Invalid UUID."));
            return 0;
        }

        return equipBaby(source, player, id);
    }

    private static int equipBaby(CommandSourceStack source, ServerPlayer player, UUID newId) {
        PrizmodData data = player.getData(ModAttachments.PRIZMOD.get());

        // Debe existir en la caja
        SpectrobeEntry newEntry = data.findInBox(newId).orElse(null);
        if (newEntry == null) {
            source.sendFailure(Component.literal("That spectrobe is not in the box."));
            return 0;
        }
        if (!newEntry.baby()) {
            source.sendFailure(Component.literal("That spectrobe is not a baby."));
            return 0;
        }

        // Swap: si había uno equipado, vuelve a caja
        SpectrobeEntry old = data.getBabySlot().orElse(null);
        if (old != null && !data.hasInBox(old.id())) {
            data.addToBox(old);
        }

        // Nuevo sale de la caja y entra al slot
        data.removeFromBox(newId);
        data.setBabySlot(newEntry);

        // Mundo: por ahora SOLO soportamos komainu
        if (!newEntry.species().equalsIgnoreCase("komainu")) {
            source.sendFailure(Component.literal("Only 'komainu' is supported for baby spawn right now."));
            return 0;
        }

        ServerLevel level = player.serverLevel();
        SpectrobeManager.despawnBaby(level, player);

        boolean ok = SpectrobeManager.spawnBaby(level, player, newEntry);
        if (!ok) {
            source.sendFailure(Component.literal("No spawn factory for species: " + newEntry.species()));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Equipped baby: " + newEntry.species() + " (" + newId + ")")
                .withStyle(ChatFormatting.AQUA), false);

        return 1;
    }
}