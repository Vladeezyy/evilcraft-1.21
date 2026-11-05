#!/usr/bin/env python3
"""Three-way merge of a Cyclops Core range into the port's copies of its NeoForge classes (fabric/cyclopscore): both versions of a file
get the shim package and go through the port passes, so only the real changes reach the copy.

usage: merge_cc.py <cyclopscore repo> <base ref> <theirs ref> <src/main/java> <cc_classes.txt>
"""
import re
import subprocess
import sys
import tempfile
from pathlib import Path

repo, base, theirs, java, cc_list = sys.argv[1:6]
tools = Path(__file__).parent
PREFIX = 'loader-neoforge/src/main/java/org/cyclops/cyclopscore/'
SHIM = Path(java) / 'org/cyclops/evilcraft/fabric/cyclopscore'
in_jar = set(Path(cc_list).read_text().split())
ref = re.compile(r'org\.cyclops\.cyclopscore\.((?:[a-z_][a-z0-9_]*\.)*[A-Z][A-Za-z0-9_]*)')


def show(commit, rel):
    r = subprocess.run(['git', '-C', repo, 'show', f'{commit}:{PREFIX}{rel}'], capture_output=True)
    return r.stdout.decode('utf-8') if r.returncode == 0 else None


def shim(text):
    text = re.sub(r'^package org\.cyclops\.cyclopscore', 'package org.cyclops.evilcraft.fabric.cyclopscore', text, flags=re.M)
    return ref.sub(lambda m: m.group(0) if m.group(1).replace('.', '/') in in_jar else 'org.cyclops.evilcraft.fabric.cyclopscore.' + m.group(1), text)


tmp = Path(tempfile.mkdtemp())
files = [p.relative_to(SHIM) for p in SHIM.rglob('*.java')]
pairs = []
for rel in files:
    b, t = show(base, str(rel)), show(theirs, str(rel))
    if b is None or t is None or b == t:
        continue
    pairs.append(rel)
    for side, text in (('base', b), ('theirs', t)):
        out = tmp / side / 'org/cyclops/evilcraft/fabric/cyclopscore' / rel
        out.parent.mkdir(parents=True, exist_ok=True)
        out.write_text(shim(text), encoding='utf-8')
for side in ('base', 'theirs'):
    root = tmp / side
    if not root.exists():
        continue
    for script, args in (('rewrite_transfer.py', [root]), ('rewrite_events.py', [root]), ('rewrite_models.py', [root, tmp]), ('rewrite_misc.py', [root])):
        subprocess.run([sys.executable, '-I', str(tools / script)] + [str(a) for a in args], capture_output=True, check=True)
conflicts = []
for rel in pairs:
    r = subprocess.run(['git', 'merge-file', '-L', 'port', '-L', 'base', '-L', 'upstream', str(SHIM / rel),
                        str(tmp / 'base/org/cyclops/evilcraft/fabric/cyclopscore' / rel), str(tmp / 'theirs/org/cyclops/evilcraft/fabric/cyclopscore' / rel)], capture_output=True)
    if r.returncode != 0:
        conflicts.append(str(rel))
print('merged', len(pairs), 'of', len(files))
print('CONFLICTS:', *conflicts, sep='\n  ')
