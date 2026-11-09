"""Uploads the EvilCraft Fabric release jars of the given MC versions to CurseForge project 1778463.
Jar and changelog come from the GitHub release v1.2.109+<mc>. Dry run unless --go.
usage: cf_ec.py <mc>... [--go]"""
import json, os, re, subprocess, sys, tempfile, urllib.request, uuid

PROJECT = 1778463
REPO = 'Vladeezyy/evilcraft-1.21'
VER = '1.2.109'
MC = {'1.21.11': 14406, '26.1.2': 16082, '26.2': 16498, '26.3': 17045}
JAVA = {'1.21.11': 11135}  # Java 21; 26.x: Java 25
FABRIC, CLIENT, SERVER, JAVA25 = 7499, 9638, 9639, 14454
RELATIONS = [{'slug': s, 'type': 'requiredDependency'} for s in ('fabric-api', 'cyclops-core', 'forge-config-api-port')] + \
            [{'slug': s, 'type': 'optionalDependency'} for s in ('jei', 'jade')]

go = '--go' in sys.argv
token = re.search(r'^curseforgeToken=(.*)$', open(os.path.expanduser('~/.gradle/gradle.properties')).read(), re.M).group(1).strip()

for mc in [a for a in sys.argv[1:] if a != '--go']:
    tag = 'v%s+%s' % (VER, mc)
    d = tempfile.mkdtemp()
    subprocess.run(['gh', 'release', 'download', tag, '-R', REPO, '-D', d, '-p', '*.jar'], check=True)
    jar = os.path.join(d, 'evilcraft-fabric-%s+%s.jar' % (VER, mc))
    notes = json.loads(subprocess.check_output(['gh', 'release', 'view', tag, '-R', REPO, '--json', 'body'], encoding='utf-8'))['body']
    meta = {'changelog': notes, 'changelogType': 'markdown', 'releaseType': 'release',
            'displayName': 'EvilCraft %s for Fabric %s' % (VER, mc),
            'gameVersions': [MC[mc], FABRIC, JAVA.get(mc, JAVA25), CLIENT, SERVER],
            'relations': {'projects': RELATIONS}}
    print(mc, os.path.basename(jar), os.path.getsize(jar), meta['gameVersions'])
    if not go:
        continue
    b = uuid.uuid4().hex
    body = ('--%s\r\nContent-Disposition: form-data; name="metadata"\r\n\r\n%s\r\n' % (b, json.dumps(meta))).encode() + \
           ('--%s\r\nContent-Disposition: form-data; name="file"; filename="%s"\r\nContent-Type: application/java-archive\r\n\r\n' % (b, os.path.basename(jar))).encode() + \
           open(jar, 'rb').read() + ('\r\n--%s--\r\n' % b).encode()
    req = urllib.request.Request('https://minecraft.curseforge.com/api/projects/%d/upload-file' % PROJECT, data=body,
                                 headers={'X-Api-Token': token, 'Content-Type': 'multipart/form-data; boundary=' + b})
    print('  ->', urllib.request.urlopen(req).read().decode())
