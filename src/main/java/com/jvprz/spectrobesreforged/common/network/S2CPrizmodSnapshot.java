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

    public record Entry(UUID id, String species, boolean baby) {}

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
                        box.add(new Entry(
                                buf.readUUID(),
                                buf.readUtf(),
                                buf.readBoolean()
                        ));
                    }

                    int teamSize = buf.readVarInt();
                    List<Entry> team = new ArrayList<>(teamSize);
                    for (int i = 0; i < teamSize; i++) {
                        boolean present = buf.readBoolean();
                        if (!present) {
                            team.add(null);
                        } else {
                            team.add(new Entry(
                                    buf.readUUID(),
                                    buf.readUtf(),
                                    buf.readBoolean()
                            ));
                        }
                    }

                    Entry baby = null;
                    if (buf.readBoolean()) {
                        baby = new Entry(
                                buf.readUUID(),
                                buf.readUtf(),
                                buf.readBoolean()
                        );
                    }

                    return new S2CPrizmodSnapshot(box, team, baby);
                }

                @Override
                public void encode(FriendlyByteBuf buf, S2CPrizmodSnapshot msg) {

                    buf.writeVarInt(msg.box.size());
                    for (var e : msg.box) {
                        buf.writeUUID(e.id());
                        buf.writeUtf(e.species());
                        buf.writeBoolean(e.baby());
                    }

                    buf.writeVarInt(msg.team.size());
                    for (var e : msg.team) {
                        buf.writeBoolean(e != null);
                        if (e != null) {
                            buf.writeUUID(e.id());
                            buf.writeUtf(e.species());
                            buf.writeBoolean(e.baby());
                        }
                    }

                    buf.writeBoolean(msg.baby != null);
                    if (msg.baby != null) {
                        buf.writeUUID(msg.baby.id());
                        buf.writeUtf(msg.baby.species());
                        buf.writeBoolean(msg.baby.baby());
                    }
                }
            };

    public static void handle(S2CPrizmodSnapshot msg) {
        Minecraft.getInstance().execute(() -> {
            var box = msg.box.stream()
                    .map(e -> new ClientPrizmodState.Entry(e.id(), e.species(), e.baby()))
                    .toList();

            var team = msg.team.stream()
                    .map(e -> e == null ? null :
                            new ClientPrizmodState.Entry(e.id(), e.species(), e.baby()))
                    .toList();

            ClientPrizmodState.Entry baby =
                    msg.baby == null ? null :
                            new ClientPrizmodState.Entry(msg.baby.id(), msg.baby.species(), msg.baby.baby());

            ClientPrizmodState.setSnapshot(box, team, baby);
        });
    }
}