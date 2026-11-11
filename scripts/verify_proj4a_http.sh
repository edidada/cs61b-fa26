#!/usr/bin/env bash
set -euo pipefail
# Start the real original Javalin server, then exercise the supplied HTTP routes.
tool=${1:-maven}
cd proj4a
if [[ "$tool" = maven ]]; then classes=target/classes:target/test-classes; else classes=build/classes/java/main:build/classes/java/test; fi
java -Djava.awt.headless=true -cp "$classes:libs/*" main.Main > evidence/server.log 2>&1 &
server_pid=$!
trap 'kill "$server_pid" 2>/dev/null || true' EXIT
ready=false
for i in $(seq 1 60); do
  if curl --fail --silent http://127.0.0.1:4567/ngordnet_4a.html -o evidence/static.html; then ready=true; break; fi
  kill -0 "$server_pid"
  sleep 1
done
test "$ready" = true
java -Djava.awt.headless=true -cp "$classes:libs/*" HttpProbe > evidence/http.txt
