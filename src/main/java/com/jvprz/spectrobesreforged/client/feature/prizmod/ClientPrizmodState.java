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
            int hpCur,
            int atk,
            int def,
            int mineralsFed,
            int mineralHpBonus,
            int mineralAtkBonus,
            int mineralDefBonus,
            com.jvprz.spectrobesreforged.common.feature.prizmod.data.SpectrobeProgress progress,
            String element
    ) {
        public int xp(){return progress.xp();}
        public boolean crowned(){return progress.crown();}
    public boolean cooperative(){return progress.cooperative();}
        public int quality(){return progress.quality();}
        public int battles(){return progress.battles();}
    }

    public static void setSnapshot(List<Entry> box, List<Entry> team, Entry baby) {
        BOX.clear();
        BOX.addAll(box);

        TEAM.clear();
        TEAM.addAll(team);

        BABY = baby;
    }
}