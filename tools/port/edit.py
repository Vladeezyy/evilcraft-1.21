#!/usr/bin/env python3
"""Small helper for the port: exact replacements plus import fixes in one Java file.
usage: from edit import edit; edit(path, [(old, new), ...], add=[imports], drop=[import prefixes])"""
import re

def edit(p, pairs=(), add=(), drop=('net.neoforged', 'cpw.mods')):
    t = open(p).read()
    for a, b in pairs:
        if a not in t:
            raise SystemExit('NOT FOUND in %s:\n%s' % (p, a))
        t = t.replace(a, b)
    for pre in drop:
        t = re.sub(r'import (static )?' + re.escape(pre) + r'[^\n]*\n', '', t)
    for imp in add:
        line = 'import ' + imp + ';'
        if line not in t:
            t = re.sub(r'(package [^\n]*;\n)', r'\1\n' + line, t, count=1)
    open(p, 'w').write(t)
