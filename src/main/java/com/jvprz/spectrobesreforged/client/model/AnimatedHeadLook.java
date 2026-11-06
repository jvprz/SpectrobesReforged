package com.jvprz.spectrobesreforged.client.model;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Compose a small local look onto the animated pose while keeping its neck anchor fixed. */
public final class AnimatedHeadLook {
    private AnimatedHeadLook() {}
    public record Pose(float x,float y,float z,float rx,float ry,float rz) {}
    public static Pose compose(Pose animated,Vector3f anchorOffset,Vector3f scale,float yaw,float pitch,float roll) {
        if(yaw==0 && pitch==0 && roll==0)return animated;
        var original=new Quaternionf().rotationZYX(animated.rz(),animated.ry(),animated.rx());
        var result=new Quaternionf(original).rotateY(yaw).rotateX(pitch).rotateZ(roll);
        var local=new Vector3f(anchorOffset).mul(scale);
        var correction=original.transform(new Vector3f(local)).sub(result.transform(new Vector3f(local)));
        // Explicit Z-Y-X extraction matching GeckoLib's render order. Do not use
        // the bundled JOML extractor, whose X denominator differs in this version.
        result.normalize();
        var euler=new Vector3f(
            (float)Math.atan2(2*(result.w*result.x+result.y*result.z),1-2*(result.x*result.x+result.y*result.y)),
            (float)Math.asin(Math.clamp(2*(result.w*result.y-result.z*result.x),-1,1)),
            (float)Math.atan2(2*(result.w*result.z+result.x*result.y),1-2*(result.y*result.y+result.z*result.z)));
        // GeckoLib negates bone position X when translating the render matrix.
        return new Pose(animated.x()-correction.x,animated.y()+correction.y,animated.z()+correction.z,
            euler.x,euler.y,euler.z);
    }
}
