#!/bin/sh
# Stress test (src/scene PerfScene, max 20 min, hang watchdog): a window of 600 ticks with a vanilla base, then the
# same base plus working EvilCraft content, in one world. Prints server MSPT, client FPS and the difference.
# -Pjfr records run/perf.jfr (then: python3 tools/jfr_hot.py run/perf.jfr 20 35 = the EvilCraft window).
cd "$(dirname "$0")/.." || exit 1
. tools/watchdog.sh
kill_dev() { pkill -f "$(pwd).*(loom-cache|net\.fabricmc)" 2>/dev/null; }
LOG=build/perf.log
mkdir -p build
rm -rf run/saves/ec_perf run/perf.jfr
run_watched "$LOG" 1200 ./gradlew runPerf --console=plain "$@"
grep -E '\[perf\] ' "$LOG" | sed -E 's/^.*\[perf\] //'
echo "--- errors"
grep -nE '/(ERROR|FATAL)\]|Caused by|Exception' "$LOG" | grep -vE 'No data fixer registered' | cut -c1-250 | head -15
