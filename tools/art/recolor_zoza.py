from pathlib import Path
import colorsys,hashlib,json
from PIL import Image
ROOT=Path(__file__).resolve().parents[2]
FOLDER=ROOT/'src/main/resources/assets/spectrobesreforged/textures/entity/spectrobe/zoza'
base=FOLDER/'zoza_color_0.png';before=hashlib.sha256(base.read_bytes()).hexdigest();im=Image.open(base).convert('RGBA')
assert im.size==(128,128),'Use the handmade 128x128 atlas.'
geometry=ROOT/'src/main/resources/assets/spectrobesreforged/geo/entity/spectrobe/zoza.geo.json';geo_hash=hashlib.sha256(geometry.read_bytes()).hexdigest()
records={}
for variant in [1,2]:
 out=im.copy();mapping={}
 for y in range(im.height):
  for x in range(im.width):
   red,green,blue,a=im.getpixel((x,y))
   if not a:continue
   h,s,v=colorsys.rgb_to_hsv(red/255,green/255,blue/255);new=None;role='unchanged'
   # Tail border has its own neutral/cream recolor. White ear patterns and claws stay intact.
   if (red,green,blue) in [(255, 222, 145), (234, 199, 118), (255, 222, 146), (239, 204, 125)]:
    role='tail_edge';new=(.14,s*.78,min(1,v*1.02)) if variant==1 else (.68,s*.24,v*.94)
   elif h<.065 and s>.40 and red>90:
    role='tail';new=(.13,s*.9,min(1,v*1.05)) if variant==1 else (.685,s*.32,v*.9)
   elif .075<h<.20 and s>.35:
    role='coat';new=(.045,min(.98,s*.95+.10),v*.97) if variant==1 else (.077,min(.97,s*.96+.08),v*.96)
   elif .70<h<.88 and s>.30:
    # UV regions distinguish terminal ear blades from the dark neck ruff.
    tip=(50<=x<60 and 38<=y<42) or (8<=x<18 and 58<=y<62)
    role='ear_tip' if tip else 'mane'
    if variant==1:new=(.88,min(.9,s*.97+.08),min(1,v*1.1)) if tip else (.80,s*.94,v*.83)
    else:new=(.45,min(.85,s*.90),v*1.02) if tip else (.49,s*.85,v*.76)
   if new:
    rgb=tuple(round(c*255) for c in colorsys.hsv_to_rgb(*new));out.putpixel((x,y),(*rgb,a));mapping[f'{role}:{red},{green},{blue}']=list(rgb)
 assert out.size==im.size and out.getchannel('A').tobytes()==im.getchannel('A').tobytes()
 # No resizing, brushes, morphology or added pixels: only RGB replacements.
 out.save(FOLDER/f'zoza_color_{variant}.png');records[str(variant)]=mapping
assert hashlib.sha256(base.read_bytes()).hexdigest()==before,'Handmade base must stay byte-identical.'
assert hashlib.sha256(geometry.read_bytes()).hexdigest()==geo_hash,'Geometry must stay unchanged.'
(ROOT/'art/zoza/handmade-palettes.json').write_text(json.dumps({'base_sha256':before,'geometry_sha256':geo_hash,'resolution':[128,128],'note':'Uses the CURRENT handmade texture as source. Base file is byte-identical; only RGB entries of variants 1/2 change. Every UV position and alpha value is preserved.','variants':records},indent=2)+'\n',encoding='utf-8')
print('Handmade base and geometry unchanged; two RGB-only recolors generated; alpha identical.')
