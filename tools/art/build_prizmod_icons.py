from pathlib import Path
import colorsys,json,hashlib
from PIL import Image,ImageDraw
ROOT=Path(__file__).resolve().parents[2]
ICONS=ROOT/'src/main/resources/assets/spectrobesreforged/textures/gui/icons'
ART=ROOT/'art/prizmod-icons'; SOURCES=ART/'sources';SOURCES.mkdir(parents=True,exist_ok=True)
NAMES=['komainu','spiko','vilar','segu','inkana','shakin']
# Each pair is a shadow/highlight ramp. Geometry and silhouettes remain hand-authored.
P={
'komainu':[
 {'body':('#760425','#ff4479'),'trim':('#176269','#75f4e6'),'nose':('#39102a','#93556b')},
 {'body':('#922b00','#ff942c'),'trim':('#9b9600','#ffff5b'),'nose':('#630a12','#d62025')},
 {'body':('#987000','#ffe329'),'trim':('#404a7b','#a8b3df'),'nose':('#4e151d','#b03937')}],
'spiko':[
 {'body':('#004737','#009f7a'),'mark':('#7e9d00','#f4ff25'),'horn':('#ab7b58','#ffefa1'),'jaw':('#ab7b58','#ffefa1'),'foot':('#780030','#fa005a'),'eye':('#6d0008','#ee162c')},
 {'body':('#336d77','#9be6c7'),'mark':('#19395f','#3e6e94'),'horn':('#004b43','#009379'),'jaw':('#687173','#d0d6d8'),'foot':('#ab6600','#ffdd18'),'eye':('#858700','#efff20')},
 {'body':('#0d6305','#7bc72a'),'mark':('#c7834e','#ffd19d'),'horn':('#748991','#e0edf8'),'jaw':('#7d8d95','#deeaf4'),'foot':('#33203f','#865b99'),'eye':('#70004d','#ff20c5')}],
'vilar':[
 {'body':('#8b0700','#f13522'),'gold':('#a77900','#ffcf26'),'dark':('#341723','#6b3a44'),'tan':('#96744a','#f4d893'),'eye':('#a87b00','#ffe777')},
 {'body':('#9a3000','#ff8f16'),'gold':('#a96b00','#ffd02b'),'dark':('#3b1c12','#87482c'),'tan':('#96744a','#f4d893'),'eye':('#007e5d','#78ffd2')},
 {'body':('#937000','#ffe32b'),'gold':('#301221','#81394c'),'dark':('#393109','#857030'),'tan':('#7e7c54','#d0c99f'),'eye':('#00813f','#7fff95')}],
'segu':[
 {'body':('#568232','#c4f28a'),'mark':('#8e8800','#ffef38'),'dark':('#172419','#465635'),'spike':('#26361e','#637445'),'eye':('#a9aa00','#ffff4d')},
 {'body':('#0f5f2a','#4cc551'),'mark':('#bc2628','#ff803a'),'dark':('#04171b','#183b3f'),'spike':('#12262a','#46645d'),'eye':('#b6c4b1','#f4f5e7')},
 {'body':('#036e7d','#a3e5ed'),'mark':('#a12a73','#fc87c3'),'dark':('#051a20','#1b3b42'),'spike':('#112b2d','#456760'),'eye':('#238900','#73ef21')}],
'inkana':[
 {'stone':('#524031','#bb946c'),'dark':('#271a18','#694336'),'crest':('#9e3e00','#ff9b42'),'gold':('#a78b37','#fff09b'),'red':('#790d1b','#f22d3b'),'fire':('#c42d00','#fff2a0')},
 {'stone':('#8b2505','#fa7f22'),'dark':('#092d31','#3f8e87'),'crest':('#a3440b','#ff9c4d'),'gold':('#357c73','#a1e2bc'),'red':('#aa6200','#ffd22a'),'fire':('#d97400','#fff9a7')},
 {'stone':('#354a1f','#b7cb74'),'dark':('#0b222c','#2b5360'),'crest':('#8b9b08','#edff72'),'gold':('#4d9229','#b7ed7a'),'red':('#9c8a00','#ffde23'),'fire':('#777800','#fff48f')}],
'shakin':[
 {'body':('#501582','#ac41ef'),'blade':('#75a9ad','#e6fffa'),'armor':('#b18e17','#fff16b'),'eye':('#117a2e','#8af895'),'seam':('#7e161c','#e8424d')},
 {'body':('#691f62','#d65ab7'),'blade':('#8e7099','#e4cbe6'),'armor':('#2b805f','#a0edbd'),'eye':('#8b8519','#efe38a'),'seam':('#7d3425','#ce7753')},
 {'body':('#063f79','#238bdb'),'blade':('#81adaf','#e1f5ef'),'armor':('#824420','#de9d63'),'eye':('#137b36','#8ae395'),'seam':('#7e2222','#d94b46')}]
}
def rgb(s):return tuple(bytes.fromhex(s[1:]))
def mix(pair,t):
 a,b=map(rgb,pair);return tuple(round(x+(y-x)*max(0,min(1,t))) for x,y in zip(a,b))
