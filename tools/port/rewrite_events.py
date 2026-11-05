#!/usr/bin/env python3
"""Pass 3: NeoForge bus registrations -> static addListener of the port's Fabric events, by the handler's parameter type
(the convention of the 1.21.1 port), and the NeoForge event imports -> the port's classes."""
import re
import sys
from pathlib import Path

src = Path(sys.argv[1])
E = 'org.cyclops.evilcraft.fabric.event.'
IMPORTS = {
    'net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent': E + 'BuildCreativeModeTabContentsEvent',
    'net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent': E + 'EntityAttributeCreationEvent',
    'net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent': E + 'RegisterSpawnPlacementsEvent',
    'net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent': E + 'EntityStruckByLightningEvent',
    'net.neoforged.neoforge.event.entity.living.LivingDeathEvent': E + 'LivingDeathEvent',
    'net.neoforged.neoforge.event.entity.living.LivingDamageEvent': E + 'LivingDamageEvent',
    'net.neoforged.neoforge.event.entity.living.LivingFallEvent': E + 'LivingFallEvent',
    'net.neoforged.neoforge.event.entity.living.LivingEvent': E + 'LivingEvent',
    'net.neoforged.neoforge.event.entity.player.PlayerInteractEvent': E + 'PlayerInteractEvent',
    'net.neoforged.neoforge.event.entity.player.ItemTooltipEvent': E + 'ItemTooltipEvent',
    'net.neoforged.neoforge.event.tick.EntityTickEvent': E + 'EntityTickEvent',
    'net.neoforged.neoforge.event.level.BlockEvent': E + 'BlockEvent',
    'net.neoforged.neoforge.client.event.RenderGuiEvent': E + 'RenderGuiEvent',
    'net.neoforged.neoforge.client.event.RegisterColorHandlersEvent': E + 'RegisterColorHandlersEvent',
    'net.neoforged.neoforge.client.event.ComputeFovModifierEvent': E + 'ComputeFovModifierEvent',
    'net.neoforged.neoforge.client.event.RegisterItemModelsEvent': E + 'RegisterItemModelsEvent',
    'net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent': E + 'RegisterSpecialModelRendererEvent',
    'net.neoforged.neoforge.client.event.RegisterBlockStateModels': E + 'RegisterBlockStateModels',
}
# Registrations whose event has no static addListener by that name in the port
SPECIAL = {
    'EntityRenderersEvent.RegisterLayerDefinitions': None,
    'RegisterEvent': None,
    'DefaultDataComponentsBoundEvent': None,
    'TagsUpdatedEvent': None,
    'ServerStartedEvent': None,
    'ClientPlayerNetworkEvent.LoggingIn': None,
}
CAP_IMPORT = 'org.cyclops.evilcraft.fabric.capabilities.RegisterCapabilitiesEvent'
BUS = re.compile(r'(?:[A-Za-z_.()]*\.getModEventBus\(\)|NeoForge\.EVENT_BUS)\.addListener\(\s*(?:EventPriority\.[A-Z]+\s*,\s*)?')
report = []
changed = 0
for path in src.rglob('*.java'):
    src_text = text = path.read_text(encoding='utf-8')
    for old, new in IMPORTS.items():
        text = re.sub(r'\b' + re.escape(old) + r'\b', new, text)

    def line(m):
        rest = text[m.end():m.end() + 200]
        ty = None
        mm = re.match(r'(?:this|[A-Za-z_]+)::([a-zA-Z_]+)', rest)
        if mm:
            sig = re.search(r'(?:void|boolean)\s+' + mm.group(1) + r'\(\s*(?:final\s+)?([A-Za-z_.]+)\s+\w+\)', text)
            ty = sig.group(1) if sig else None
        else:
            mm = re.match(r'\(([A-Za-z_.]+)\s+\w+\)\s*->', rest)
            ty = mm.group(1) if mm else None
        if ty is None or ty in SPECIAL:
            report.append((path.name, ty, rest[:50].split('\n')[0]))
            return m.group(0)
        return ty + '.addListener('

    text = BUS.sub(line, text)
    if text != src_text:
        path.write_text(text, encoding='utf-8')
        changed += 1
print('changed', changed)
for r in report:
    print(r)
