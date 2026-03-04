package com.jvprz.spectrobesreforged.common.network;

import com.jvprz.spectrobesreforged.SpectrobesReforged;
import com.jvprz.spectrobesreforged.client.feature.prizmod.ClientPrizmodState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record S2CPrizmodSnapshot(
        List<Entry> box,
        List<Entry> team,
        Entry baby
) implements CustomPacketPayload {

    public static final Type<S2CPrizmodSnapshot> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    SpectrobesReforged.MODID, "prizmod_snapshot"));

    public record Entry(UUID id, String species, int color, String stage, int level, int hp, int atk, int def) {}

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, S2CPrizmodSnapshot> STREAM_CODEC =
            new StreamCodec<>() {

                @Override
                public S2CPrizmodSnapshot decode(FriendlyByteBuf buf) {

                    int boxSize = buf.readVarInt();
                    List<Entry> box = new ArrayList<>(boxSize);
                    for (int i = 0; i < boxSize; i++) {
                        box.add(readEntry(buf));
                    }

                    int teamSize = buf.readVarInt();
                    List<Entry> team = new ArrayList<>(teamSize);
                    for (int i = 0; i < teamSize; i++) {
                        boolean present = buf.readBoolean();
                        team.add(present ? readEntry(buf) : null);
                    }

                    Entry baby = buf.readBoolean() ? readEntry(buf) : null;

                    return new S2CPrizmodSnapshot(box, team, baby);
                }

                @Override
                public void encode(FriendlyByteBuf buf, S2CPrizmodSnapshot msg) {

                    buf.writeVarInt(msg.box.size());
                    for (var e : msg.box) writeEntry(buf, e);

                    buf.writeVarInt(msg.team.size());
                    for (var e : msg.team) {
                        buf.writeBoolean(e != null);
                        if (e != null) writeEntry(buf, e);
                    }

                    buf.writeBoolean(msg.baby != null);
                    if (msg.baby != null) writeEntry(buf, msg.baby);
                }

                private Entry readEntry(FriendlyByteBuf buf) {
                    return new Entry(
                            buf.readUUID(),
                            buf.readUtf(),
                            buf.readVarInt(),  // color
                            buf.readUtf(),     // stage
                            buf.readVarInt(),  // level
                            buf.readVarInt(),  // hp
                            buf.readVarInt(),  // atk
                            buf.readVarInt()   // def
                    );
                }

                private void writeEntry(FriendlyByteBuf buf, Entry e) {
                    buf.writeUUID(e.id());
                    buf.writeUtf(e.species());
                    buf.writeVarInt(e.color());
                    buf.writeUtf(e.stage());
                    buf.writeVarInt(e.level());
                    buf.writeVarInt(e.hp());
                    buf.writeVarInt(e.atk());
                    buf.writeVarInt(e.def());
                }
            };

    public static void handle(S2CPrizmodSnapshot msg) {
        Minecraft.getInstance().execute(() -> {
            var box = msg.box.stream()
                    .map(e -> new ClientPrizmodState.Entry(
                            e.id(), e.species(),
                            e.color(), e.stage(),
                            e.level(), e.hp(), e.atk(), e.def()
                    ))
                    .toList();

            var team = msg.team.stream()
                    .map(e -> e == null ? null :
                            new ClientPrizmodState.Entry(
                                    e.id(), e.species(),
                                    e.color(), e.stage(),
                                    e.level(), e.hp(), e.atk(), e.def()
                            ))
                    .toList();

            ClientPrizmodState.Entry baby =
                    msg.baby == null ? null :
                            new ClientPrizmodState.Entry(
                                    msg.baby.id(), msg.baby.species(),
                                    msg.baby.color(), msg.baby.stage(),
                                    msg.baby.level(), msg.baby.hp(), msg.baby.atk(), msg.baby.def()
                            );

            ClientPrizmodState.setSnapshot(box, team, baby);
        });
    }
}