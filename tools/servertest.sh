#!/bin/sh
# Starts the dev server (with a fresh world unless KEEP_WORLD=1), waits until it is up (hang watchdog), stops it and
# prints the errors of the log. Exit code 0 when the server started without errors of this mod.
cd "$(dirname "$0")/.." || exit 1
. tools/watchdog.sh
kill_dev() { pkill -f "$(pwd).*(loom-cache|net\.fabricmc)" 2>/dev/null; }
LOG=build/servertest.log
mkdir -p build run-server
echo "eula=true" > run-server/eula.txt
[ "$KEEP_WORLD" = 1 ] || rm -rf run-server/world
if lsof -iTCP:25565 -sTCP:LISTEN >/dev/null 2>&1; then echo "PORT BUSY: 25565"; exit 1; fi
printf 'online-mode=false\nspawn-protection=0\n' > run-server/server.properties
./gradlew runServer --console=plain > "$LOG" 2>&1 &
wait_started "$LOG"
code=$?
sleep 5
kill_dev
sleep 3
grep -nE 'ERROR|Exception|Caused by|Crash|crash report' "$LOG" | grep -v 'Unable to load registered texture' | head -40
if [ $code = 0 ] && grep -q 'Done (' "$LOG" && ! grep -q 'Crash report\|crash report' "$LOG"; then
    echo "SERVER OK"
    exit 0
fi
echo "SERVER FAIL (code $code)"
exit 1
