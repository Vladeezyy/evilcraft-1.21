#!/usr/bin/env python3
"""Three-way merge of an upstream range into this tree: both upstream versions go through the mechanical port passes first,
so that only the real upstream changes reach the port. Conflicts are left as markers (listed at the end).

usage: merge_upstream.py <upstream repo> <base ref> <theirs ref> <prefix in upstream> <prefix here> <cc_classes.txt> <cyclopscore repo>
"""
import os
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

repo, base, theirs, up_prefix, here_prefix, cc_list, cc_repo = sys.argv[1:8]
tools = Path(__file__).parent


def export(ref, target):
    target.mkdir(parents=True, exist_ok=True)
    tar = subprocess.run(['git', '-C', repo, 'archive', ref, up_prefix], capture_output=True, check=True).stdout
    subprocess.run(['tar', '-x', '-C', str(target)], input=tar, check=True)


def passes(root):
    java, res = root / 'src/main/java', root / 'src/main/resources'
    for script, args in (('rewrite_transfer.py', [java]), ('rewrite_cc_shim.py', [java, cc_list, cc_repo]), ('rewrite_events.py', [java]),
                         ('rewrite_models.py', [java, res]), ('rewrite_misc.py', [java]), ('data_fabric.py', [res])):
        if script == 'rewrite_models.py' and not res.exists():
            res.mkdir(parents=True)
        subprocess.run([sys.executable, '-I', str(tools / script)] + [str(a) for a in args], capture_output=True, check=True)


tmp = Path(tempfile.mkdtemp())
b, t = tmp / 'base', tmp / 'theirs'
export(base, b)
export(theirs, t)
passes(b)
passes(t)
b, t = b / up_prefix, t / up_prefix
changed, added, deleted, conflicts, missing = [], [], [], [], []
files = {p.relative_to(b) for p in b.rglob('*') if p.is_file()} | {p.relative_to(t) for p in t.rglob('*') if p.is_file()}
for rel in sorted(files):
    bf, tf, here = b / rel, t / rel, Path(here_prefix) / rel
    if bf.exists() and tf.exists() and bf.read_bytes() == tf.read_bytes():
        continue
    if not tf.exists():
        if here.exists():
            here.unlink()
            deleted.append(rel)
    elif not bf.exists():
        if here.exists():
            conflicts.append(str(rel) + ' (added by both)')
        else:
            here.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy(tf, here)
            added.append(rel)
    elif not here.exists():
        missing.append(rel)
    else:
        r = subprocess.run(['git', 'merge-file', '-L', 'port', '-L', 'base', '-L', 'upstream', str(here), str(bf), str(tf)], capture_output=True)
        changed.append(rel)
        if r.returncode != 0:
            conflicts.append(str(rel))
print('merged', len(changed), 'added', len(added), 'deleted', len(deleted))
print('deleted:', *deleted)
print('not in the port (upstream changed them):', *missing, sep='\n  ')
print('CONFLICTS:', *conflicts, sep='\n  ')
shutil.rmtree(tmp)
