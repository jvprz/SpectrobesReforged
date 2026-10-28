package com.jvprz.spectrobesreforged.common.worldgen;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class ExcavationPatchShapeTest {
 @Test void everyPatchHasOneCentreAndACompleteShell(){for(int r=2;r<=3;r++)for(int h=1;h<=2;h++){
  var cells=ExcavationPatchShape.cells(r,h);var set=new HashSet<>(cells);assertEquals(cells.size(),set.size());
  assertTrue(set.contains(new ExcavationPatchShape.Offset(0,0,0)));
  for(var p:List.of(new ExcavationPatchShape.Offset(1,0,0),new ExcavationPatchShape.Offset(-1,0,0),new ExcavationPatchShape.Offset(0,1,0),new ExcavationPatchShape.Offset(0,-1,0),new ExcavationPatchShape.Offset(0,0,1),new ExcavationPatchShape.Offset(0,0,-1)))assertTrue(set.contains(p));
  for(var p:cells){assertTrue(Math.abs(p.x())<=r&&Math.abs(p.y())<=h&&Math.abs(p.z())<=r);assertTrue(set.contains(new ExcavationPatchShape.Offset(-p.x(),-p.y(),-p.z())));}
 }}
 @Test void allHostCellsConnectToTheCentre(){for(int r=2;r<=3;r++)for(int h=1;h<=2;h++){
  var pending=new HashSet<>(ExcavationPatchShape.cells(r,h));var queue=new ArrayDeque<ExcavationPatchShape.Offset>();queue.add(new ExcavationPatchShape.Offset(0,0,0));
  while(!queue.isEmpty()){var p=queue.remove();if(!pending.remove(p))continue;for(int[] d:new int[][]{{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1}})queue.add(new ExcavationPatchShape.Offset(p.x()+d[0],p.y()+d[1],p.z()+d[2]));}
  assertTrue(pending.isEmpty());
 }}
 @Test void invalidSizesAreRejected(){assertThrows(IllegalArgumentException.class,()->ExcavationPatchShape.cells(10,2));assertThrows(IllegalArgumentException.class,()->ExcavationPatchShape.cells(2,0));}
}
