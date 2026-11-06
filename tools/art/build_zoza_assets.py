raise SystemExit("Archived geometry generator: do not overwrite the handmade texture. Run tools/art/recolor_zoza.py for RGB-only recolors.")
from pathlib import Path
from PIL import Image,ImageDraw
import json,math,shutil
r=Path(__file__).resolve().parents[2];a=r/'src/main/resources/assets/spectrobesreforged';gfile=a/'geo/entity/spectrobe/zoza.geo.json';t=a/'textures/entity/spectrobe/zoza';anim=a/'animations/entity/spectrobe/zoza.animation.json';backup=r/'art/zoza/source';backup.mkdir(parents=True,exist_ok=True)
for f in [gfile,anim,*t.glob('*.png')]:
 if not (backup/f.name).exists():shutil.copy2(f,backup/f.name)
g=json.loads((backup/gfile.name).read_text(encoding='utf-8'));geo=g['minecraft:geometry'][0];bones=geo['bones'];root=next(b for b in bones if b['name']=='zoza');head=next(b for b in bones if b['name']=='bb_main');head['name']='head';head['parent']='zoza';head['pivot']=[0,6,-3]
legs=head['cubes'][4:];head['cubes']=head['cubes'][:4]
for name,cubes,pivot in [('leg_fr',[legs[0]],[2,4,-2]),('leg_fl',[legs[1]],[-2,4,-2]),('leg_br',[legs[2],legs[3]],[2,5,3]),('leg_bl',[legs[4],legs[5]],[-2,5,3])]:bones.append({'name':name,'parent':'zoza','pivot':pivot,'cubes':cubes})
for b in bones:
 if b['name'] in ['left ear','left_ear']:
  b['parent']='head';b['pivot']=[1.5 if b['name']=='left ear' else -1.5,9,-3]
body=next(b for b in bones if b['name']=='body');tails=body['cubes'][1:3];body['cubes']=[body['cubes'][0],*body['cubes'][3:]]
for i,c in enumerate(tails):bones.append({'name':'tail_'+('left' if i==0 else 'right'),'parent':'zoza','pivot':c['pivot'],'cubes':[c]})
# Small neck tufts: keep the artist's overall shape, add six low-poly fur points.
for i,(x,z,angle) in enumerate([(-3,-3,-25),(2,-3,25),(-3,-1,-20),(2,-1,20),(-2,1,-20),(1,1,20)]):
 body['cubes'].append({'origin':[x,5,z],'size':[1,2,2],'pivot':[x+.5,6,z+1],'rotation':[0,0,angle],'uv':[72+i*6,0]})
