#!/usr/bin/env bash
set -euo pipefail
# Run unchanged official interactive drivers with three input sizes.
tool=${1:-maven}
cd hw07
mkdir -p timing
if [[ "$tool" = maven ]]; then classes=target/classes:target/test-classes; else classes=build/classes/java/main:build/classes/java/test; fi
for driver in InsertRandomSpeedTest InsertInOrderSpeedTest BucketsSpeedTest; do
  if [[ "$driver" = BucketsSpeedTest ]]; then
    input='8\n1000\ny\n5000\ny\n10000\nn\n'
  elif [[ "$driver" = InsertRandomSpeedTest ]]; then
    input='8\n1000\n1000\n1000\ny\n5000\n5000\n5000\ny\n10000\n10000\n10000\nn\n'
  else
    input='1000\n1000\n1000\ny\n5000\n5000\n5000\ny\n10000\n10000\n10000\nn\n'
  fi
  printf '%b' "$input" | timeout 180 java -cp "$classes:libs/algs4.jar" "speed.$driver" > "timing/$driver.txt" 2>&1
  if grep -E 'Exception|Error|Stack Overflow' "timing/$driver.txt"; then exit 1; fi
  if [[ "$driver" = BucketsSpeedTest ]]; then expected=15; else expected=9; fi
  test "$(grep -Ec ': [0-9]+\.[0-9]+ sec' "timing/$driver.txt")" = "$expected"
done
java -version > timing/environment.txt 2>&1
