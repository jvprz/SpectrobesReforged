package com.jvprz.spectrobesreforged.common.worldgen;
import java.util.*;
/** Compact ellipsoid, always with a complete one-block shell around its centre. */
public final class ExcavationPatchShape {
 private ExcavationPatchShape(){}
 public record Offset(int x,int y,int z){}
 public static List<Offset> cells(int radius,int height){
  if(radius<2||radius>3||height<1||height>2)throw new IllegalArgumentException("Invalid patch size");
  var result=new ArrayList<Offset>();
  for(int x=-radius;x<=radius;x++)for(int y=-height;y<=height;y++)for(int z=-radius;z<=radius;z++)
   if((double)(x*x+z*z)/(radius*radius)+(double)(y*y)/(height*height)<=1.0)result.add(new Offset(x,y,z));
  return List.copyOf(result);
 }
}
