#!/usr/bin/env python3
"""API renames of 26.2 for code that did not come through the upstream merge (the port's own code, newer code of the LTS branch).

usage: rewrite_262.py <src dir> [more src dirs]
"""
import re
import sys
from pathlib import Path

changed = 0
for root in sys.argv[1:]:
    for path in Path(root).rglob('*.java'):
        old = text = path.read_text(encoding='utf-8')
        # EntityType.ZOMBIE -> EntityTypes.ZOMBIE (the constants moved in 26.2)
        text = re.sub(r'\bEntityType\.(?=[A-Z][A-Z_]+\b)', 'EntityTypes.', text)
        if 'EntityTypes.' in text and 'import net.minecraft.world.entity.EntityTypes;' not in text:
            text = re.sub(r'^(package [^;]+;\n)', r'\1\nimport net.minecraft.world.entity.EntityTypes;', text, count=1, flags=re.M)
        # Removed from BlockPos in 26.2
        text = re.sub(r'((?:helper\.absolutePos\([^()]*\)|\b\w+(?:\.relative\([^()]*\)|\.above\(\)|\.below\(\))*))\.getBottomCenter\(\)',
                      r'net.minecraft.world.phys.Vec3.atBottomCenterOf(\1)', text)
        if text != old:
            path.write_text(text, encoding='utf-8')
            changed += 1
print('changed', changed)
