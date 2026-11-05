#!/bin/sh
# Compile mc/26.1.2 and print a summary of the errors (each error once; javac's log appears twice in the Gradle output).
S=${S:-$(pwd)/build}
cd "$(git rev-parse --show-toplevel)" || exit 1
JAVA_HOME=$HOME/.gradle/jdks/eclipse_adoptium-25-aarch64-os_x.2/jdk-25.0.4.1+1/Contents/Home sh ./gradlew compileJava --console=plain > "$S/build.log" 2>&1
python3 -I - "$S/build.log" "$S/errs.txt" <<'PY'
import collections, re, sys
lines = open(sys.argv[1], errors='replace').read().split('\n')
errs = []
seen = set()
i = 0
while i < len(lines):
    m = re.match(r'.*/src/main/java/org/cyclops/(.*?):(\d+): error: (.*)', lines[i])
    if m:
        extra = []
        j = i + 1
        while j < len(lines) and j < i + 7 and not re.match(r'.*: (error|warning):', lines[j]):
            s = lines[j].strip()
            if s.startswith(('symbol:', 'location:', 'required:', 'found:', 'reason:')):
                extra.append(s)
            j += 1
        e = (m.group(1), int(m.group(2)), m.group(3), ' | '.join(extra))
        if e not in seen:
            seen.add(e)
            errs.append(e)
        i = j
    else:
        i += 1
with open(sys.argv[2], 'w') as f:
    for e in errs:
        f.write('%s:%d: %s %s\n' % e)
files = {e[0] for e in errs}
print('errors', len(errs), 'files', len(files))
d = collections.Counter('/'.join(e[0].split('/')[:3]) for e in errs)
print('-- by dir'); [print(' ', v, k) for k, v in d.most_common(22)]
s = collections.Counter()
for e in errs:
    m = re.search(r'symbol: *(\S+ \S+?)(\(|\s|$)', e[3])
    s[m.group(1) if m else re.sub(r'\s+', ' ', e[2])[:70]] += 1
print('-- by symbol'); [print(' ', v, k) for k, v in s.most_common(45)]
PY
grep -E 'BUILD|FAILURE: |error: (illegal|class, interface|invalid)' "$S/build.log" | sort -u | head -12
