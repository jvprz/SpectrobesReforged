package com.jvprz.spectrobesreforged.common.feature.incubator.logic;

/** Responsive game-event envelope. Values are game signal strength, not microphone Hz. */
public final class AwakeningSignal {
    private int impulse,hold,decay,source;
    private long lastEvent=Long.MIN_VALUE;
    private double filtered;
    public void receive(int value,long tick){
        value=Math.clamp(value,0,16);
        if(value==0)return;
        // Events arriving together are order-independent; later softer sounds replace old peaks.
        impulse=lastEvent==tick?Math.max(impulse,value):value;
        lastEvent=tick;hold=5;decay=0;
    }
    public int tick(int music){
        music=Math.clamp(music,0,16);
        source=(impulse>0?1:0)|(music>0?2:0);
        int target=Math.max(impulse,music);
        filtered+=(target-filtered)*0.5;
        if(target==0&&filtered<0.5)filtered=0;
        if(hold>0)hold--;else if(++decay>=2){decay=0;impulse=Math.max(0,impulse-1);}
        return Math.clamp((int)Math.round(filtered),0,16);
    }
    public int source(){return source;}
    public void reset(){impulse=hold=decay=source=0;filtered=0;lastEvent=Long.MIN_VALUE;}
}
