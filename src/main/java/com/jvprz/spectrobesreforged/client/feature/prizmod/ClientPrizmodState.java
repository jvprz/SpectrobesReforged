package com.jvprz.spectrobesreforged.client.feature.prizmod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ClientPrizmodState {
    private ClientPrizmodState() {}

    public static final List<Entry> BOX = new ArrayList<>();
    public static final List<Entry> TEAM = new ArrayList<>(6);
    public static Entry BABY = null;

    public static record Entry(
            UUID id,
            String species,
            int color,
            String stage,
            int level,
            int hp,
            int atk,
            int def
    ) {}

    public static void setSnapshot(List<Entry> box, List<Entry> team, Entry baby) {
        BOX.clear();
        BOX.addAll(box);

        TEAM.clear();
        TEAM.addAll(team);

        BABY = baby;
    }
}