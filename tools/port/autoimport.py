#!/usr/bin/env python3
"""Add imports for 'cannot find symbol: class/variable X' when X is a top-level class that exists exactly once in this tree."""
import collections
import re
import sys
from pathlib import Path

src = Path(sys.argv[1])
errs = Path(sys.argv[2]).read_text().splitlines()
index = collections.defaultdict(list)
for p in src.rglob('*.java'):
    index[p.stem].append(p)
wanted = collections.defaultdict(set)
for e in errs:
    m = re.match(r'(.*?):\d+: cannot find symbol symbol: +(class|variable) ([A-Z][A-Za-z0-9_]*)\b', e)
    if m:
        wanted['org/cyclops/' + m.group(1)].add(m.group(3))
added, ambiguous, missing = 0, collections.Counter(), collections.Counter()
for rel, names in wanted.items():
    path = src / rel
    text = path.read_text(encoding='utf-8')
    imports = []
    for name in sorted(names):
        hits = index.get(name, [])
        if len(hits) == 1:
            fq = str(hits[0].relative_to(src))[:-5].replace('/', '.')
            line = 'import %s;' % fq
            if line not in text and hits[0].parent != path.parent:
                imports.append(line)
        elif hits:
            ambiguous[name] += 1
        else:
            missing[name] += 1
    if imports:
        text = re.sub(r'^(package [^;]+;\n)', lambda g: g.group(1) + '\n' + '\n'.join(imports) + '\n', text, count=1, flags=re.M)
        path.write_text(text, encoding='utf-8')
        added += len(imports)
print('added', added)
print('ambiguous', ambiguous.most_common(20))
print('missing', missing.most_common(70))
