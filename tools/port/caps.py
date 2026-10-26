#!/usr/bin/env python3
"""Rewrites NeoForge capability lookups on item stacks and levels (x.getCapability(CAP, args...)) into lookups on
the capability object (CAP.getCapability(x[, null], args...)). usage: caps.py <source dir>"""
import os, re, sys

CAP = re.compile(r'\s*((?:[\w.]+\.)?Capabilities\.\w+\.[A-Z_]+)\s*([,)])')

def receiver_start(t, end):
    # Walk back over a receiver expression like a.b(c).d
    i = end
    while i > 0:
        c = t[i - 1]
        if c.isalnum() or c in '_.':
            i -= 1
        elif c == ')':
            depth = 0
            while i > 0:
                i -= 1
                if t[i] == ')':
                    depth += 1
                elif t[i] == '(':
                    depth -= 1
                    if depth == 0:
                        break
        else:
            break
    return i

def args_end(t, start):
    depth = 1
    i = start
    while depth:
        if t[i] == '(':
            depth += 1
        elif t[i] == ')':
            depth -= 1
        i += 1
    return i  # index after the closing paren

def rewrite(t):
    out = t
    pos = 0
    while True:
        k = out.find('.getCapability(', pos)
        if k < 0:
            return out
        m = CAP.match(out, k + len('.getCapability('))
        if not m:
            pos = k + 1
            continue
        start = receiver_start(out, k)
        receiver = out[start:k]
        end = args_end(out, k + len('.getCapability('))
        cap = m.group(1)
        rest = out[m.end(1):end - 1].strip()
        rest = rest[1:].strip() if rest.startswith(',') else ''
        if not receiver or receiver.endswith('Capabilities'):
            pos = k + 1
            continue
        if rest:
            new = '%s.getCapability(%s, %s)' % (cap, receiver, rest)
        else:
            new = '%s.getCapability(%s, null)' % (cap, receiver)
        out = out[:start] + new + out[end:]
        pos = start + len(new)

for d, _, fs in os.walk(sys.argv[1]):
    for f in fs:
        if f.endswith('.java'):
            p = os.path.join(d, f)
            t = open(p).read()
            n = rewrite(t)
            if n != t:
                open(p, 'w').write(n)
                print(p)
