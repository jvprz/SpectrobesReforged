package com.jvprz.spectrobesreforged.client.prizmod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ClientPrizmodState {
    private ClientPrizmodState() {}

    public static final List<Entry> BOX = new ArrayList<>();
    public static final List<Entry> TEAM = new ArrayList<>(6);
    public static Entry BABY = null;

    public record Entry(UUID id, String species, boolean baby) {}

    public static void setSnapshot(List<Entry> box, List<Entry> team, Entry baby) {
        BOX.clear();
        BOX.addAll(box);

        TEAM.clear();
        TEAM.addAll(team);

        BABY = baby;
    }
}