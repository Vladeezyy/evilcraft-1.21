#!/usr/bin/env python3
"""Mechanical pass 1 over the merged 26.1.2 tree: NeoForge transfer imports -> the Fabric layer, ResourceLocation -> Identifier,
Cyclops Core's NeoForge-only helpers -> the port's IModHelpersNeoForge."""
import re
import sys
from pathlib import Path

root = Path(sys.argv[1])
T = 'org.cyclops.evilcraft.fabric.transfer.'
IMPORTS = {
    'net.neoforged.neoforge.transfer.ResourceHandler': T + 'ResourceHandler',
    'net.neoforged.neoforge.transfer.ItemAccessResourceHandler': T + 'ItemAccessResourceHandler',
    'net.neoforged.neoforge.transfer.TransferPreconditions': T + 'TransferPreconditions',
    'net.neoforged.neoforge.transfer.access.ItemAccess': T + 'ItemAccess',
    'net.neoforged.neoforge.transfer.fluid.FluidResource': T + 'FluidResource',
    'net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler': T + 'FluidStacksResourceHandler',
    'net.neoforged.neoforge.transfer.fluid.FluidUtil': T + 'FluidUtil',
    'net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler': T + 'ItemAccessFluidHandler',
    'net.neoforged.neoforge.transfer.item.ItemResource': T + 'ItemResource',
    'net.neoforged.neoforge.transfer.item.VanillaContainerWrapper': T + 'VanillaContainerWrapper',
    'net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper': T + 'WorldlyContainerWrapper',
    'net.neoforged.neoforge.transfer.transaction.RootCommitJournal': T + 'RootCommitJournal',
    'net.neoforged.neoforge.transfer.transaction.SnapshotJournal': T + 'SnapshotJournal',
    'net.neoforged.neoforge.transfer.transaction.Transaction': T + 'Transaction',
    'net.neoforged.neoforge.transfer.transaction.TransactionContext': T + 'TransactionContext',
    'net.neoforged.neoforge.fluids.FluidStackTemplate': 'org.cyclops.evilcraft.fabric.fluids.FluidStackTemplate',
    'org.cyclops.evilcraft.fabric.fluids.FluidUtil': T + 'FluidUtil',
    'net.minecraft.resources.ResourceLocation': 'net.minecraft.resources.Identifier',
}
HELPER = 'org.cyclops.evilcraft.fabric.cyclopscore.helper.IModHelpersNeoForge'
changed = 0
for path in root.rglob('*.java'):
    if '/fabric/transfer/' in str(path):
        continue
    src = text = path.read_text(encoding='utf-8')
    for old, new in IMPORTS.items():
        text = re.sub(r'\b' + re.escape(old) + r'\b', new, text)
    # The bucket handler is only named in a javadoc link
    text = text.replace('import net.neoforged.neoforge.transfer.fluid.BucketResourceHandler;\n', '')
    text = text.replace('{@link BucketResourceHandler}', "NeoForge's BucketResourceHandler")
    text = re.sub(r'\bResourceLocation\b', 'Identifier', text)
    # Identifier imported twice after a rename
    seen = []
    def dedupe(m):
        if m.group(0) in seen:
            return ''
        seen.append(m.group(0))
        return m.group(0)
    text = re.sub(r'^import net\.minecraft\.resources\.Identifier;\n', dedupe, text, flags=re.M)
    # Helpers that only exist in the NeoForge build of Cyclops Core
    if re.search(r'IModHelpers\.get\(\)\s*\.get(FluidHelpers|CapabilityHelpers)\(\)', text):
        text = re.sub(r'IModHelpers\.get\(\)(\s*)\.get(FluidHelpers|CapabilityHelpers)\(\)',
                      r'IModHelpersNeoForge.get()\1.get\2()', text)
        if 'import ' + HELPER not in text and not str(path).endswith('IModHelpersNeoForge.java'):
            text = re.sub(r'^(package [^;]+;\n)', r'\1\nimport ' + HELPER + ';', text, count=1, flags=re.M)
    if text != src:
        path.write_text(text, encoding='utf-8')
        changed += 1
print('changed', changed)
