#!/usr/bin/env python3
"""Mechanical pass over the upstream model and renderer code (run after rewrite_events.py), and the blockstate files.

usage: rewrite_models.py <src/main/java> <src/main/resources>
"""
import re
import sys
from pathlib import Path

src, res = Path(sys.argv[1]), Path(sys.argv[2])
EV = 'org.cyclops.evilcraft.fabric.event.'
HELPER = 'org.cyclops.evilcraft.fabric.cyclopscore.helper.IModHelpersNeoForge'
EXTRA = 'org.cyclops.evilcraft.fabric.fluids.FluidHelpersFabricExtra'
COLORED = 'org.cyclops.evilcraft.fabric.client.model.ColoredQuads'
changed = 0


def add_import(text, fq):
    if 'import ' + fq + ';' in text:
        return text
    return re.sub(r'^(package [^;]+;\n)', lambda g: g.group(1) + '\nimport ' + fq + ';', text, count=1, flags=re.M)


for path in src.rglob('*.java'):
    if '/fabric/client/' in str(path) or path.name == 'IModHelpersNeoForge.java':
        continue
    old = text = path.read_text(encoding='utf-8')
    text = re.sub(r'net\.neoforged\.neoforge\.model\.data\.(ModelData|ModelProperty)', r'org.cyclops.evilcraft.fabric.client.model.\1', text)
    text = text.replace('net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel',
                        'net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel')
    text = text.replace('net.neoforged.neoforge.client.event.EntityRenderersEvent', EV + 'EntityRenderersEvent')
    text = re.sub(r'^import net\.neoforged\.neoforge\.client\.model\.quad\.Baked(Colors|Normals);\n', '', text, flags=re.M)
    # Same import written twice by the earlier passes
    seen = set()
    out = []
    for line in text.split('\n'):
        if line.startswith('import '):
            if line in seen:
                continue
            seen.add(line)
        out.append(line)
    text = '\n'.join(out)
    # NeoForge's two extra fields of BakedQuad
    text, n = re.subn(r'new BakedQuad\(([^;]*?),\s*BakedNormals\.UNSPECIFIED,\s*BakedColors\.of\((\w+)\)\)',
                      lambda m: 'ColoredQuads.withColor(new BakedQuad(' + m.group(1) + '), ' + m.group(2) + ')', text)
    if n:
        text = add_import(text, COLORED)
    text = re.sub(r',\s*BakedNormals\.[A-Z_]+,\s*BakedColors\.[A-Z_]+\)', ')', text)
    text = re.sub(r',\s*BakedNormals\.[A-Z_]+,\s*\w+\.bakedColors\(\)\)', ')', text)
    # BlockStateModel#collectParts of NeoForge has the level, position and state
    text = re.sub(r'\.collectParts\(\w+, BlockPos\.ZERO, \w+, (\w+), (\w+)\)', r'.collectParts(\1, \2)', text)
    # Item models
    text, n = re.subn(r'QuadCollection\.Builder quadBuilder = new QuadCollection\.Builder\(\);\s*'
                      r'for \(BakedQuad quad : (\w+)\) \{\s*quadBuilder\.addUnculledFace\(quad\);\s*\}\s*'
                      r'new CuboidItemModelWrapper\(List\.of\(\), quadBuilder\.build\(\), (\w+), new Matrix4f\(\)\)',
                      lambda m: 'ColoredQuads.itemModel(' + m.group(1) + ', ' + m.group(2) + ')', text)
    if n:
        text = add_import(text, COLORED)
    # Fluid render helpers that only the NeoForge helpers have with fluid stacks
    text, n = re.subn(r'IModHelpers\.get\(\)(\s*)\.getRenderHelpers\(\)(\s*)\.(getFluidIcon|renderFluidContext|getFluidVertexBufferColor|getFluidBakedQuadColor)\(',
                      r'IModHelpersNeoForge.get()\1.getRenderHelpers()\2.\3(', text)
    if n:
        text = add_import(text, HELPER)
    text, n = re.subn(r'(\w+)\.getFluid\(\)\.getFluidType\(\)\.getLightLevel\(\1\)', r'FluidHelpersFabricExtra.getLightLevel(\1.getFluid())', text)
    if n:
        text = add_import(text, EXTRA)
    # Bus registrations of client layers
    text = re.sub(r'[\w.()]*\.getModEventBus\(\)\.addListener\(this::loadLayerDefinitions\)',
                  'EntityRenderersEvent.RegisterLayerDefinitions.addListener(this::loadLayerDefinitions)', text)
    # Block entity renderers: NeoForge's render bounding box -> vanilla's flag for renderers larger than their block
    text = re.sub(r'public AABB getRenderBoundingBox\(\w+ \w+\) \{.*?\n    \}', 'public boolean shouldRenderOffScreen() {\n        return true;\n    }', text, flags=re.S)
    text = re.sub(r'public boolean shouldRenderOffScreen\(\w+ \w+\)', 'public boolean shouldRenderOffScreen()', text)
    if text != old:
        path.write_text(text, encoding='utf-8')
        changed += 1
print('java files changed', changed)

n = 0
for path in (res / 'assets').glob('*/blockstates/*.json'):
    old = path.read_text(encoding='utf-8')
    # Vanilla's variants have no type; Fabric finds custom block state models by this key
    text = re.sub(r'"type"(\s*:\s*"evilcraft:)', r'"fabric:type"\1', old)
    if text != old:
        path.write_text(text, encoding='utf-8')
        n += 1
print('blockstates changed', n)
