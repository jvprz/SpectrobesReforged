"""Vivid palette pass from frozen sources; no redraw, blur, alpha or coordinate edits."""
from pathlib import Path
from PIL import Image, ImageDraw
import colorsys
ROOT=Path(__file__).resolve().parents[2]
ART=ROOT/'art/mineral-vivid'
SOURCE=ART/'source'
TARGET=ROOT/'src/main/resources/assets/spectrobesreforged/textures/item/minerals'

def vivid(pixel):
    r,g,b,a=pixel
    if not a:return pixel
    h,s,v=colorsys.rgb_to_hsv(r/255,g/255,b/255)
    # Preserve neutral metals, white reflections and the darkest outlines.
    if s<.15 or v<.18:return pixel
    saturation=min(.985,s+(1-s)*.35+.025)
    value=min(.985,v*1.065+.005)
    rgb=colorsys.hsv_to_rgb(h,saturation,value)
    return (*[round(c*255) for c in rgb],a)

def main():
    changed=[]
    for path in sorted(SOURCE.glob('*.png')):
        image=Image.open(path).convert('RGBA');result=Image.new('RGBA',image.size)
        palette={p:vivid(p) for p in set(image.getdata())}
        result.putdata([palette[p] for p in image.getdata()])
        assert image.size==result.size and image.getchannel('A').tobytes()==result.getchannel('A').tobytes()
        if result.tobytes()!=image.tobytes():
            result.save(TARGET/path.name);changed.append(path.stem)
        else:
            import shutil
            shutil.copyfile(path,TARGET/path.name)
    # Each cell shows old palette on the left and the vivid version on the right.
    selected=[f'{family}_{grade}' for family in ['coronium','aurorium','flashium'] for grade in ['c','b','a','a_plus','s']]
    canvas=Image.new('RGB',(700,3*132),'#17222d');draw=ImageDraw.Draw(canvas)
    for i,key in enumerate(selected):
        x=(i%5)*140;y=(i//5)*132
        for offset,folder in [(0,SOURCE),(66,TARGET)]:
            im=Image.open(folder/(key+'.png')).convert('RGBA').resize((64,64),Image.Resampling.NEAREST)
            canvas.paste(im,(x+offset+4,y+6),im)
        draw.text((x+4,y+80),key,fill='white');draw.text((x+4,y+98),'before    after',fill='#a3bed0')
    canvas.save(ART/'elemental-comparison.png')
    files=sorted(SOURCE.glob('*.png'))
    canvas=Image.new('RGB',(768,((len(files)+5)//6)*100),'#17222d');draw=ImageDraw.Draw(canvas)
    for i,path in enumerate(files):
        x=i%6*128;y=i//6*100
        for offset,folder in [(0,SOURCE),(60,TARGET)]:
            im=Image.open(folder/path.name).convert('RGBA').resize((56,56),Image.Resampling.NEAREST)
            canvas.paste(im,(x+offset+4,y+4),im)
        draw.text((x+3,y+65),path.stem,fill='white');draw.text((x+3,y+80),'before   after',fill='#a3bed0')
    canvas.save(ART/'all-comparison.png')
    print('Color-only changes:',len(changed),'of',len(files),'textures. Neutral palettes retained.')

if __name__=='__main__':main()
