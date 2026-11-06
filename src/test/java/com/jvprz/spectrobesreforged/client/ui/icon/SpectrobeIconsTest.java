package com.jvprz.spectrobesreforged.client.ui.icon;

import org.junit.jupiter.api.Test;
import javax.imageio.ImageIO;
import java.util.HashSet;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class SpectrobeIconsTest {
    @Test void knownSpeciesResolveDistinctValid16PixelVariants() throws Exception {
        for (String species : new String[]{"komainu","spiko","vilar","segu","inkana","shakin","zoza"}) {
            var paths = new HashSet<String>();
            int[] baseAlpha = null;
            var palettes = new HashSet<Integer>();
            for (int color=0;color<3;color++) {
                var path=SpectrobeIcons.icon(species,color).getPath();
                assertTrue(paths.add(path),species);
                try(var stream=getClass().getResourceAsStream("/assets/spectrobesreforged/"+path)) {
                    assertNotNull(stream,path);
                    var image=ImageIO.read(stream);assertNotNull(image,path);
                    assertEquals(16,image.getWidth());assertEquals(16,image.getHeight());
                    int[] alpha=new int[256], pixels=new int[256];
                    for(int y=0;y<16;y++)for(int x=0;x<16;x++) {
                        int pixel=image.getRGB(x,y);pixels[y*16+x]=pixel;alpha[y*16+x]=pixel>>>24;
                        assertTrue(alpha[y*16+x]==0 || alpha[y*16+x]==255,"Pixel art must stay crisp");
                    }
                    if(baseAlpha==null)baseAlpha=alpha;else assertArrayEquals(baseAlpha,alpha,"Silhouette must match");
                    assertTrue(palettes.add(Arrays.hashCode(pixels)),"Variant must have its own colors");
                }
            }
        }
    }
    @Test void placeholdersRetainTheirBaseIconAndVariantsClamp() {
        assertEquals("textures/gui/icons/harumi.png",SpectrobeIcons.icon("harumi",2).getPath());
        assertEquals(SpectrobeIcons.icon("komainu",0),SpectrobeIcons.icon(" Komainu ",-1));
        assertEquals(SpectrobeIcons.icon("shakin",2),SpectrobeIcons.icon("SHAKIN",99));
    }
}
