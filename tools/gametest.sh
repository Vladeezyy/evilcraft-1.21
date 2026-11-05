#!/bin/sh
# Runs the GameTests of the original (registered by Cyclops Core, src/main/java/.../gametest) on a dev server under the hang watchdog;
# the server exits after the tests. Prints the failed tests and the summary; exit code 0 when all required tests pass.
cd "$(dirname "$0")/.." || exit 1
. tools/watchdog.sh
kill_dev() { pkill -f "$(pwd).*(loom-cache|net\.fabricmc)" 2>/dev/null; }
LOG=build/gametest.log
mkdir -p build run-gametest
echo "eula=true" > run-gametest/eula.txt
rm -rf run-gametest/world build/gametest/junit.xml
# A GameTest server waits silently while a long test runs: allow 3 minutes of silence
STALL=180 run_watched "$LOG" 1200 ./gradlew runGametest --console=plain "$@"
code=$?
grep -E 'failed!|::|required tests? failed|All [0-9]+ required tests passed|tests? (passed|failed)' "$LOG" | sed -E 's/^\[[^]]*\] \[[^]]*\] //' | head -60
grep -nE 'Crash report|Exception in server tick loop' "$LOG" | head -5
if [ $code = 0 ] && grep -qE 'All [0-9]+ required tests passed' "$LOG"; then
    echo "GAMETESTS OK"
    exit 0
fi
echo "GAMETESTS FAIL (code $code)"
exit 1
