package com.jvprz.spectrobesreforged.common.feature.prizmod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/** Additive save field; old collections receive neutral training/quality defaults. */
public record SpectrobeProgress(int xp,boolean crown,int quality,int battles,boolean cooperative) {
    public static final SpectrobeProgress EMPTY=new SpectrobeProgress(0,false,-1,0);
    public static final Codec<SpectrobeProgress> CODEC=RecordCodecBuilder.create(i->i.group(
            Codec.INT.optionalFieldOf("xp",0).forGetter(SpectrobeProgress::xp),
            Codec.BOOL.optionalFieldOf("crown",false).forGetter(SpectrobeProgress::crown),
            Codec.INT.optionalFieldOf("quality",-1).forGetter(SpectrobeProgress::quality),
            Codec.INT.optionalFieldOf("battles",0).forGetter(SpectrobeProgress::battles),
            Codec.BOOL.optionalFieldOf("cooperative",false).forGetter(SpectrobeProgress::cooperative)
    ).apply(i,SpectrobeProgress::new));
    public SpectrobeProgress(int xp,boolean crown,int quality,int battles){this(xp,crown,quality,battles,false);}
    public SpectrobeProgress {xp=Math.clamp(xp,0,10000000);quality=Math.clamp(quality,-1,100);crown=crown||quality==100;battles=Math.max(0,battles);}
    public SpectrobeProgress withXp(int value){return new SpectrobeProgress(value,crown,quality,battles,cooperative);}
    public SpectrobeProgress withCrown(){return new SpectrobeProgress(xp,true,quality,battles,cooperative);}
    public SpectrobeProgress withCooperative(){return new SpectrobeProgress(xp,crown,quality,battles,true);}
    public SpectrobeProgress won(){return new SpectrobeProgress(xp,crown,quality,Math.min(1000000,battles+1),cooperative);}
}
