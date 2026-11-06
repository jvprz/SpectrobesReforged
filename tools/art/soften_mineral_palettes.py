"""Palette-only edits: no pixel movement, resizing, filtering or alpha changes.
Sources are frozen under art/mineral-palettes/source so repeated runs are idempotent.
Elemental mineral assets are prepared only; they are not registered as gameplay items.
"""
from pathlib import Path
import colorsys
from PIL import Image, ImageDraw
import math

ROOT=Path(__file__).resolve().parents[2]
SOURCE=ROOT/'art/mineral-palettes/source'
TARGET=ROOT/'src/main/resources/assets/spectrobesreforged/textures/item/minerals'
ART=ROOT/'art/mineral-palettes'
AUTHORED=ART/'authored-current'
RETIRED={'uranium','chrome','evolve'}
MATERIALS={'titanium'}
FLASHIUM={'c':'power_c','b':'defense_b','a':'health_a','a_plus':'star','s':'chrome'}
FAMILIES={'coronium':(2,'titanium'),'aurorium':(118,'uranium'),'flashium':(205,'chrome')}

def softer(pixel):
    r,g,b,a=pixel
    if not a:return pixel
    h,s,v=colorsys.rgb_to_hsv(r/255,g/255,b/255)
    # Keep the darkest outlines; gently narrow the value range and saturation.
    if v<.18:return pixel
    s*=.90
    v=.90*v+.045
    rgb=colorsys.hsv_to_rgb(h,s,v)
    return (*[round(c*255) for c in rgb],a)

def elemental(pixel,target,color_neutrals=False):
    r,g,b,a=pixel
    if not a:return pixel
    h,s,v=colorsys.rgb_to_hsv(r/255,g/255,b/255)
    if s<.16 and (not color_neutrals or v<.18 or v>.96):return pixel  # preserve neutral highlights and darkest outlines
    hue=(target+11-22*v)/360 % 1
    rgb=colorsys.hsv_to_rgb(hue,min(.82,max(.65 if color_neutrals else .42,s*.88)),v)
    return (*[round(c*255) for c in rgb],a)

def star():
    """Artist-authored Flashium A+ is the authoritative shape and shading source."""
    return Image.open(ART/'artist-flashium-a-plus.png').convert('RGBA')

def star_color(pixel,target):
    r,g,b,a=pixel
    if not a:return pixel
    h,s,v=colorsys.rgb_to_hsv(r/255,g/255,b/255)
    if s<.16:return pixel
    # Pure hue shift: preserve the artist's saturation, value, alpha and pixel layout.
    hue=(h+(target-205)/360)%1
    rgb=colorsys.hsv_to_rgb(hue,s,v)
    return (*[round(c*255) for c in rgb],a)

def recolor(image,fn):
    result=Image.new('RGBA',image.size)
    palette={color:fn(color) for color in set(image.getdata())}
    result.putdata([palette[pixel] for pixel in image.getdata()])
    assert result.getchannel('A').tobytes()==image.getchannel('A').tobytes()
    return result

def sheet():
    files=sorted(p for p in SOURCE.glob('*.png') if p.stem not in RETIRED|MATERIALS and (TARGET/p.name).exists())
    canvas=Image.new('RGB',(768,((len(files)+5)//6)*104+120),'#17222d')
    draw=ImageDraw.Draw(canvas)
    for i,p in enumerate(files):
        x=(i%6)*128;y=(i//6)*104
        for offset,im in [(0,Image.open(p).convert('RGBA')),(60,Image.open(TARGET/p.name).convert('RGBA'))]:
            enlarged=im.resize((56,56),Image.Resampling.NEAREST)
            canvas.paste(enlarged,(x+offset+4,y+5),enlarged)
        draw.text((x+4,y+65),p.stem,fill='white')
        draw.text((x+4,y+80),'before   after',fill='#9ab5c8')
    y=((len(files)+5)//6)*104
    draw.text((8,y),'FLASHIUM - palette variants (not registered)',fill='white')
    for i,key in enumerate(FLASHIUM):
        im=Image.open(TARGET/f'flashium_{key}.png').convert('RGBA').resize((64,64),Image.Resampling.NEAREST)
        canvas.paste(im,(i*144+14,y+22),im)
        draw.text((i*144+14,y+90),'Flashium '+key.upper(),fill='white')
    canvas.save(ART/'comparison.png')
    family=Image.new('RGB',(560,124),'#17222d');d=ImageDraw.Draw(family)
    for i,key in enumerate(FLASHIUM):
        im=Image.open(TARGET/f'flashium_{key}.png').convert('RGBA').resize((80,80),Image.Resampling.NEAREST)
        family.paste(im,(i*112+16,8),im);d.text((i*112+16,98),'Flashium '+key.upper(),fill='white')
    family.save(ART/'flashium-preview.png')
    all_families=Image.new('RGB',(600,3*128+24),'#17222d');draw=ImageDraw.Draw(all_families)
    for row,name in enumerate(FAMILIES):
        draw.text((8,row*128+6),name.upper(),fill='white')
        for column,key in enumerate(FLASHIUM):
            im=Image.open(TARGET/f'{name}_{key}.png').convert('RGBA').resize((80,80),Image.Resampling.NEAREST)
            x=column*120+20;y=row*128+24
            all_families.paste(im,(x,y),im)
            draw.text((x,row*128+108),key.upper(),fill='white')
    all_families.save(ART/'elemental-minerals-preview.png')

def main():
    for p in SOURCE.glob('*.png'):
        if p.stem in RETIRED|MATERIALS:continue
        before=Image.open(p).convert('RGBA')
        recolor(before,softer).save(TARGET/p.name)
    star_base=star()
    for family,(hue,s_rank) in FAMILIES.items():
        for key,source in FLASHIUM.items():
            authored=AUTHORED/f'{family}_{key}.png'
            if authored.exists():
                import shutil
                if not (TARGET/authored.name).exists():shutil.copyfile(authored,TARGET/authored.name)
                continue
            source=s_rank if key=='s' else source
            image=star_base if source=='star' else Image.open(TARGET/f'{source}.png').convert('RGBA')
            destination=TARGET/f'{family}_{key}.png'
            if key=='a_plus':
                if family=='flashium':
                    # Preserve the authored file exactly, including PNG metadata.
                    import shutil
                    shutil.copyfile(ART/'artist-flashium-a-plus.png',destination)
                else:recolor(image,lambda pixel:star_color(pixel,hue)).save(destination)
            else:recolor(image,lambda pixel:elemental(pixel,hue,family=="coronium" and key=="s")).save(destination)
    sheet()
    print('Updated',len(list(SOURCE.glob('*.png'))),'mineral palettes; prepared',len(FLASHIUM)*len(FAMILIES),'elemental textures.')

if __name__=='__main__':raise SystemExit('Historical palette pass disabled: use vivid_mineral_palettes.py to preserve the approved vivid colors.')
