package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.network.ModSnapshotSender;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

public final class ModCommands {
    private ModCommands() {}

    // For now only komainu. Later: SpectrobeSpeciesRegistry.allKeys()
    private static final List<String> SPECIES = List.of("komainu");

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_SPECIES =
            (ctx, builder) -> SharedSuggestionProvider.suggest(SPECIES, builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("givespectrobe")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.argument("species", StringArgumentType.word())
                                .suggests(SUGGEST_SPECIES)
                                // /givespectrobe <species>
                                .executes(ctx -> {
                                    String species = StringArgumentType.getString(ctx, "species");
                                    return giveSpectrobe(ctx.getSource(), species, 0);
                                })
                                // /givespectrobe <species> <color>
                                .then(Commands.argument("color", IntegerArgumentType.integer(0, 2))
                                        .executes(ctx -> {
                                            String species = StringArgumentType.getString(ctx, "species");
                                            int color = IntegerArgumentType.getInteger(ctx, "color");
                                            return giveSpectrobe(ctx.getSource(), species, color);
                                        })
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("spectrobehealth")
                        .requires(cs -> cs.hasPermission(2))
                        .executes(ctx -> {
                            var source = ctx.getSource();
                            ServerPlayer player = source.getPlayer();
                            if (player == null) return 0;

                            PrizmodData data = player.getData(ModAttachments.PRIZMOD.get());
                            data.healEquippedFull();

                            source.sendSuccess(() -> Component.literal("Spectrobe team healed to full health.")
                                    .withStyle(ChatFormatting.GREEN), false);

                            // refrescar UI si está abierta
                            ModSnapshotSender.sendSnapshot(player, data);

                            return 1;
                        })
        );
    }

    private static int giveSpectrobe(CommandSourceStack source, String speciesRaw, int color) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        String species = speciesRaw.toLowerCase();

        if (!SPECIES.contains(species)) {
            source.sendFailure(Component.literal("Unknown spectrobe species: " + species));
            return 0;
        }

        PrizmodData data = player.getData(ModAttachments.PRIZMOD.get());
        UUID id = UUID.randomUUID();

        // Defaults for now (until JSON registry is wired):
        String stage = "CHILD";
        int level = 1;

        int hp = 0, atk = 0, def = 0;
        if (species.equals("komainu")) {
            hp = 120;
            atk = 65;
            def = 55;
        }

        SpectrobeEntry entry = new SpectrobeEntry(
                id,
                species,
                color,
                stage,
                level,
                hp,   // max hp
                hp,   // current hp (full)
                atk,
                def
        );

        data.addToBox(entry);

        source.sendSuccess(() -> Component.literal(
                "You have received a " + entry.species() + " in your Prizmod."
        ).withStyle(ChatFormatting.GREEN), false);

        source.sendSuccess(() -> Component.literal("Code: " + entry.id())
                .withStyle(ChatFormatting.DARK_GRAY), false);

        // refrescar UI si está abierta
        ModSnapshotSender.sendSnapshot(player, data);

        return 1;
    }
}