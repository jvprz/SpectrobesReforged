"""Deterministic orthographic inventory icons from actual Java block models and UVs.

No network/GPU/Minecraft installation is needed during a wiki build. Flat items
retain their original sprite; only block items use this renderer.
"""
from pathlib import Path
import copy
import json
import math
from functools import lru_cache
from PIL import Image

SIZE = 64
VANILLA = Path(__file__).with_name('vanilla')

class BlockIcons:
    def __init__(self, root):
        self.assets = root / 'src/main/resources/assets'

    def resource(self, identifier, kind, suffix):
        ns, key = identifier.split(':', 1) if ':' in identifier else ('minecraft', identifier)
        base = VANILLA if ns == 'minecraft' else self.assets / ns
        return base / kind / (key + suffix)

    @lru_cache(maxsize=None)
    def model(self, identifier):
        path = self.resource(identifier, 'models', '.json')
        data = json.loads(path.read_text(encoding='utf-8-sig'))
        parent = data.get('parent', '')
        inherited = self.model(parent) if parent and 'builtin/' not in parent else {}
        result = {**inherited, **data}
        result['textures'] = {**inherited.get('textures', {}), **data.get('textures', {})}
        return result

    def block_model(self, identifier):
        try: model = self.model(identifier.replace(':', ':item/', 1))
        except FileNotFoundError: return None
        return model if model.get('elements') else None

    @lru_cache(maxsize=None)
    def texture(self, identifier):
        image = Image.open(self.resource(identifier, 'textures', '.png')).convert('RGBA')
        # Animated block textures use their first square frame in an inventory icon.
        if image.height > image.width and identifier.split(':')[-1].startswith('block/'): image = image.crop((0, 0, image.width, image.width))
        return image

    @staticmethod
    def project(point):
        x, y, z = point
        return ((x-z)/math.sqrt(2), (x+z)/(2*math.sqrt(2))-y*math.sqrt(3)/2,
                (x+z)*math.sqrt(3)/(2*math.sqrt(2))+y/2)

    @staticmethod
    def rotate(point, rotation):
        if isinstance(rotation, list):
            for transform in rotation: point = BlockIcons.rotate(point, transform)
            return point
        if not rotation: return point
        origin = rotation['origin']; p = [v-o for v,o in zip(point,origin)]
        axis = 'xyz'.index(rotation['axis']); a,b = [(1,2),(2,0),(0,1)][axis]
        angle = math.radians(rotation['angle']); c,s = math.cos(angle),math.sin(angle)
        p[a],p[b] = p[a]*c-p[b]*s,p[a]*s+p[b]*c
        if rotation.get('rescale'):
            p[a] /= abs(c);p[b] /= abs(c)
        return [v+o for v,o in zip(p,origin)]

    def render(self, model, output, size=SIZE):
        faces=[]; textures=model['textures']
        for element in model['elements']:
            x,y,z=element['from'];X,Y,Z=element['to']
            # UV top-left, top-right and bottom-left corners, viewed from outside.
            corners={
                'south': [(x,Y,Z),(X,Y,Z),(x,y,Z)],
                'north': [(X,Y,z),(x,Y,z),(X,y,z)],
                'east': [(X,Y,Z),(X,Y,z),(X,y,Z)],
                'west': [(x,Y,z),(x,Y,Z),(x,y,z)],
                'up': [(x,Y,z),(X,Y,z),(x,Y,Z)],
                'down': [(x,y,Z),(X,y,Z),(x,y,z)]}
            for direction,face in element['faces'].items():
                texture=face['texture']; seen=set()
                while texture.startswith('#'):
                    if texture in seen: raise ValueError('Cyclic model texture reference')
                    seen.add(texture); texture=textures[texture[1:]]
                points=[self.project(self.rotate(p,element.get('rotation'))) for p in corners[direction]]
                faces.append((points,self.texture(texture),face.get('uv',[0,0,16,16]),face.get('rotation',0),
                              1 if direction=='up' else .8 if direction in ('south','north') else .6))
        for quad in model.get('quads',[]):
            faces.append(([self.project(p) for p in quad['points']],self.texture(quad['texture']),quad['uv'],quad.get('rotation',0),quad.get('shade',1)))
        vertices=[p for points,*_ in faces for p in points+[tuple(points[1][i]+points[2][i]-points[0][i] for i in range(3))]]
        lo=[min(p[i] for p in vertices) for i in range(2)];hi=[max(p[i] for p in vertices) for i in range(2)]
        scale=(size-8)/max(hi[i]-lo[i] for i in range(2))
        offset=[(size-(hi[i]-lo[i])*scale)/2-lo[i]*scale for i in range(2)]
        fragments=[[] for _ in range(size*size)]
        for points,texture,uv,rotation,shade in faces:
            a,b,d=[(p[0]*scale+offset[0],p[1]*scale+offset[1],p[2]) for p in points]
            ex,ey=b[0]-a[0],b[1]-a[1];fx,fy=d[0]-a[0],d[1]-a[1]
            determinant=ex*fy-ey*fx
            if abs(determinant)<1e-9: continue
            fourth=(b[0]+d[0]-a[0],b[1]+d[1]-a[1])
            xs=[a[0],b[0],d[0],fourth[0]];ys=[a[1],b[1],d[1],fourth[1]]
            for py in range(max(0,math.floor(min(ys))),min(size,math.ceil(max(ys)))):
                for px in range(max(0,math.floor(min(xs))),min(size,math.ceil(max(xs)))):
                    qx,qy=px+.5-a[0],py+.5-a[1]
                    u=(qx*fy-qy*fx)/determinant;v=(ex*qy-ey*qx)/determinant
                    if not (0<=u<1 and 0<=v<1): continue
                    depth=a[2]+u*(b[2]-a[2])+v*(d[2]-a[2])
                    for _ in range(rotation//90):u,v=v,1-u
                    tx=min(texture.width-1,max(0,int((uv[0]+u*(uv[2]-uv[0]))*texture.width/16)))
                    ty=min(texture.height-1,max(0,int((uv[1]+v*(uv[3]-uv[1]))*texture.height/16)))
                    r,g,b_,alpha=texture.getpixel((tx,ty))
                    if alpha: fragments[py*size+px].append((depth,int(r*shade),int(g*shade),int(b_*shade),alpha))
        image=Image.new('RGBA',(size,size));pixels=[]
        for layers in fragments:
            red=green=blue=alpha=0.0
            for _,r,g,b,a in sorted(layers):
                opacity=a/255;out=opacity+alpha*(1-opacity)
                red=(r*opacity+red*alpha*(1-opacity))/out
                green=(g*opacity+green*alpha*(1-opacity))/out
                blue=(b*opacity+blue*alpha*(1-opacity))/out;alpha=out
            pixels.append((round(red),round(green),round(blue),round(alpha*255)))
        image.putdata(pixels);output.parent.mkdir(parents=True,exist_ok=True);image.save(output)
