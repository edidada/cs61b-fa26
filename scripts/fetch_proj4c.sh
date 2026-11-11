#!/usr/bin/env bash
set -euo pipefail
# From repository root: fetch verified official jars and the official data archive.
module=${1:-proj4cd}
mkdir -p "$module/libs" "$module/evidence"
base=https://raw.githubusercontent.com/Berkeley-CS61B/library-fa26/7d9a380f3d881630a8cbf6382945a2d27618e728
export module base
jq -r '.jars[] | [.name, .git_blob] | @tsv' "$module/dependencies.json" |
  xargs -P 6 -n 2 bash -c 'curl --fail --location --retry 3 --silent --show-error "$base/$1" -o "$module/libs/$1"; test "$(git hash-object "$module/libs/$1")" = "$2"' _
# The Fall 2026 course page currently points to a Drive archive named sp26.
gdown 1h4GKpQKd51v41vq3dhwR4gWHdiyHWBGF -O "$module/wordnet.zip"
sha256sum "$module/wordnet.zip" > "$module/evidence/archive-sha256.txt"
unzip -q -o "$module/wordnet.zip" -d "$module/download"
source_file=$(find "$module/download" -name synsets_size82191.txt -print -quit)
test -n "$source_file"
mkdir -p "$module/data"
cp "$(dirname "$source_file")/"*.csv "$(dirname "$source_file")/"*.txt "$module/data/"
sha256sum "$module/data/"*.csv "$module/data/"*.txt > "$module/evidence/data-sha256.txt"

# Small official input examples for reproducible design tracing (not the large corpus).
for name in synsets_size16.txt hyponyms_size16.txt synsets_eecs.txt hyponyms_eecs.txt word_history_eecs.csv; do
  cp "$module/data/$name" "$module/evidence/$name"
done
