package com.jvprz.spectrobesreforged.common.feature.excavation;

/** A raw specimen's saved host and grade determine its initial geological layers. */
public record ExcavationProfile(int rock, int coverLayers, int openingLayers, int minDepth, int maxDepth) {
    public static final int STONE=0, DEEPSLATE=1, SANDSTONE=2, CLAY=3, RED_SANDSTONE=4;
    public static ExcavationProfile forSpecimen(String key,int terrain,int rock) {
        int difficulty=key.endsWith("_a_plus")?3:key.endsWith("_a")?2:key.endsWith("_b")?1:
                key.equals("vilar")||key.equals("inkana")||key.equals("spiko")?1:0;
        int minimum=difficulty>=2||rock==DEEPSLATE?3:2;
        int maximum=difficulty==0&&rock!=DEEPSLATE?3:4;
        int cover=terrain==ExcavationPuzzle.ROCK?0:Math.min(3,1+(difficulty+1)/2);
        return new ExcavationProfile(Math.clamp(rock,0,4),cover,Math.min(4,2+difficulty),minimum,maximum);
    }
    public static String rockTexture(int rock) {
        return switch(rock){case DEEPSLATE->"deepslate";case SANDSTONE->"sandstone";case CLAY->"clay";case RED_SANDSTONE->"red_sandstone";default->"stone";};
    }
    public static String openingTexture(int remaining,int terrain,int rock) {
        if(remaining>=3)return switch(terrain){case ExcavationPuzzle.ICE->"snow";case ExcavationPuzzle.SAND->rock==RED_SANDSTONE?"red_sandstone_top":"sandstone_top";case ExcavationPuzzle.MUD->"mud";default->rockTexture(rock);};
        if(remaining==2)return switch(terrain){case ExcavationPuzzle.ICE->"packed_ice";case ExcavationPuzzle.SAND->rock==RED_SANDSTONE?"red_sandstone_top":"sandstone_top";case ExcavationPuzzle.MUD->"mud";case ExcavationPuzzle.WATER_TERRAIN->"sand";case ExcavationPuzzle.LAVA->"basalt_side";default->rockTexture(rock);};
        return switch(terrain){case ExcavationPuzzle.ICE->"ice";case ExcavationPuzzle.SAND->rock==RED_SANDSTONE?"red_sand":"sand";case ExcavationPuzzle.MUD,ExcavationPuzzle.WATER_TERRAIN->"clay";case ExcavationPuzzle.LAVA->"basalt_side";default->rock==CLAY?"clay":rock==SANDSTONE?"sand":"gravel";};
    }
}
