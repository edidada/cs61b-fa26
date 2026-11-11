#!/usr/bin/env bash
set -euo pipefail
# From repository root: fetch verified official jars and the official data archive.
module=${1:-proj4a}
mkdir -p "$module/libs" "$module/evidence"
base=https://raw.githubusercontent.com/Berkeley-CS61B/library-fa26/7d9a380f3d881630a8cbf6382945a2d27618e728
export module base
jq -r '.jars[] | [.name, .git_blob] | @tsv' "$module/dependencies.json" |
  xargs -P 6 -n 2 bash -c 'curl --fail --location --retry 3 --silent --show-error "$base/$1" -o "$module/libs/$1"; test "$(git hash-object "$module/libs/$1")" = "$2"' _
# The Fall 2026 course page currently points to a Drive archive named sp26.
gdown 149_H-aFsKjVa0IwX4-mnMnJc82G465SG -O "$module/ngrams.zip"
sha256sum "$module/ngrams.zip" > "$module/evidence/archive-sha256.txt"
unzip -q -o "$module/ngrams.zip" -d "$module/download"
source_file=$(find "$module/download" -name word_history_size14377.csv -print -quit)
test -n "$source_file"
mkdir -p "$module/data"
cp "$(dirname "$source_file")/"*.csv "$module/data/"
sha256sum "$module/data/"*.csv > "$module/evidence/data-sha256.txt"
