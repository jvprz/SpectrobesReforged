package com.jvprz.spectrobesreforged.common.feature.excavation;

import java.util.Random;

/** Excavation rules shared by server and tests; no client or registry dependencies. */
public final class ExcavationPuzzle {
    public static final int WIDTH = 32, CELLS = WIDTH * WIDTH;
    public static final int DUST = 8, FOSSIL = 16, COVER = 32;
    public static final int DRILL = 0, BLOWER = 1, FINE = 2, HEAVY = 3, RECOVERY = 4,
            PITON = 5, FIRE = 6, WATER = 7, BLOWER_S = 8, VACUUM = 9,
            SOLVENT = 10, SONIC = 11, GIGA = 12, BLAST = 13, TAP = 14;
    public static final int ROCK = 0, SAND = 1, ICE = 2, LAVA = 3, WATER_TERRAIN = 4, MUD = 5;
    private final byte[] cells;
    private int integrity = 100, elapsedTicks, stress, heat, gas, repairs, overburden, terrain;
    private int rock;
    private int idleTicks, lastX = -100, lastY = -100, strokes;
    private boolean damaged, automatic;
    private long randomState;

    public record State(byte[] cells, int integrity, int ticks, boolean damaged, int stress,
                        int heat, int gas, int repairs, int overburden, int terrain,
                        boolean automatic, long randomState, int strokes, int rock) {
        public State(byte[] cells,int integrity,int ticks,boolean damaged,int stress,int heat,int gas,int repairs,int overburden,int terrain,boolean automatic,long randomState,int strokes) {
            this(cells,integrity,ticks,damaged,stress,heat,gas,repairs,overburden,terrain,automatic,randomState,strokes,0);
        }
        public State { cells = cells.clone(); }
        @Override public byte[] cells() { return cells.clone(); }
    }
    public static int radius(int tool) {
        return switch (tool) {
            case FINE, SOLVENT -> 1;
            case HEAVY, PITON, FIRE, WATER, SONIC -> 3;
            case BLOWER, VACUUM -> 4;
            case BLOWER_S -> 32;
            default -> 2;
        };
    }
    /** Compatibility constructor for the first playable prototype (already at the fossil layer). */
    public ExcavationPuzzle(boolean[] mask, long seed) { this(mask, seed, ROCK, 0); }
    public ExcavationPuzzle(boolean[] mask, long seed, int terrain, int overburden) {
        this(mask,seed,terrain,new ExcavationProfile(0,terrain==ROCK?0:1,overburden,2,4));
    }
    public ExcavationPuzzle(boolean[] mask,long seed,int terrain,ExcavationProfile profile) {
        if (mask.length != CELLS) throw new IllegalArgumentException("Invalid fossil mask");
        this.terrain = Math.clamp(terrain, ROCK, MUD);
        this.overburden = Math.clamp(profile.openingLayers(), 0, 4);
        this.rock = Math.clamp(profile.rock(),0,4);
        this.randomState = seed;
        cells = new byte[CELLS];
        var random = new Random(seed);
        int count = 0;
        // Smooth patches avoid the previous salt-and-pepper depth distribution.
        int[] patches = new int[64];
        for (int i = 0; i < patches.length; i++) patches[i] = profile.minDepth() + random.nextInt(profile.maxDepth()-profile.minDepth()+1);
        for (int i = 0; i < CELLS; i++) {
            int depth = patches[(i / WIDTH / 4) * 8 + i % WIDTH / 4];
            cells[i] = (byte) ((mask[i] ? FOSSIL : 0) | depth | (this.terrain == ROCK ? 0 : COVER | (Math.clamp(profile.coverLayers(),1,4)-1)<<6));
            if (mask[i]) count++;
        }
        if (count == 0) throw new IllegalArgumentException("Empty fossil mask");
    }
    private ExcavationPuzzle(State saved) {
        cells = saved.cells();
        if (cells.length != CELLS) throw new IllegalArgumentException("Invalid excavation grid");
        int count = 0;
        for (byte raw : cells) {
            int value = raw & 255;
            if (((value & 192) != 0 && (value & COVER) == 0) || (value & 7) > 4) throw new IllegalArgumentException("Invalid cell");
            if ((value & FOSSIL) != 0) count++;
        }
        if (count == 0) throw new IllegalArgumentException("Empty fossil mask");
        integrity = Math.clamp(saved.integrity(), 0, 100);
        elapsedTicks = Math.clamp(saved.ticks(), 0, 72000);
        stress = Math.clamp(saved.stress(), 0, 100); heat = Math.clamp(saved.heat(), 0, 100);
        gas = Math.clamp(saved.gas(), 0, 100); repairs = Math.clamp(saved.repairs(), 0, 4);
        terrain = Math.clamp(saved.terrain(), ROCK, MUD); overburden = Math.clamp(saved.overburden(), 0, 4);
        damaged = saved.damaged(); automatic = saved.automatic(); randomState = saved.randomState();
        strokes = Math.max(0, saved.strokes());
        rock = Math.clamp(saved.rock(),0,4);
    }
    public static ExcavationPuzzle restore(byte[] cells, int health, int ticks, boolean damaged) {
        return restore(new State(cells, health, ticks, damaged, 0, 0, 0, 0, 0, ROCK, false, 1, 0));
    }
    public static ExcavationPuzzle restore(State state) { return new ExcavationPuzzle(state); }
    public State state() { return new State(cells, integrity, elapsedTicks, damaged, stress, heat, gas,
            repairs, overburden, terrain, automatic, randomState, strokes, rock); }
    public int cell(int i) { return i >= 0 && i < CELLS ? cells[i] & 255 : 0; }
    public byte[] snapshot() { return cells.clone(); }
    public int rock() { return rock; }
    public static int coverLayers(int value) { return (value&COVER)==0?0:1+((value>>>6)&3); }
    public int integrity() { return integrity; }
    public int elapsedTicks() { return elapsedTicks; }
    public boolean damaged() { return damaged; }
    public int stress() { return stress; }
    public int heat() { return heat; }
    public int gas() { return gas; }
    public int repairsLeft() { return 4 - repairs; }
    public int overburden() { return overburden; }
    public int terrain() { return terrain; }
    public boolean automatic() { return automatic; }
    public void tick() {
        if (integrity <= 0) return;
        elapsedTicks = Math.min(72000, elapsedTicks + 1);
        idleTicks++;
        if (idleTicks > 4) stress = Math.max(0, stress - 2);
        if (elapsedTicks % 10 == 0) heat = Math.max(0, heat - 1);
    }
    public int percent() {
        if (overburden > 0) return 0;
        int total = 0, exposed = 0;
        for (byte c : cells) if ((c & FOSSIL) != 0) {
            total++;
            if ((c & (7 | DUST | COVER)) == 0) exposed++;
        }
        return exposed * 100 / total;
    }
    public boolean canExtract() { return integrity > 0 && percent() >= 90; }
    public boolean crowned() { return canExtract() && percent() == 100 && integrity == 100
            && !damaged && !automatic && elapsedTicks <= 1200; }
    public int experience() {
        if (!canExtract() || automatic) return 0;
        return 20 + percent() / 5 + integrity / 5 + (elapsedTicks <= 1200 ? 15 : 0) + (crowned() ? 25 : 0);
    }
    /** Deterministic saved stream: closing/reopening cannot reroll a blast. */
    private boolean chance(int percent) {
        randomState += 0x9e3779b97f4a7c15L;
        long value = randomState;
        value = (value ^ (value >>> 30)) * 0xbf58476d1ce4e5b9L;
        value = (value ^ (value >>> 27)) * 0x94d049bb133111ebL;
        value ^= value >>> 31;
        return Long.remainderUnsigned(value >>> 1, 100) < percent;
    }
    public boolean work(int x, int y, int tool) { return work(x, y, tool, 1, false); }
    public boolean work(int x, int y, int tool, int skillLevel, boolean catalogued) {
        if (integrity <= 0 || automatic || x < 0 || y < 0 || x >= WIDTH || y >= WIDTH || tool < 0 || tool > TAP) return false;
        if (overburden > 0) {
            if (tool != TAP) return false;
            overburden--; return true;
        }
        if (tool == TAP) return false;
        if (tool == GIGA || tool == BLAST) {
            if (tool == GIGA && !catalogued) return false;
            automatic = true;
            if (tool == BLAST && !chance(50)) { integrity = 0; damaged = true; return true; }
            for (int i = 0; i < CELLS; i++) cells[i] &= FOSSIL;
            return true;
        }
        if (tool == RECOVERY) {
            if (repairs >= 4 || integrity == 100) return false;
            integrity = Math.min(100, integrity + 10); repairs++; return true;
        }
        // Sonic is deliberately slow and harmless; solvent dissolves tight patches.
        strokes++;
        if (tool == SONIC && strokes % 3 != 0) return true;
        if (tool == SOLVENT && strokes % 2 != 0) return true;
        int previousStress = stress, previousHeat = heat, previousGas = gas;
        boolean moved = (x - lastX) * (x - lastX) + (y - lastY) * (y - lastY) >= 9;
        if (moved) stress = Math.max(0, stress - 9);
        lastX = x; lastY = y; idleTicks = 0;
        boolean changed = false, exposedContact = false;
        int radius = radius(tool);
        for (int dy = -radius; dy <= radius; dy++) for (int dx = -radius; dx <= radius; dx++) {
            if (tool != BLOWER_S && dx * dx + dy * dy > radius * radius + 1) continue;
            int xx = x + dx, yy = y + dy;
            if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= WIDTH) continue;
            int index = yy * WIDTH + xx, value = cell(index), depth = value & 7;
            boolean covered = (value & COVER) != 0;
            if (covered) {
                boolean matches = switch (terrain) {
                    case SAND -> tool == BLOWER || tool == BLOWER_S || tool == VACUUM;
                    case ICE -> tool == FIRE;
                    case LAVA -> tool == WATER;
                    case WATER_TERRAIN, MUD -> tool == VACUUM;
                    default -> false;
                };
                if (matches) {
                    int remaining=coverLayers(value)-1;
                    cells[index]=(byte)((value & ~(COVER|192)) | (remaining==0?0:COVER|((remaining-1)<<6)));
                    changed = true;
                }
                continue;
            }
            boolean drill = tool == DRILL || tool == FINE || tool == HEAVY || tool == PITON;
            if ((drill || tool == SOLVENT || tool == SONIC) && depth > 0 && gas < 85) {
                int removed = tool == HEAVY || tool == PITON ? 2 : 1;
                cells[index] = (byte)((value & FOSSIL) | Math.max(0, depth - removed) | DUST);
                changed = true;
            } else if ((tool == BLOWER || tool == BLOWER_S || tool == VACUUM) && (depth == 1 || (depth == 0 && (value & DUST) != 0))) {
                cells[index] = (byte)(value & FOSSIL); changed = true;
            }
            // Work on nearby rock takes priority over accidental contact with an exposed edge.
            if (dx == 0 && dy == 0 && depth == 0 && (value & FOSSIL) != 0) exposedContact = true;
        }
        boolean cooling = tool == BLOWER || tool == BLOWER_S || tool == VACUUM || tool == WATER;
        if (cooling) {
            stress = Math.max(0, stress - 24); heat = Math.max(0, heat - 25); gas = Math.max(0, gas - 30);
        } else if (tool == FIRE && changed) heat = Math.min(100, heat + 12);
        else if (changed && tool != SONIC) { gas = Math.min(100, gas + (tool == HEAVY || tool == PITON ? 4 : 2)); stress = Math.max(0, stress - 3); }
        boolean risky = tool == DRILL || tool == FINE || tool == HEAVY || tool == PITON || tool == SOLVENT || tool == FIRE;
        if (risky && ((!changed && exposedContact) || heat >= 85)) {
            int pressure = tool == FINE || tool == SOLVENT ? 4 : tool == HEAVY || tool == PITON ? 16 : 9;
            stress = Math.min(100, stress + pressure);
            if (stress == 100 && chance(Math.max(25, 100 - (Math.clamp(skillLevel, 1, 10) - 1) * 8))) {
                integrity = Math.max(0, integrity - (tool == HEAVY || tool == PITON ? 6 : 2));
                damaged = true; stress = 72;
            }
        }
        return changed || stress != previousStress || heat != previousHeat || gas != previousGas;
    }
}
