from pathlib import Path
import json,copy,math,shutil
ROOT=Path(__file__).resolve().parents[2];ASSETS=ROOT/'src/main/resources/assets/spectrobesreforged';SAVE=ROOT/'art/animation-pass/source';SAVE.mkdir(parents=True,exist_ok=True)
NAMES=['komainu','spiko','vilar','segu','inkana','shakin','zoza']
def vec(data,default):
 if isinstance(data,list):return [float(v) if isinstance(v,str) and v.replace('.','',1).replace('-','',1).isdigit() else v for v in data]
 if 'vector' in data:return vec(data['vector'],default)
 keys=sorted((float(k),k) for k in data if k.replace('.','',1).isdigit())
 return vec(data[keys[0][1]],default) if keys else default.copy()
def pose(clip):return {b:{c:vec(v,[1,1,1] if c=='scale' else [0,0,0]) for c,v in channels.items()} for b,channels in clip['bones'].items()}
def fresh(base,length):return {'loop':True,'animation_length':length,'bones':copy.deepcopy(base)}
def wave(clip,bone,channel,axis,amount,phase=0):
 channels=clip['bones'].setdefault(bone,{});baseline=vec(channels.get(channel,[1,1,1] if channel=='scale' else [0,0,0]),[0,0,0]);length=clip['animation_length'];values={}
 for i in range(9):
  v=baseline.copy();v[axis]+=round(amount*(math.sin(i*math.tau/8+phase)-math.sin(phase)),4);values[f'{i*length/8:.4f}']={'vector':v}
 channels[channel]=values
def soften(data,factor):
 if isinstance(data,list):return data
 if 'vector' in data:return data
 first=vec(data,[0,0,0]);result=copy.deepcopy(data)
 if not all(isinstance(v,(int,float)) for v in first):return data
 for k,v in result.items():
  if not k.replace('.','',1).isdigit():continue
  current=vec(v,first)
  if not all(isinstance(v,(int,float)) for v in current):continue
  value=[round(first[i]+(current[i]-first[i])*factor,4) for i in range(3)]
  if isinstance(v,dict):v['vector']=value
  else:result[k]=value
 return result
for key in NAMES:
 f=ASSETS/f'animations/entity/spectrobe/{key}.animation.json';backup=SAVE/f.name
 if key=='komainu' and {'run','blink'}<=set(json.loads(f.read_text(encoding='utf-8'))['animations']):
  print('Preserving handmade Komainu animation set.');continue
 if not backup.exists():shutil.copy2(f,backup)
 data=json.loads(backup.read_text(encoding='utf-8'));clips=data['animations'];original=copy.deepcopy(clips);base=pose(original['idle'])
 geo=ASSETS/f'geo/entity/spectrobe/{key}.geo.json';g=json.loads(geo.read_text(encoding='utf-8'));names={b['name'] for b in g['minecraft:geometry'][0]['bones']}
 if key=='shakin':
  gb=SAVE/'shakin.geo.json'
  if not gb.exists():shutil.copy2(geo,gb)
  for bone in g['minecraft:geometry'][0]['bones']:
   if bone['name'] in ['root','body']:bone['pivot']=[0,10,0]
   if bone['name'] in ['leg_r','leg_l']:bone['parent']='body'
  geo.write_text(json.dumps(g,indent=2)+'\n',encoding='utf-8')
  base={'root':{'rotation':[0,-90,0],'position':[0,0,0]},'body':{'rotation':[0,0,0]},'leg_r':{'rotation':[0,0,0]},'leg_l':{'rotation':[0,0,0]}}
  for name,length,amplitude in [('idle',3.2,1.2),('walk',1.1,2),('sniff',1.6,2.5)]:
   c=fresh(base,length);wave(c,'root','position',1,.18 if name=='idle' else .28);wave(c,'body','rotation',2,amplitude)
   wave(c,'leg_r','rotation',0,3 if name=='idle' else 6);wave(c,'leg_l','rotation',0,-3 if name=='idle' else -6)
   clips[name]=c
 else:
  # Retain the artist's assembled starting pose, especially Zoza's root/head counter-tilt.
  for name in ['idle','walk']:
   for bone,channels in clips[name]['bones'].items():
    factor=.5 if name=='idle' else 1
    if key=='zoza':factor=.35 if 'leg' in bone or 'ear' in bone else .45
    if key=='inkana':factor=.25 if 'hand' in bone else .45
    if key=='segu':factor=.10 if 'arm' in bone else .35
    if factor<1:
     for channel,value in list(channels.items()):
      if channel!='scale':channels[channel]=soften(value,factor)
  if key in ['komainu','segu','spiko','vilar']:
   c=fresh(base,3.2 if key!='segu' else 4)
   body='body';wave(c,body,'position',1,.12 if key!='segu' else .08)
   if key=='komainu':wave(c,'mouth','rotation',0,.8)
   if key=='spiko':wave(c,'tail','rotation',1,2.5);wave(c,'hair','rotation',2,1.3)
   if key=='vilar':
    for i,b in enumerate(['part_2','part_3','part _4','part_5','tail']):wave(c,b,'position',1,.1,i*.45)
   if key=='segu':wave(c,'left_arm','rotation',2,2);wave(c,'right_arm','rotation',2,-2)
   clips['idle']=c
 base=pose(clips['idle'])
 for name,length in [('curious',2.2),('interested',3),('carry',2.8),('happy',1),('eat',1.2)]:
  c=fresh(base,length)
  root='root' if key in ['komainu','spiko','shakin'] else key
  wave(c,root,'position',1,.28 if name=='happy' else .1)
  if key=='komainu':wave(c,'mouth','rotation',0,5 if name=='eat' else 1.5)
  if key=='spiko':
   wave(c,'tail','rotation',1,4 if name=='happy' else 2);wave(c,'hair','rotation',2,1.5)
  if key=='vilar':
   for i,b in enumerate(['part_2','part_3','part _4','part_5','tail']):wave(c,b,'position',1,.16 if name=='happy' else .08,i*.55)
   wave(c,'r','rotation',2,3 if name=='eat' else 1);wave(c,'l','rotation',2,-3 if name=='eat' else -1)
  if key=='segu':wave(c,'left_arm','rotation',2,5 if name=='happy' else 2);wave(c,'right_arm','rotation',2,-5 if name=='happy' else -2)
  if key=='inkana':
   wave(c,'right_hand','position',1,.4 if name=='happy' else .12);wave(c,'left_hand','position',1,.4 if name=='happy' else .12)
   for i,b in enumerate(['fire_base','fire_mid','fire_low']):
    c['bones'].setdefault(b,{})['scale']=[1,1,1];wave(c,b,'scale',1,.06,i*.6);wave(c,b,'rotation',2,1.5,i*.5)
  if key=='shakin':
   wave(c,'body','rotation',2,2 if name=='curious' else 1);wave(c,'leg_r','rotation',0,5 if name=='happy' else 3);wave(c,'leg_l','rotation',0,-5 if name=='happy' else -3)
  if key=='zoza':
   wave(c,'left ear','rotation',2,2);wave(c,'left_ear','rotation',2,-2)
   wave(c,'left tail','rotation',1,3);wave(c,'right tail','rotation',1,-3)
  assert set(c['bones'])<=names,(key,name,set(c['bones'])-names)
  clips[name]=c
 f.write_text(json.dumps(data,indent=2)+'\n',encoding='utf-8')
print('Seven species: calmer idle/movement and five contextual clips each. Shakin rig fins follow body; swimming faces forward.')
