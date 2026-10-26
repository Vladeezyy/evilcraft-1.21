#!/usr/bin/env python3
"""Rewrites NeoForge data of the mod for Fabric, in place:
- neoforge:components ingredients -> fabric:components custom ingredients
- neoforge:conditions (not tag_empty) -> fabric:load_conditions (tags_populated)
- c: tag names that differ in Fabric's conventional tags
usage: data_fabric.py <src/main/resources>"""
import json, os, sys

TAGS = {'c:slimeballs': 'c:slime_balls'}

def base_of(items):
    if isinstance(items, list):
        return [base_of(i) for i in items] if len(items) > 1 else base_of(items[0])
    if items.startswith('#'):
        return {'tag': items[1:]}
    return {'item': items}

def condition(c):
    t = c.get('type')
    if t == 'neoforge:not':
        inner = c['value']
        if inner.get('type') == 'neoforge:tag_empty':
            return {'condition': 'fabric:tags_populated', 'values': [inner['tag']]}
        return {'condition': 'fabric:not', 'value': condition(inner)}
    if t == 'neoforge:tag_empty':
        return {'condition': 'fabric:not', 'value': {'condition': 'fabric:tags_populated', 'values': [c['tag']]}}
    if t == 'neoforge:mod_loaded':
        return {'condition': 'fabric:all_mods_loaded', 'values': [c['modid']]}
    if t == 'neoforge:and':
        return {'condition': 'fabric:and', 'values': [condition(v) for v in c['values']]}
    if t == 'neoforge:or':
        return {'condition': 'fabric:or', 'values': [condition(v) for v in c['values']]}
    raise ValueError('Unknown condition %s' % c)

def walk(o):
    if isinstance(o, dict):
        if o.get('type') == 'neoforge:components':
            n = {'fabric:type': 'fabric:components', 'base': base_of(o['items']), 'components': o['components']}
            return n
        out = {}
        for k, v in o.items():
            if k == 'neoforge:conditions':
                out['fabric:load_conditions'] = [condition(c) for c in v]
            elif k == 'tag' and isinstance(v, str):
                out[k] = TAGS.get(v, v)
            else:
                out[k] = walk(v)
        return out
    if isinstance(o, list):
        return [walk(v) for v in o]
    if isinstance(o, str):
        return TAGS.get(o, o) if o.startswith('c:') else o
    return o

root = sys.argv[1]
n = 0
for d, _, fs in os.walk(os.path.join(root, 'data')):
    for f in fs:
        if f.endswith('.json'):
            p = os.path.join(d, f)
            text = open(p).read()
            if 'neoforge:' not in text and not any(t in text for t in TAGS):
                continue
            if '/neoforge/' in p.replace(os.sep, '/') + '/' and ('biome_modifier' in p or 'data_maps' in p or 'loot_modifiers' in p):
                continue  # read by the mod's own code
            data = json.loads(text)
            new = walk(data)
            if new != data:
                open(p, 'w').write(json.dumps(new, indent=2) + '\n')
                n += 1
print('rewritten', n)
