package com.jvprz.spectrobesreforged.common.registry;

import com.jvprz.spectrobesreforged.common.feature.prizmod.data.PrizmodData;
import com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeEntry;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeSpecies;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.SpectrobeSpeciesRegistry;
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

import java.util.UUID;

public final class ModCommands {
    private ModCommands() {}

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_SPECIES =
            (ctx, builder) -> SharedSuggestionProvider.suggest(
                    SpectrobeSpeciesRegistry.keys(),
                    builder
            );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("givespectrobe")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.argument("species", StringArgumentType.word())
                                .suggests(SUGGEST_SPECIES)
                                .executes(ctx -> {
                                    String species = StringArgumentType.getString(ctx, "species");
                                    return giveSpectrobe(ctx.getSource(), species, 0);
                                })
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

                            ModSnapshotSender.sendSnapshot(player, data);
                            return 1;
                        })
        );
    }

    private static int giveSpectrobe(CommandSourceStack source, String speciesRaw, int color) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        String speciesKey = speciesRaw == null ? "" : speciesRaw.trim().toLowerCase(java.util.Locale.ROOT);
        SpectrobeSpecies species = SpectrobeSpeciesRegistry.getByKey(speciesKey);

        if (species == null) {
            source.sendFailure(Component.literal("Unknown spectrobe species: " + speciesKey));
            return 0;
        }

        PrizmodData data = player.getData(ModAttachments.PRIZMOD.get());
        UUID id = UUID.randomUUID();

        String stage = species.initialStage().name();
        int level = 1;

        int hp = species.stats().hp().base();
        int atk = species.stats().attack().base();
        int def = species.stats().defense().base();

        SpectrobeEntry entry = new SpectrobeEntry(
                id,
                species.key(),
                color,
                stage,
                level,
                hp,
                hp,
                atk,
                def,
                0, // mineralsFed
                0, // mineralHpBonus
                0, // mineralAtkBonus
                0  // mineralDefBonus
        );

        data.addToBox(entry);

        source.sendSuccess(() -> Component.literal(
                "You have received a " + entry.species() + " in your Prizmod."
        ).withStyle(ChatFormatting.GREEN), false);

        source.sendSuccess(() -> Component.literal("Code: " + entry.id())
                .withStyle(ChatFormatting.DARK_GRAY), false);

        ModSnapshotSender.sendSnapshot(player, data);
        return 1;
    }
}