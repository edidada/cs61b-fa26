#!/usr/bin/env bash
set -euo pipefail
classes=${1:?compiled classes required}
test_classes=${2:?compiled test classes required}
library=${3:?algs4 jar required}
output=${4:?output directory required}
mkdir -p "$output"
java -version > "$output/environment.txt" 2>&1
uname -a >> "$output/environment.txt"
for n in 1000 5000 10000; do
  printf '8\n%s\nn\n' "$n" > "$output/input-$n.txt"
  timeout 120 java -cp "$classes:$test_classes:$library" InsertRandomSpeedTest \
    < "$output/input-$n.txt" > "$output/timing-$n.txt" 2>&1
  # The original driver catches exceptions, so exit status alone is insufficient.
  if grep -Eqi 'exception|stack overflow|error' "$output/timing-$n.txt"; then
    cat "$output/timing-$n.txt"
    exit 1
  fi
  grep -Eq 'class ULLMap: [0-9]+\.[0-9]+ sec' "$output/timing-$n.txt"
  grep -Eq 'class BSTMap: [0-9]+\.[0-9]+ sec' "$output/timing-$n.txt"
  grep -Eq "Java's Built-in TreeMap: [0-9]+\.[0-9]+ sec" "$output/timing-$n.txt"
  grep -Eq "Java's Built-in HashMap: [0-9]+\.[0-9]+ sec" "$output/timing-$n.txt"
done
printf 'PASS: original interactive timing driver completed N=1000,5000,10000 L=8 for four maps\n' > "$output/result.txt"
