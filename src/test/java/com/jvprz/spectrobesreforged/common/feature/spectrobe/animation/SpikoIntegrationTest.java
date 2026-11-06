package com.jvprz.spectrobesreforged.common.feature.spectrobe.animation;

import com.google.gson.*;
import com.jvprz.spectrobesreforged.common.feature.spectrobe.ai.KomainuMotion;
import javax.imageio.ImageIO;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpikoIntegrationTest {
    private static final Path ROOT=Path.of("src/main/resources/assets/spectrobesreforged");
    private static JsonObject read(Path p)throws Exception{return JsonParser.parseString(Files.readString(p)).getAsJsonObject();}
    @Test void blinkOnlyControlsEyesAndItemFollowsMouth()throws Exception {
        var clips=read(ROOT.resolve("animations/entity/spectrobe/spiko.animation.json")).getAsJsonObject("animations");
        assertEquals(Set.of("eyes"),clips.getAsJsonObject("blink").getAsJsonObject("bones").keySet());
        assertEquals(Set.of("mouth"),clips.getAsJsonObject("cry").getAsJsonObject("bones").keySet());
        assertTrue(clips.has("jump"));
        assertTrue(clips.getAsJsonObject("happy").getAsJsonObject("bones").has("eyes"));
        assertFalse(clips.has("carry"),"Carrying uses the regular pose and the item socket");
        for(var clip:clips.entrySet())if(!Set.of("blink","happy").contains(clip.getKey()))assertFalse(clip.getValue().getAsJsonObject().getAsJsonObject("bones").has("eyes"),"Body controller must not fight blink");
        var bones=read(ROOT.resolve("geo/entity/spectrobe/spiko.geo.json")).getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject().getAsJsonArray("bones");
        var item=bones.asList().stream().map(JsonElement::getAsJsonObject).filter(b->b.get("name").getAsString().equals("item")).findFirst().orElseThrow();
        assertEquals("mouth",item.get("parent").getAsString());
    }
    @Test void handmadeAnimationAndBaseAreUnchangedAndNewColorsHaveTheSameSilhouette()throws Exception {
        assertArrayEquals(Files.readAllBytes(Path.of("art/spiko-handmade/artist-spiko.animation.json")),Files.readAllBytes(ROOT.resolve("animations/entity/spectrobe/spiko.animation.json")));
        var image=ImageIO.read(ROOT.resolve("textures/entity/spectrobe/spiko/spiko_color_0.png").toFile());
        var metadata=read(Path.of("art/spiko-handmade/palette-source.json"));
        String hash=java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(ROOT.resolve("textures/entity/spectrobe/spiko/spiko_color_0.png"))));
        assertEquals(metadata.get("base_sha256").getAsString(),hash);
        for(int color=1;color<3;color++){
            var variant=ImageIO.read(ROOT.resolve("textures/entity/spectrobe/spiko/spiko_color_"+color+".png").toFile());
            assertEquals(image.getWidth(),variant.getWidth());assertEquals(image.getHeight(),variant.getHeight());
            for(int y=0;y<image.getHeight();y++)for(int x=0;x<image.getWidth();x++)assertEquals(image.getRGB(x,y)>>>24,variant.getRGB(x,y)>>>24);
        }
    }
    @Test void cryIsMonoAndGaitsHaveHysteresis()throws Exception {
        byte[] bytes=Files.readAllBytes(ROOT.resolve("sounds/entity/spectrobe/spiko/cry.ogg"));int vorbis=-1;
        for(int i=0;i<bytes.length-16;i++)if(bytes[i]==1&&bytes[i+1]=='v'&&bytes[i+2]=='o'&&bytes[i+3]=='r'){vorbis=i;break;}
        assertTrue(vorbis>=0);assertEquals(1,bytes[vorbis+11]);
        assertFalse(KomainuMotion.running(false,.7F));assertTrue(KomainuMotion.running(true,.7F));
        assertTrue(KomainuMotion.running(false,.9F));assertFalse(KomainuMotion.running(true,.5F));
    }
}
