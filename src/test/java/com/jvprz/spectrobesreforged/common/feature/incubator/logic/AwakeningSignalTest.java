package com.jvprz.spectrobesreforged.common.feature.incubator.logic;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class AwakeningSignalTest {
 @Test void softerLaterEventsReplaceOldPeaks(){var s=new AwakeningSignal();s.receive(16,1);for(int i=0;i<5;i++)s.tick(0);s.receive(2,6);assertTrue(s.tick(0)<12);assertTrue(s.tick(0)<8);}
 @Test void simultaneousEventsAreOrderIndependent(){var a=new AwakeningSignal();var b=new AwakeningSignal();a.receive(3,1);a.receive(14,1);b.receive(14,1);b.receive(3,1);for(int i=0;i<30;i++)assertEquals(a.tick(0),b.tick(0));}
 @Test void steadySignalsReachAllColorBoundaries(){for(int n:new int[]{0,5,6,10,11,16}){var s=new AwakeningSignal();int value=0;for(int t=0;t<40;t++){s.receive(n,t);value=s.tick(0);}assertEquals(n,value);}}
 @Test void noSoundDecaysToZeroAndResetClearsEverything(){var s=new AwakeningSignal();s.receive(16,1);for(int t=0;t<100;t++)s.tick(0);assertEquals(0,s.tick(0));assertEquals(0,s.source());s.receive(16,2);s.tick(16);s.reset();assertEquals(0,s.tick(0));assertEquals(0,s.source());}
 @Test void musicAndEventsKeepStrongestSourceRule(){var s=new AwakeningSignal();for(int i=0;i<20;i++)s.tick(12);assertEquals(2,s.source());s.receive(4,21);assertEquals(12,s.tick(12));assertEquals(3,s.source());}
 @Test void outOfRangeValuesAreClamped(){var s=new AwakeningSignal();s.receive(999,1);for(int i=0;i<50;i++)assertTrue(s.tick(999)<=16);s.reset();s.receive(-10,2);assertEquals(0,s.tick(-10));}
}
