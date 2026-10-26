#!/bin/sh
# Compile and write the unique compile errors (file:line: message | symbol | location) to errs_u.txt next to the log.
S=${S:-/private/tmp/claude-501/-Users-vladeezy-Desktop/8cc13a09-16fb-427d-9b4b-ed05b663e50e/scratchpad}
./gradlew compileJava --console=plain > $S/build.log 2>&1
python3 - "$S/build.log" <<'PY' | sort -u > $S/errs_u.txt
import sys,re
lines=open(sys.argv[1]).read().split('\n')
i=0
while i<len(lines):
    m=re.match(r'.*/src/main/java/org/cyclops/evilcraft/(.*?):(\d+): error: (.*)',lines[i])
    if m:
        extra=[]
        j=i+1
        while j<len(lines) and j<i+6 and not re.match(r'.*: (error|warning):',lines[j]):
            if 'symbol:' in lines[j] or 'location:' in lines[j]:
                extra.append(lines[j].strip())
            j+=1
        print('%s:%s: %s %s'%(m.group(1),m.group(2),m.group(3),' | '.join(extra)))
        i=j
    else:
        i+=1
PY
grep -c . $S/errs_u.txt
grep -m3 'BUILD\|FAILURE' $S/build.log