def classify(name,c,x,y):
 r,g,b,a=c;h,s,v=colorsys.rgb_to_hsv(r/255,g/255,b/255)
 if name=='komainu':
  if .40<h<.58:return 'trim',(v-.25)/.65
  if .80<h<.91:return 'nose',(v-.15)/.40
  if h>.90 and s>.35:return 'body',(v-.22)/.75
 elif name=='spiko':
  if .25<h<.53:return 'body',(v-.25)/.45
  if .16<h<.22:return 'mark',(v-.65)/.35
  if .09<h<.16:return ('jaw' if y>=9 else 'horn'),(v-.65)/.35
  if .70<h<.95:return 'foot',(v-.35)/.40
  if h<.04 and s>.5:return 'eye',v
 elif name=='vilar':
  if h<.04 and s>.4:return 'body',(v-.40)/.50
  if .14<h<.20:return ('eye' if x<7 and y<6 else 'gold'),(v-.50)/.45
  if .06<h<.14:return 'tan',(v-.4)/.55
  if .80<h<.99:return 'dark',(v-.18)/.25
 elif name=='segu':
  if r==g==b:
   if r==255:return 'eye',1
   return ('spike' if y<5 or r>100 else 'dark'),(v-.10)/.70
  if .12<h<.24 and s>.5:return 'mark',(v-.65)/.35
  if .10<h<.28:return 'body',(v-.42)/.58
 elif name=='inkana':
  if s<.16:return None
  if y<5 and r>140:return 'fire',(v-.55)/.45
  if .11<h<.20:return 'gold',(v-.60)/.40
  if (h<.025 or h>.97) and s>.5:return 'red',(v-.35)/.50
  if .025<h<.10 and s>.55:return 'crest',(v-.50)/.50
  if .04<h<.16:return ('dark' if r<100 else 'stone'),(v-.15)/.55
 elif name=='shakin':
  if r==g==b and r>100:return 'blade',(v-.5)/.5
  if .15<h<.20:return 'armor',(v-.65)/.35
  if .20<h<.45:return 'eye',v
  if h>.90 and s>.4:return 'seam',(v-.4)/.5
  if .69<h<.90:return 'body',(v-.25)/.60
 return None
report={}
for name in NAMES:
 source=SOURCES/(name+'.png')
 if not source.exists():source.write_bytes((ICONS/(name+'.png')).read_bytes())
 original=Image.open(source).convert('RGBA');assert original.size==(16,16)
 report[name]={'source_sha256':hashlib.sha256(source.read_bytes()).hexdigest(),'variants':[]}
 for variant in range(3):
  im=original.copy()
  for y in range(16):
   for x in range(16):
    c=original.getpixel((x,y))
    if not c[3]:continue
    role=classify(name,c,x,y)
    if role:
     family,t=role;im.putpixel((x,y),(*mix(P[name][variant][family],t),c[3]))
  assert im.getchannel('A').tobytes()==original.getchannel('A').tobytes()
  filename=name+('' if variant==0 else '_color_'+str(variant))+'.png';im.save(ICONS/filename)
  report[name]['variants'].append(filename)
# Contact sheet: exact nearest-neighbor display, showing both UI background colors.
canvas=Image.new('RGB',(930,650),(22,27,33));d=ImageDraw.Draw(canvas)
for row,name in enumerate(NAMES):
 d.text((12,32+row*105),name.capitalize(),fill=(231,239,244))
 for variant in range(3):
  fn=name+('' if variant==0 else '_color_'+str(variant))+'.png';im=Image.open(ICONS/fn).convert('RGBA')
  for bg in range(2):
   x=130+variant*255+bg*110;y=20+row*105;d.rectangle((x-3,y-3,x+83,y+83),fill=(43,53,65) if bg==0 else (214,221,225))
   icon=im.resize((80,80),Image.Resampling.NEAREST);canvas.paste(icon,(x,y),icon)
for i,t in enumerate(['Base','Variante 1','Variante 2']):d.text((130+i*255,4),t,fill='white')
canvas.save(ART/'prizmod-icons-preview.png')
(ART/'palettes.json').write_text(json.dumps({'note':'Original hand-authored 16x16 silhouettes and alpha retained; species-specific contrast ramps updated to current texture variants. Source PNGs retained for repeatable edits.','sprites':report,'ramps':P},indent=2)+'\n',encoding='utf-8')
print('18 icons generated at 16x16; original alpha and silhouettes verified.')
