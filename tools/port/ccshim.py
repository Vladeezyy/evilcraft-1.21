#!/usr/bin/env python3
"""Copies the NeoForge-only parts of Cyclops Core that EvilCraft uses into EvilCraft's own Fabric shim package
(org.cyclops.evilcraft.fabric.cyclopscore) and rewrites the references to them.

usage: ccshim.py <cyclopscore checkout (master-1.21-lts)> <evilcraft src/main/java> [extra classes or packages to copy]
"""
import os, re, sys

CC, SRC = sys.argv[1], sys.argv[2]
NEO = os.path.join(CC, 'loader-neoforge/src/main/java')
OTHER = [os.path.join(CC, 'loader-common/src/main/java'), os.path.join(CC, 'loader-fabric/src/main/java')]
OLD, NEW = 'org.cyclops.cyclopscore', 'org.cyclops.evilcraft.fabric.cyclopscore'

def classes(root):
    out = set()
    for d, _, fs in os.walk(root):
        for f in fs:
            if f.endswith('.java'):
                fq = os.path.relpath(os.path.join(d, f), root)[:-5].replace('/', '.')
                if fq.startswith(OLD + '.'):
                    out.add(fq[len(OLD) + 1:])
    return out

neo = classes(NEO)
other = set().union(*[classes(r) for r in OTHER])
neo_only = neo - other
ref = re.compile(re.escape(OLD) + r'\.((?:[a-z_][a-z0-9_]*\.)*[A-Z][A-Za-z0-9_]*)')

def rewrite(text):
    def sub(m):
        return NEW + '.' + m.group(1) if m.group(1) in neo_only else m.group(0)
    return ref.sub(sub, text)

def dest(fq):
    return os.path.join(SRC, NEW.replace('.', '/'), fq.replace('.', '/') + '.java')

def copy(fq):
    text = open(os.path.join(NEO, OLD.replace('.', '/'), fq.replace('.', '/') + '.java')).read()
    pkg = fq.rsplit('.', 1)[0] if '.' in fq else ''
    # Same-package classes that stay in Cyclops Core need an import now
    simple = set(re.findall(r'\b([A-Z][A-Za-z0-9_]*)\b', text))
    extra = sorted(c for c in other if c.rsplit('.', 1)[0] == pkg and c.rsplit('.', 1)[-1] in simple)
    text = text.replace('package ' + OLD + ('.' + pkg if pkg else '') + ';',
                        'package ' + NEW + ('.' + pkg if pkg else '') + ';\n\n' +
                        ''.join('import %s.%s;\n' % (OLD, c) for c in extra), 1)
    os.makedirs(os.path.dirname(dest(fq)), exist_ok=True)
    open(dest(fq), 'w').write(rewrite(text))

def needed(text, pkg):
    out = set(m.group(1) for m in re.finditer(re.escape(NEW) + r'\.((?:[a-z_][a-z0-9_]*\.)*[A-Z][A-Za-z0-9_]*)', text))
    # Same-package references inside copied classes
    if pkg is not None:
        for s in set(re.findall(r'\b([A-Z][A-Za-z0-9_]*)\b', text)):
            fq = (pkg + '.' if pkg else '') + s
            if fq in neo_only:
                out.add(fq)
    return out

# 1. Rewrite EvilCraft's own references
for d, _, fs in os.walk(SRC):
    for f in fs:
        if f.endswith('.java'):
            p = os.path.join(d, f)
            t = open(p).read()
            n = rewrite(t)
            if n != t:
                open(p, 'w').write(n)

# 2. Copy the closure of referenced NeoForge-only classes
todo = set()
for extra in sys.argv[3:]:
    todo |= {c for c in neo_only if c == extra or c.startswith(extra + '.')}
for d, _, fs in os.walk(SRC):
    for f in fs:
        if f.endswith('.java'):
            todo |= needed(open(os.path.join(d, f)).read(), None)
done = set()
while todo:
    fq = todo.pop()
    if fq in done or fq not in neo_only:
        continue
    done.add(fq)
    if not os.path.exists(dest(fq)):
        copy(fq)
    pkg = fq.rsplit('.', 1)[0] if '.' in fq else ''
    todo |= needed(open(dest(fq)).read(), pkg) - done
print('shim classes:', len(done))
