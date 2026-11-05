#!/usr/bin/env python3
"""Mechanical pass over what is left after rewrite_models.py: removed imports, API renames of 26.1.

usage: rewrite_misc.py <src/main/java>
"""
import re
import sys
from pathlib import Path

src = Path(sys.argv[1])
HELPER = 'org.cyclops.evilcraft.fabric.cyclopscore.helper.IModHelpersNeoForge'
changed = 0


def add_import(text, fq):
    if 'import ' + fq + ';' in text:
        return text
    return re.sub(r'^(package [^;]+;\n)', lambda g: g.group(1) + '\nimport ' + fq + ';', text, count=1, flags=re.M)


for path in src.rglob('*.java'):
    old = text = path.read_text(encoding='utf-8')
    # NeoForge's fluid item capability is asked through the item access
    text = re.sub(r'new Capabilities\.Fluid\.ITEM\.getCapability\(ItemAccessItemLocation\((.+?)\), null\)',
                  r'new ItemAccessItemLocation(\1).getCapability(Capabilities.Fluid.ITEM)', text)
    text = re.sub(r'((?:org\.cyclops\.evilcraft\.fabric\.capabilities\.)?Capabilities\.Fluid\.ITEM)\.getCapability\((ItemAccess\.forStack\(\w+\)|\w+), null\)',
                  r'\2.getCapability(\1)', text)
    text = re.sub(r'\bIFluidHelpersNeoForge\b', 'IModHelpersNeoForge.FluidHelpers', text)
    text = re.sub(r'^import [\w.]*IModHelpersNeoForge\.FluidHelpers;\n', '', text, flags=re.M)
    if 'IModHelpersNeoForge.FluidHelpers' in text:
        text = add_import(text, HELPER)
    text = text.replace('net.minecraft.Util.', 'net.minecraft.util.Util.')
    text = re.sub(r'\bholders\.holderOrThrow\(', 'holders.getOrThrow(', text)
    text = re.sub(r'FuelRegistry\.INSTANCE\.add\((this\.\w+\(\)), (\d+)\)',
                  r'net.fabricmc.fabric.api.registry.FuelValueEvents.BUILD.register((builder, context) -> builder.add(\1, \2))', text)
    text = re.sub(r'^import net\.fabricmc\.fabric\.api\.registry\.FuelRegistry;\n', '', text, flags=re.M)
    text = re.sub(r'\bBreakBlockEvent\b', 'BlockEvent.BreakEvent', text)
    text = re.sub(r'^import net\.neoforged\.neoforge\.event\.level\.block\.BlockEvent\.BreakEvent;\n',
                  'import org.cyclops.evilcraft.fabric.event.BlockEvent;\n', text, flags=re.M)
    text = re.sub(r'^\s*@SubscribeEvent(\(.*\))?\n', '', text, flags=re.M)
    text = re.sub(r'\bTags\.Items\.TOOLS_SHEAR\b', 'ConventionalItemTags.SHEAR_TOOLS', text)
    if 'ConventionalItemTags' in text:
        text = add_import(text, 'net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags')
    # Ingredient#getValues of the 1.21.1 port
    text = re.sub(r'\b(\w+)\.getValues\(\)(?=\)? *\{)', r'\1.items().toList()', text)
    # Explosions: Neo's hook -> vanilla
    text = re.sub(r'public void onBlockExploded\(BlockState (\w+), ServerLevel (\w+), BlockPos (\w+), Explosion (\w+)\) \{',
                  r'protected void onExplosionHit(BlockState \1, ServerLevel \2, BlockPos \3, Explosion \4, java.util.function.BiConsumer<ItemStack, BlockPos> dropConsumer) {\n'
                  r'        if (\1.isAir() || \4.getBlockInteraction() == Explosion.BlockInteraction.TRIGGER_BLOCK) {\n            return;\n        }', text)
    # Neo's fluid overlay hook does not exist; only blocks with Fabric's transparency hook would use it
    text = re.sub(r'    @Override\n(    public boolean shouldDisplayFluidOverlay)', r'\1', text)
    text = text.replace('getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player)',
                        'getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData)')
    text = text.replace('super.getLightEmission(state, world, pos)', 'state.getLightEmission()')
    text = text.replace('.lastHurtByPlayerTime', '.lastHurtByPlayerMemoryTime')
    # Imports of NeoForge that nothing uses any more
    for m in re.findall(r'^import net\.neoforged\.[\w.]*\.(\w+);\n', text, flags=re.M):
        rest = re.sub(r'^import .*\n', '', text, flags=re.M)
        if not re.search(r'\b' + m + r'\b', rest):
            text = re.sub(r'^import net\.neoforged\.[\w.]*\.' + m + r';\n', '', text, flags=re.M)
    if text != old:
        path.write_text(text, encoding='utf-8')
        changed += 1
print('changed', changed)
