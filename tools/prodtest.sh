#!/bin/sh
# Production runs (intermediary names, as players have them), each under the hang watchdog: the release jar on a real
# Fabric server (prodServer), then the client scene on a production client (prodScene: release jar + scene mod).
# SERVER_ONLY=1: only the server (CI). Exit code 1 when the server does not start.
cd "$(dirname "$0")/.." || exit 1
. tools/watchdog.sh
kill_dev() { pkill -f "$(pwd).*(loom-cache|net\.fabricmc)" 2>/dev/null; pkill -f "fabric.addMods=.*evilcraft" 2>/dev/null; }
mkdir -p build run-prod-server run-prod
echo "eula=true" > run-prod-server/eula.txt
rm -rf run-prod-server/world
if lsof -iTCP:25565 -sTCP:LISTEN >/dev/null 2>&1; then echo "PORT BUSY: 25565"; exit 1; fi
LOG=build/prodserver.log
echo "=== Fabric production server"
./gradlew prodServer --console=plain > "$LOG" 2>&1 &
wait_started "$LOG"
sleep 3
kill_dev; sleep 3
grep -E 'Done \(|Crash report|crash report|BUILD FAILED' "$LOG" | head -3
grep -E '/(ERROR|FATAL)\]|Caused by|Exception' "$LOG" | grep -vE 'server.properties|No data fixer registered' | cut -c1-250 | sort | uniq -c | sort -rn | head -10
if ! grep -q 'Done (' "$LOG" || grep -qE 'Crash report|crash report' "$LOG"; then
    echo "PRODUCTION SERVER FAIL"
    exit 1
fi
echo "PRODUCTION SERVER OK"
# SERVER_ONLY=1 (CI, no display): only the server
[ "$SERVER_ONLY" = 1 ] && exit 0
[ -f run-prod/options.txt ] || cp run/options.txt run-prod/ 2>/dev/null
rm -rf run-prod/saves/ec_scene run-prod/screenshots
LOG=build/prodscene.log
run_watched "$LOG" 900 ./gradlew prodScene --console=plain
echo "=== Fabric production client: $(grep -c '\] CHECK' "$LOG") checks, $(grep -oE 'DONE, failed checks: [0-9]+' "$LOG")"
grep -E 'CHECK FAIL' "$LOG" | sed 's/.*CHECK/CHECK/' | head -20
grep -E 'Mixin apply failed|InvalidInjection|Caused by|Crash report' "$LOG" | cut -c1-250 | sort -u | head -10
