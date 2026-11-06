from pathlib import Path
import colorsys,json,hashlib,shutil,sys
from PIL import Image
ROOT=Path(__file__).resolve().parents[2];ASSETS=ROOT/'src/main/resources/assets/spectrobesreforged';art=ROOT/'art/komainu-handmade';art.mkdir(parents=True,exist_ok=True)
source=ASSETS/'textures/entity/spectrobe/komainu/komainu_color_0.png';sha=hashlib.sha256(source.read_bytes()).hexdigest();backup=art/f'komainu-base-{sha[:12]}.png'
if not backup.exists():shutil.copy2(source,backup)
im=Image.open(source).convert('RGBA')
for variant in [1,2]:
 out=im.copy()
 for y in range(im.height):
  for x in range(im.width):
   r,g,b,a=im.getpixel((x,y))
   if not a:continue
   h,s,v=colorsys.rgb_to_hsv(r/255,g/255,b/255);new=None
   if h>.90 and s>.35:new=(.055,min(.98,s+.12),min(1,v*1.12)) if variant==1 else (.132,min(.96,s+.09),min(1,v*1.22))
   elif .40<h<.58 and s>.25:new=(.155,min(.97,s+.20),min(1,v*1.05)) if variant==1 else (.645,min(.43,s*.60),v*.80)
   elif .75<h<=.90 and s>.35:new=(.005,min(.97,s+.12),min(1,v*1.18)) if variant==1 else (.99,min(.85,s+.03),v*1.06)
   if new:out.putpixel((x,y),(*[round(c*255) for c in colorsys.hsv_to_rgb(*new)],a))
 assert out.size==im.size and out.getchannel('A').tobytes()==im.getchannel('A').tobytes();out.save(source.with_name(f'komainu_color_{variant}.png'))
assert hashlib.sha256(source.read_bytes()).hexdigest()==sha
(art/'palette-source.json').write_text(json.dumps({'base_sha256':sha,'note':'Handmade base remains byte-identical; both recolors only replace RGB and preserve all pixel positions and alpha.'},indent=2)+'\n',encoding='utf-8')
print('Komainu recolors updated from handmade atlas; base and alpha preserved.')
