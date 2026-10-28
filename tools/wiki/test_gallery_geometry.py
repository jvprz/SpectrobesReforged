"""Geometry regressions for flat fins, fractional box UVs and GeckoLib handedness."""
import json
from pathlib import Path
import tempfile
import unittest
from design_gallery import geometry

class GalleryGeometryTest(unittest.TestCase):
    def fixture(self, cube, rotation=None):
        tmp=tempfile.TemporaryDirectory();self.addCleanup(tmp.cleanup);root=Path(tmp.name)
        p=root/'src/main/resources/assets/spectrobesreforged/geo/entity/spectrobe/test.geo.json';p.parent.mkdir(parents=True)
        p.write_text(json.dumps({'minecraft:geometry':[{'description':{'texture_width':64,'texture_height':64},'bones':[{'name':'root','pivot':[0,0,0],'rotation':rotation or [0,0,0],'cubes':[cube]}]}]}))
        return geometry(root,'test','entity/test')
    def test_flat_fin_has_one_visible_surface_instead_of_double_textures(self):
        model=self.fixture({'origin':[0,0,0],'size':[8,8,0],'uv':[0,0]})
        visible=[]
        for quad in model['quads']:
            a,b,c=quad['points'];u=[v-w for v,w in zip(b,a)];v=[x-y for x,y in zip(c,a)]
            cross=[u[1]*v[2]-u[2]*v[1],u[2]*v[0]-u[0]*v[2],u[0]*v[1]-u[1]*v[0]]
            if sum(x*x for x in cross)>1e-8:visible.append(quad)
        self.assertEqual(1,len(visible))
        self.assertEqual([2,0,0,2],visible[0]['uv'])
    def test_fractional_box_uvs_use_geckolib_floor_dimensions(self):
        model=self.fixture({'origin':[2,0,0],'size':[3.8,4.7,2.3],'uv':[0,0]},[0,-90,0])
        for quad in model['quads']:
            self.assertTrue(all(abs(uv*4-round(uv*4))<1e-7 for uv in quad['uv']))
        points=[p for q in model['quads'] for p in q['points']]
        self.assertAlmostEqual(-5.8,min(p[2] for p in points))
        self.assertAlmostEqual(-2,max(p[2] for p in points))
