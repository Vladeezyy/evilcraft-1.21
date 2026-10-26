#!/bin/sh
# Runs the client test scene (src/scene, DevScene, max 15 min, hang watchdog): screenshots in
# run/screenshots/scene_*.png, then prints the functional checks and the errors of the run.
cd "$(dirname "$0")/.." || exit 1
. tools/watchdog.sh
kill_dev() { pkill -f "$(pwd).*(loom-cache|net\.fabricmc)" 2>/dev/null; }
LOG=build/scenetest.log
mkdir -p build
rm -rf run/saves/ec_scene run/screenshots/scene_*.png
run_watched "$LOG" 900 ./gradlew runScene --console=plain "$@"
grep -E '\[scene\] (CHECK|DONE)' "$LOG" | sed -E 's/^.*\[scene\] //'
ls run/screenshots/ 2>/dev/null | grep scene_
echo "--- errors"
grep -nE 'ERROR|Exception|Caused by|Crash report' "$LOG" | grep -vE 'No data fixer registered|Unable to load registered texture' | head -40
