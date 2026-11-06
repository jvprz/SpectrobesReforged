"""Recolor the handmade Spiko atlas without changing its drawing or transparency."""
from pathlib import Path
import colorsys, json, hashlib
from PIL import Image
ROOT=Path(__file__).resolve().parents[2]
ASSETS=ROOT/'src/main/resources/assets/spectrobesreforged'
def recolor():
    source=ASSETS/'textures/entity/spectrobe/spiko/spiko_color_0.png'
    base=Image.open(source).convert('RGBA')
    geo=json.loads((ASSETS/'geo/entity/spectrobe/spiko.geo.json').read_text(encoding='utf-8'))
    regions={}
    for bone in geo['minecraft:geometry'][0]['bones']:
        points=set()
        for cube in bone.get('cubes',[]):
            u,v=cube['uv'];w,h,d=map(int,cube['size'])
            points.update((x,y) for x in range(u,u+2*(w+d)) for y in range(v+d,v+d+h))
            points.update((x,y) for x in range(u+d,u+d+2*w) for y in range(v,v+d))
        regions[bone['name']]=points
    feet=set().union(*(v for k,v in regions.items() if k.startswith('leg_')))
    jaw=regions['mouth']
    images=[base]
    for variant in (1,2):
        out=base.copy()
        for y in range(base.height):
            for x in range(base.width):
                r,g,b,a=base.getpixel((x,y))
                if not a: continue
                h,s,v=colorsys.rgb_to_hsv(r/255,g/255,b/255);new=None
                if .35<h<.53 and s>.4: new=(.46,.35,min(1,.22+v*.78)) if variant==1 else (.30,.85,v*.90)
                elif .14<h<.23 and s>.5: new=(.59,.62,v*.58) if variant==1 else (.08,.42,v)
                elif .045<h<.17 and s>.2:
                    if (x,y) in jaw:new=(.54,.04,v*.96) if variant==1 else (.57,.12,v*.95)
                    else:new=(.46,.92,v*.64) if variant==1 else (.57,.12,v*.95)
                elif (h<.04 or h>.90) and s>.4:
                    if (x,y) in feet:new=(.10,.96,min(1,v*1.18)) if variant==1 else (.77,.55,v*.85)
                    else:new=(.15,.95,min(1,v*1.12)) if variant==1 else (.88,.9,min(1,v*1.15))
                elif .70<h<=.90 and s>.3:new=(.10,.96,min(1,v*1.12)) if variant==1 else (.77,.55,v*.85)
                if new:out.putpixel((x,y),(*[round(c*255) for c in colorsys.hsv_to_rgb(*new)],a))
        assert out.getchannel('A').tobytes()==base.getchannel('A').tobytes()
        out.save(source.with_name(f'spiko_color_{variant}.png'));images.append(out)
    art=ROOT/'art/spiko-handmade';art.mkdir(parents=True,exist_ok=True)
    preview=Image.new('RGBA',(base.width*3,base.height))
    for i,im in enumerate(images):preview.paste(im,(i*base.width,0))
    preview.resize((1152,384),Image.Resampling.NEAREST).save(art/'recolors-preview.png')
    (art/'palette-source.json').write_text(json.dumps({'base_sha256':hashlib.sha256(source.read_bytes()).hexdigest(),'note':'RGB-only palette substitution; handmade drawing, base file and alpha preserved.'},indent=2)+'\n',encoding='utf-8')
if __name__=='__main__':recolor()