geo['description']['identifier']='geometry.zoza';gfile.write_text(json.dumps(g,indent=2)+'\n',encoding='utf-8')
base=Image.open(backup/'zoza_color_0.png').convert('RGBA');palettes=[{'coat':[(128,98,0),(249,226,29),(255,246,114)],'mane':[(37,24,65),(91,53,143),(152,105,206)],'tail':[(153,35,31),(233,86,66),(255,222,145)],'tip':[(51,24,99),(121,65,182),(195,164,236)]},{'coat':[(105,24,8),(235,68,12),(255,149,40)],'mane':[(37,18,56),(91,33,107),(154,76,158)],'tail':[(160,111,8),(247,203,39),(255,249,163)],'tip':[(117,12,91),(223,50,161),(255,173,225)]},{'coat':[(97,32,4),(220,106,9),(255,180,43)],'mane':[(14,41,45),(27,78,80),(67,131,126)],'tail':[(61,58,91),(130,123,162),(218,216,237)],'tip':[(13,99,80),(42,171,139),(178,234,210)]}]
for v,p in enumerate(palettes):
 im=base.copy();px=im.load()
 for y in range(128):
  for x in range(128):
   old=base.getpixel((x,y));rgb=old[:3]
   if not old[3]:continue
   role='coat' if rgb in [(214,212,36),(224,221,46)] else 'mane' if rgb==(125,27,158) else 'tail' if rgb==(197,22,82) else 'coat' if rgb==(218,147,38) else 'mane' if rgb in [(91,188,244),(83,97,116)] else 'tip' if rgb==(244,134,134) else None
   if role:
    # Cohesive clusters of two shades, without random texture noise.
    shade=0 if (x%8==0 or y%8==7) else 2 if y%8==0 else 1
    px[x,y]=(*p[role][shade],old[3])
 # Draw the front face: clear dark eye masks, white eyes and small nose.
 face=(35,39)
 for xx,yy in [(0,1),(1,1),(3,1),(4,1),(0,2),(4,2)]:px[face[0]+xx,face[1]+yy]=(*p['mane'][0],255)
 for xx in [1,3]:px[face[0]+xx,face[1]+2]=(241,244,217,255)
 px[37,42]=(*p['mane'][0],255)
 # All four sides of each distal ear tip receive purple/pink/teal + ivory diamond marks.
 for u,y0 in [(50,34),(8,54)]:
  for xx in range(u,u+10):
   for yy in range(y0+4,y0+8):
    if xx<128 and yy<128:px[xx,yy]=(*p['tip'][1 if yy>y0+4 else 2],255)
  for start,w in [(u,4),(u+4,1),(u+5,4),(u+9,1)]:
   if w>=4:
    for dx,dy in [(1,0),(0,1),(2,1),(1,2),(2,3)]:px[start+dx,y0+4+dy]=(246,242,220,255)
 # Textured tuft UV islands.
 for i in range(6):
  u=72+i*6
  for xx in range(u,u+6):
   for yy in range(4):px[xx,yy]=(*p['mane'][0 if yy==3 else 2 if yy==0 else 1],255)
 # Two double-sided 15x12 tail pennants, outlined in cream with curled centers.
 pattern=[
 '...CCCC........','..CRRRRC.......','.CRRCCRRC......','CRRC.RCRC......','CRRC.CRRC..C...', '.CRRCCRC..CRC..','..CRRRRCCCCRC..','...CCCRRRRRRC..','.....CRRCCRC...','....CRRC.RC....','....CRRRCC.....','.....CCC.......']
 assert all(len(s)==15 for s in pattern)
 for y0 in [15,42]:
  for side in range(2):
   for yy,row in enumerate(pattern):
    for xx,ch in enumerate(row):
     dest=xx if side==0 else 29-xx
     if ch=='.':px[dest,y0+yy]=(0,0,0,0)
     else:px[dest,y0+yy]=(*p['tail'][2 if ch=='C' else 1],255)
 im.save(t/f'zoza_color_{v}.png')
# Animation channels only target real bones and preserve the assembled model.
def cycle(axis,amplitude,length):return {'0.0':[0,0,0],str(length/2):[amplitude if i==axis else 0 for i in range(3)],str(length):[0,0,0]}
anims={
'idle':{'loop':True,'animation_length':2,'bones':{'zoza':{'position':cycle(1,.18,2)},'left ear':{'rotation':cycle(2,3,2)},'left_ear':{'rotation':cycle(2,-3,2)},'tail_left':{'rotation':cycle(1,6,2)},'tail_right':{'rotation':cycle(1,-6,2)}}},
'walk':{'loop':True,'animation_length':.6,'bones':{'zoza':{'position':cycle(1,.35,.6)},**{n:{'rotation':cycle(0,a,.6)} for n,a in [('leg_fl',22),('leg_fr',-22),('leg_bl',-22),('leg_br',22)]},'tail_left':{'rotation':cycle(1,10,.6)},'tail_right':{'rotation':cycle(1,-10,.6)}}},
'sniff':{'loop':True,'animation_length':1.2,'bones':{'head':{'rotation':cycle(0,7,1.2)},'left ear':{'rotation':cycle(2,8,1.2)},'left_ear':{'rotation':cycle(2,-8,1.2)},'tail_left':{'rotation':cycle(1,12,1.2)},'tail_right':{'rotation':cycle(1,-12,1.2)}}}}
anim.write_text(json.dumps({'format_version':'1.8.0','animations':anims},indent=2)+'\n',encoding='utf-8')
# Verify hierarchy, animation links and texture resolution before integrating.
names={b['name'] for b in bones};assert all(b.get('parent') in names for b in bones if 'parent' in b)
assert all(set(an['bones'])<=names for an in anims.values())
for v in range(3):assert Image.open(t/f'zoza_color_{v}.png').size==(128,128)
print('Zoza model detailed; three 128x128 palettes; idle/walk/sniff use real bones.')
