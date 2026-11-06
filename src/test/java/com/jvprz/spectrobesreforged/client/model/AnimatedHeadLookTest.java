package com.jvprz.spectrobesreforged.client.model;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnimatedHeadLookTest {
    private static Quaternionf rotation(AnimatedHeadLook.Pose p) { return new Quaternionf().rotationZYX(p.rz(),p.ry(),p.rx()); }
    private static Vector3f anchor(AnimatedHeadLook.Pose p,Vector3f local,Vector3f scale) {
        return rotation(p).transform(new Vector3f(local).mul(scale)).add(-p.x(),p.y(),p.z());
    }
    @Test void authoredPoseIsRetainedWhenNoLookIsRequested() {
        var pose=new AnimatedHeadLook.Pose(1,14,-4,(float)Math.toRadians(35),0,.12F);
        assertEquals(pose,AnimatedHeadLook.compose(pose,new Vector3f(0,10,-3),new Vector3f(1),0,0,0));
    }
    @Test void zozaHeadCompensationSurvivesItsTiltedRootAndAnimatedHeight() {
        var pose=new AnimatedHeadLook.Pose(0,0,0,(float)Math.toRadians(35),0,0);
        var result=AnimatedHeadLook.compose(pose,new Vector3f(),new Vector3f(1),.15F,.06F,0);
        var expected=rotation(pose).rotateY(.15F).rotateX(.06F);
        assertEquals(1,Math.abs(expected.dot(rotation(result))),.00001);
        assertEquals(pose.x(),result.x(),.00001);assertEquals(pose.y(),result.y(),.00001);assertEquals(pose.z(),result.z(),.00001);
    }
    @Test void originPivotCannotMakeTheNeckOrbitOrDetachWhenLooking() {
        var pose=new AnimatedHeadLook.Pose(0,-1,-2,.20F,-.04F,.08F);
        var neck=new Vector3f(0,10,-3);var scale=new Vector3f(1,.95F,1.1F);
        var result=AnimatedHeadLook.compose(pose,neck,scale,.27F,.12F,.03F);
        assertTrue(anchor(pose,neck,scale).distance(anchor(result,neck,scale))<.00001);
    }
    @Test void animatedTranslationIsPreservedAcrossDifferentPosesWithoutAccumulation() {
        var neck=new Vector3f(0,9,-5);var scale=new Vector3f(1);
        for(int frame=0;frame<100;frame++) {
            var pose=new AnimatedHeadLook.Pose(0,-11+frame*.01F,-11,.03F*frame,0,0);
            var result=AnimatedHeadLook.compose(pose,neck,scale,.2F,.06F,0);
            assertTrue(anchor(pose,neck,scale).distance(anchor(result,neck,scale))<.00002);
            assertEquals(result,AnimatedHeadLook.compose(pose,neck,scale,.2F,.06F,0));
        }
    }
}
