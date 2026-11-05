#!/usr/bin/env python3
"""Imports of Minecraft classes that moved: rewritten by the simple name when the class exists once in the target jar.

usage: backport_imports.py <minecraft jar> <src dir> [more src dirs]
"""
import collections
import re
import sys
import zipfile
from pathlib import Path

jar = zipfile.ZipFile(sys.argv[1])
names = {n[:-6].replace('/', '.') for n in jar.namelist() if n.endswith('.class') and '$' not in n and n.startswith('net/minecraft/')}
simple = collections.defaultdict(list)
for n in names:
    simple[n.rsplit('.', 1)[1]].append(n)
changed, missing = 0, collections.Counter()
for root in sys.argv[2:]:
    for path in Path(root).rglob('*.java'):
        old = text = path.read_text(encoding='utf-8')

        def sub(m):
            fq = m.group(1)
            if fq in names:
                return m.group(0)
            hits = simple.get(fq.rsplit('.', 1)[1], [])
            if len(hits) == 1:
                return 'import ' + hits[0] + ';'
            missing[fq + (' (several)' if hits else '')] += 1
            return m.group(0)

        text = re.sub(r'^import (net\.minecraft\.[\w.]*\.[A-Z]\w*);', sub, text, flags=re.M)
        if text != old:
            path.write_text(text, encoding='utf-8')
            changed += 1
print('changed', changed)
print(*missing.most_common(60), sep='\n')
