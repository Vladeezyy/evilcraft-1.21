#!/usr/bin/env python3
"""Idempotent source rewrites of the Fabric port (NeoForge API -> Fabric and this mod's shims).
usage: normalize.py <src/main/java>"""
import os, re, subprocess, sys

SRC = sys.argv[1]
HERE = os.path.dirname(os.path.abspath(__file__))

SUBS = [
    # Environment annotations
    (r'@OnlyIn\(value = Dist\.CLIENT, _interface = [\w.]+\.class\)\n', ''),
    (r'import net\.neoforged\.api\.distmarker\.OnlyIn;', 'import net.fabricmc.api.Environment;'),
    (r'import net\.neoforged\.api\.distmarker\.Dist;', 'import net.fabricmc.api.EnvType;'),
    (r'@OnlyIn\(Dist\.CLIENT\)', '@Environment(EnvType.CLIENT)'),
    (r'@OnlyIn\(Dist\.DEDICATED_SERVER\)', '@Environment(EnvType.SERVER)'),
    (r'\bDist\.CLIENT\b', 'EnvType.CLIENT'),
    (r'\bDist\.DEDICATED_SERVER\b', 'EnvType.SERVER'),
    # Fluids and capabilities
    (r'net\.neoforged\.neoforge\.fluids\.(FluidStack|FluidUtil|FluidActionResult|IFluidTank|SimpleFluidContent|FluidType|BaseFlowingFluid)\b', r'org.cyclops.evilcraft.fabric.fluids.\1'),
    (r'net\.neoforged\.neoforge\.fluids\.capability\.', 'org.cyclops.evilcraft.fabric.fluids.capability.'),
    (r'net\.neoforged\.neoforge\.capabilities\.(Capabilities|RegisterCapabilitiesEvent|BlockCapability|ItemCapability|ICapabilityProvider|IBlockCapabilityProvider)\b', r'org.cyclops.evilcraft.fabric.capabilities.\1'),
    (r'net\.neoforged\.neoforge\.common\.SoundActions\b', 'org.cyclops.evilcraft.fabric.fluids.SoundActions'),
    # Configs
    (r'import org\.cyclops\.evilcraft\.fabric\.cyclopscore\.config\.ConfigurableProperty;', 'import org.cyclops.cyclopscore.config.ConfigurablePropertyCommon;'),
    (r'@ConfigurableProperty\(', '@ConfigurablePropertyCommon('),
    (r'import net\.neoforged\.fml\.config\.ModConfig;', 'import org.cyclops.cyclopscore.config.ModConfigLocation;'),
    (r'\bModConfig\.Type\.', 'ModConfigLocation.'),
    (r'import org\.cyclops\.cyclopscore\.config\.extendedconfig\.GuiConfig;', 'import org.cyclops.evilcraft.fabric.cyclopscore.config.extendedconfig.GuiConfig;'),
    # Registries, hooks
    (r'net\.neoforged\.neoforge\.registries\.DeferredHolder\b', 'org.cyclops.evilcraft.fabric.registries.DeferredHolder'),
    (r'net\.neoforged\.neoforge\.server\.ServerLifecycleHooks\b', 'org.cyclops.evilcraft.fabric.ServerLifecycleHooks'),
    (r'net\.neoforged\.neoforge\.common\.CommonHooks\b', 'org.cyclops.evilcraft.fabric.CommonHooks'),
    (r'net\.neoforged\.neoforge\.common\.loot\.(IGlobalLootModifier|LootModifier)\b', r'org.cyclops.evilcraft.fabric.loot.\1'),
    (r'import net\.neoforged\.neoforge\.common\.util\.FakePlayer;', 'import net.fabricmc.fabric.api.entity.FakePlayer;'),
    (r'FakePlayerFactory\.getMinecraft\(', 'FakePlayer.get('),
    (r'import net\.neoforged\.neoforge\.common\.util\.FakePlayerFactory;\n', ''),
    # Client
    (r'import net\.neoforged\.neoforge\.client\.model\.data\.(ModelData|ModelProperty);', r'import org.cyclops.evilcraft.fabric.client.model.\1;'),
    (r'import net\.neoforged\.neoforge\.client\.model\.geometry\.(IGeometryBakingContext|UnbakedGeometryHelper|IGeometryLoader);', r'import org.cyclops.evilcraft.fabric.client.model.\1;'),
    (r'import net\.neoforged\.neoforge\.client\.extensions\.common\.IClientFluidTypeExtensions;', 'import org.cyclops.evilcraft.fabric.client.IClientFluidTypeExtensions;'),
    (r'\bgetGuiLeft\(\)', 'leftPos'),
    (r'\bgetGuiTop\(\)', 'topPos'),
    (r'\bgetXSize\(\)', 'imageWidth'),
    (r'\bgetYSize\(\)', 'imageHeight'),
    # Cyclops Core's own mod instance -> this mod
    (r'import org\.cyclops\.evilcraft\.fabric\.cyclopscore\.CyclopsCore;', 'import org.cyclops.evilcraft.EvilCraft;'),
    (r'\bCyclopsCore\._instance\b', 'EvilCraft._instance'),
    (r'import org\.cyclops\.cyclopscore\.infobook\.pageelement\.\*;', 'import org.cyclops.evilcraft.fabric.cyclopscore.infobook.pageelement.*;'),
    (r'IModHelpersNeoForge\.get\(\)', 'IModHelpers.get()'),
    (r'import org\.cyclops\.evilcraft\.fabric\.cyclopscore\.helper\.IModHelpersNeoForge;', 'import org.cyclops.cyclopscore.helper.IModHelpers;'),
]
COMPONENT = re.compile(r'\.(set|get|getOrDefault|has|remove|update|component|getTyped)\(\s*((?:org\.cyclops\.evilcraft\.(?:fabric\.cyclopscore\.)?)?RegistryEntries\.COMPONENT_[A-Z_]+)(\s*[,)])')

def clog(t):
    def sub(m):
        args = m.group(1)
        depth = 0
        for i, c in enumerate(args):
            if c in '([':
                depth += 1
            elif c in ')]':
                depth -= 1
            elif c == ',' and depth == 0:
                a, b = args[:i].strip(), args[i + 1:].strip()
                if 'Level' in a:
                    return 'org.cyclops.evilcraft.EvilCraft.clog(%s, %s)' % (b, a)
        return 'org.cyclops.evilcraft.EvilCraft.clog(%s)' % args
    return re.sub(r'\bCyclopsCore\.clog\(((?:[^()]|\((?:[^()]|\([^()]*\))*\))*)\)', sub, t)

for d, _, fs in os.walk(SRC):
    for f in fs:
        if f.endswith('.java'):
            p = os.path.join(d, f)
            t = open(p).read()
            n = t
            for a, b in SUBS:
                n = re.sub(a, b, n)
            n = COMPONENT.sub(lambda m: '.%s(%s.get()%s' % (m.group(1), m.group(2), m.group(3)), n)
            n = clog(n)
            if n != t:
                open(p, 'w').write(n)
subprocess.run([sys.executable, os.path.join(HERE, 'caps.py'), SRC], check=True, stdout=subprocess.DEVNULL)
