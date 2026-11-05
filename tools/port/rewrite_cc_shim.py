#!/usr/bin/env python3
"""Pass 2: org.cyclops.cyclopscore.X references that are not in the Fabric jar of Cyclops Core: point them to the shim
package, copying the class from upstream's loader-neoforge when the shim does not have it yet (repeated until stable)."""
import re
import subprocess
import sys
from pathlib import Path

src = Path(sys.argv[1])
cc_list = set(Path(sys.argv[2]).read_text().split())
cc_repo = sys.argv[3]
SHIM = src / 'org/cyclops/evilcraft/fabric/cyclopscore'
OLD, NEW = 'org.cyclops.cyclopscore', 'org.cyclops.evilcraft.fabric.cyclopscore'
ref = re.compile(re.escape(OLD) + r'\.((?:[a-z_][a-z0-9_]*\.)*[A-Z][A-Za-z0-9_]*)')
copied, unresolved = [], set()


def upstream(rel):
    for loader in ('loader-neoforge',):
        r = subprocess.run(['git', '-C', cc_repo, 'show', f'origin/master-26-lts:{loader}/src/main/java/org/cyclops/cyclopscore/{rel}.java'],
                           capture_output=True)
        if r.returncode == 0:
            return r.stdout.decode('utf-8')
    return None


def process(path):
    text = path.read_text(encoding='utf-8')

    def sub(m):
        rel = m.group(1).replace('.', '/')
        if rel in cc_list:
            return m.group(0)
        shim = SHIM / (rel + '.java')
        if not shim.exists():
            body = upstream(rel)
            if body is None:
                unresolved.add(m.group(1))
                return m.group(0)
            pkg = m.group(1).rsplit('.', 1)[0] if '.' in m.group(1) else ''
            body = body.replace('package ' + OLD + ('.' + pkg if pkg else '') + ';',
                                'package ' + NEW + ('.' + pkg if pkg else '') + ';', 1)
            # Classes of the old package that were reachable without an import
            names = set(re.findall(r'\b([A-Z][A-Za-z0-9_]*)\b', body))
            prefix = pkg.replace('.', '/') + '/' if pkg else ''
            extra = sorted(c for c in cc_list if c.startswith(prefix) and '/' not in c[len(prefix):] and c[len(prefix):] in names)
            imports = ''.join('import %s.%s;\n' % (OLD, c.replace('/', '.')) for c in extra)
            body = re.sub(r'^(package [^;]+;\n)', lambda g: g.group(1) + '\n' + imports, body, count=1, flags=re.M)
            shim.parent.mkdir(parents=True, exist_ok=True)
            shim.write_text(body, encoding='utf-8')
            copied.append(m.group(1))
        return NEW + '.' + m.group(1)

    new = ref.sub(sub, text)
    if new != text:
        path.write_text(new, encoding='utf-8')
        return True
    return False


changed = 0
while True:
    n = 0
    for p in list(src.rglob('*.java')):
        if process(p):
            n += 1
    changed += n
    if n == 0:
        break
print('changed', changed)
print('copied', len(copied), copied)
print('unresolved', sorted(unresolved))
