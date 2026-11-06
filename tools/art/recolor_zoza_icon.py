from pathlib import Path
import colorsys,hashlib,json
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[2]
folder=ROOT/'src/main/resources/assets/spectrobesreforged/textures/gui/icons'
base=folder/'zoza.png';sha=hashlib.sha256(base.read_bytes()).hexdigest();src=Image.open(base).convert('RGBA');assert src.size==(16,16)
art=ROOT/'art/zoza';copy=art/('zoza-icon-source-'+sha[:12]+'.png')
if not copy.exists():copy.write_bytes(base.read_bytes())
for variant in [1,2]:
 out=src.copy()
 for y in range(16):
  for x in range(16):
   red,green,blue,a=src.getpixel((x,y))
   if not a:continue
   h,s,v=colorsys.rgb_to_hsv(red/255,green/255,blue/255);new=None
   if s>.35 and (.075<h<.20):
    new=(.045,min(.96,s*.95+.08),v*.97) if variant==1 else (.077,min(.95,s*.96+.06),v*.96)
   elif s>.35 and .70<h<.90:
    new=(.875,s*.92,min(1,v*1.04)) if variant==1 else (.49,s*.85,v*.92)
   elif s>.35 and (h>.90 or h<.06):
    new=(.13,s*.85,min(1,v*1.08)) if variant==1 else (.685,s*.32,min(1,v*1.04))
   if new:out.putpixel((x,y),(*[round(c*255) for c in colorsys.hsv_to_rgb(*new)],a))
 assert out.getchannel('A').tobytes()==src.getchannel('A').tobytes();out.save(folder/f'zoza_color_{variant}.png')
assert hashlib.sha256(base.read_bytes()).hexdigest()==sha
preview=Image.new('RGB',(640,200),(22,27,33));d=ImageDraw.Draw(preview)
for i in range(3):
 fn='zoza.png' if i==0 else f'zoza_color_{i}.png';im=Image.open(folder/fn).convert('RGBA').resize((128,128),Image.Resampling.NEAREST);preview.paste(im,(38+i*210,45),im);d.text((38+i*210,15),['Sprite original','Recolor 1','Recolor 2'][i],fill='white')
preview.save(art/'zoza-icons-preview.png');print('Three 16x16 Zoza icons ready; original byte-identical and alpha preserved.')
