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

    private static final int WIRE_VERSION = 2;

    public static final Type<S2CPrizmodSnapshot> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    SpectrobesReforged.MODID, "prizmod_snapshot"));

    public record Entry(
            UUID id,
            String species,
            int color,
            String stage,
            int level,
            int hp,     // max
            int hpCur,  // current
            int atk,
            int def
    ) {}

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, S2CPrizmodSnapshot> STREAM_CODEC =
            new StreamCodec<>() {

                @Override
                public S2CPrizmodSnapshot decode(FriendlyByteBuf buf) {
                    int start = buf.readerIndex();

                    S2CPrizmodSnapshot out;
                    try {
                        int ver = buf.readVarInt();
                        out = switch (ver) {
                            case 2 -> decodeV2(buf);
                            default -> {
                                // Si no reconocemos versión, intentamos como V1 (sin header)
                                buf.readerIndex(start);
                                yield decodeV1(buf);
                            }
                        };
                    } catch (RuntimeException ex) {
                        // Fallback: si era un payload viejo (sin version header)
                        buf.readerIndex(start);
                        out = decodeV1(buf);
                    }

                    // CLAVE: aseguramos consumir el payload completo
                    if (buf.readableBytes() > 0) {
                        buf.skipBytes(buf.readableBytes());
                    }

                    return out;
                }

                private S2CPrizmodSnapshot decodeV2(FriendlyByteBuf buf) {
                    int boxSize = buf.readVarInt();
                    List<Entry> box = new ArrayList<>(boxSize);
                    for (int i = 0; i < boxSize; i++) box.add(readEntryV2(buf));

                    int teamSize = buf.readVarInt();
                    List<Entry> team = new ArrayList<>(teamSize);
                    for (int i = 0; i < teamSize; i++) {
                        boolean present = buf.readBoolean();
                        team.add(present ? readEntryV2(buf) : null);
                    }

                    Entry baby = buf.readBoolean() ? readEntryV2(buf) : null;
                    return new S2CPrizmodSnapshot(box, team, baby);
                }

                // V1 legacy: UUID + species + baby(boolean)
                private S2CPrizmodSnapshot decodeV1(FriendlyByteBuf buf) {
                    int boxSize = buf.readVarInt();
                    List<Entry> box = new ArrayList<>(boxSize);
                    for (int i = 0; i < boxSize; i++) box.add(readEntryV1(buf));

                    int teamSize = buf.readVarInt();
                    List<Entry> team = new ArrayList<>(teamSize);
                    for (int i = 0; i < teamSize; i++) {
                        boolean present = buf.readBoolean();
                        team.add(present ? readEntryV1(buf) : null);
                    }

                    Entry baby = buf.readBoolean() ? readEntryV1(buf) : null;
                    return new S2CPrizmodSnapshot(box, team, baby);
                }

                private Entry readEntryV2(FriendlyByteBuf buf) {
                    return new Entry(
                            buf.readUUID(),
                            buf.readUtf(),     // species
                            buf.readVarInt(),  // color
                            buf.readUtf(),     // stage
                            buf.readVarInt(),  // level
                            buf.readVarInt(),  // hp max
                            buf.readVarInt(),  // hp cur
                            buf.readVarInt(),  // atk
                            buf.readVarInt()   // def
                    );
                }

                private Entry readEntryV1(FriendlyByteBuf buf) {
                    UUID id = buf.readUUID();
                    String species = buf.readUtf();
                    boolean baby = buf.readBoolean();

                    String stage = baby ? "CHILD" : "ADULT";
                    int color = 0;
                    int level = 1;

                    int hp = 0;
                    int hpCur = 0;
                    int atk = 0;
                    int def = 0;

                    return new Entry(id, species, color, stage, level, hp, hpCur, atk, def);
                }

                @Override
                public void encode(FriendlyByteBuf buf, S2CPrizmodSnapshot msg) {
                    buf.writeVarInt(WIRE_VERSION); // <- header versionado

                    buf.writeVarInt(msg.box.size());
                    for (var e : msg.box) writeEntryV2(buf, e);

                    buf.writeVarInt(msg.team.size());
                    for (var e : msg.team) {
                        buf.writeBoolean(e != null);
                        if (e != null) writeEntryV2(buf, e);
                    }

                    buf.writeBoolean(msg.baby != null);
                    if (msg.baby != null) writeEntryV2(buf, msg.baby);
                }

                private void writeEntryV2(FriendlyByteBuf buf, Entry e) {
                    buf.writeUUID(e.id());
                    buf.writeUtf(e.species() == null ? "" : e.species());
                    buf.writeVarInt(e.color());
                    buf.writeUtf(e.stage() == null ? "CHILD" : e.stage());
                    buf.writeVarInt(e.level());
                    buf.writeVarInt(e.hp());
                    buf.writeVarInt(e.hpCur());
                    buf.writeVarInt(e.atk());
                    buf.writeVarInt(e.def());
                }
            };

    public static void handle(S2CPrizmodSnapshot msg) {
        Minecraft.getInstance().execute(() -> {
            var box = msg.box.stream()
                    .map(e -> new ClientPrizmodState.Entry(
                            e.id(), e.species(), e.color(), e.stage(), e.level(),
                            e.hp(), e.hpCur(), e.atk(), e.def()
                    ))
                    .toList();

            var team = msg.team.stream()
                    .map(e -> e == null ? null :
                            new ClientPrizmodState.Entry(
                                    e.id(), e.species(), e.color(), e.stage(), e.level(),
                                    e.hp(), e.hpCur(), e.atk(), e.def()
                            ))
                    .toList();

            ClientPrizmodState.Entry baby =
                    msg.baby == null ? null :
                            new ClientPrizmodState.Entry(
                                    msg.baby.id(), msg.baby.species(), msg.baby.color(),
                                    msg.baby.stage(), msg.baby.level(),
                                    msg.baby.hp(), msg.baby.hpCur(), msg.baby.atk(), msg.baby.def()
                            );

            ClientPrizmodState.setSnapshot(box, team, baby);
        });
    }
}