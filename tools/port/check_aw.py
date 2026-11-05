#!/usr/bin/env python3
"""Checks each entry of the access widener against the clean Minecraft jar; Loom does not fail on entries that no longer exist.

usage: check_aw.py <accesswidener> <minecraft-merged.jar> <javap>
"""
import re
import subprocess
import sys
import zipfile

aw, jar, javap = sys.argv[1:4]
names = set(zipfile.ZipFile(jar).namelist())
cache = {}


def sig(c):
    if c not in cache:
        cache[c] = subprocess.run([javap, '-cp', jar, '-p', '-s', c.replace('/', '.')], capture_output=True, text=True).stdout
    return cache[c]


bad = 0
for i, line in enumerate(open(aw)):
    p = line.split('#')[0].split()
    if len(p) < 3 or p[0].startswith('accessWidener'):
        continue
    kind, c = p[1], p[2]
    ok = c + '.class' in names
    if ok and kind != 'class':
        name, desc = p[3], p[4]
        s = sig(c)
        ok = ('descriptor: ' + desc) in s if name == '<init>' else re.search(r'\b' + re.escape(name) + r'\b[^\n]*\n\s*descriptor: ' + re.escape(desc), s) is not None
    if not ok:
        bad += 1
        print(i + 1, line.strip()[:140])
print('bad entries:', bad)
