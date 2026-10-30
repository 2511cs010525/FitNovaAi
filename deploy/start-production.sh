#!/bin/sh
set -eu

public_port="${PORT:-8080}"
sed "s/__PORT__/${public_port}/g" /opt/fitnova/nginx.conf.template > /tmp/nginx.conf

PORT=8081 java -jar /opt/fitnova/backend.jar &
backend_pid=$!

cd /opt/fitnova/web
PORT=3000 npm run start &
frontend_pid=$!

# Vercel sends the first request as soon as the public port opens. Let Spring
# finish database validation before Nginx accepts traffic.
node -e 'const deadline = Date.now() + 90000; async function ready() { try { const response = await fetch("http://127.0.0.1:8081/actuator/health"); if (response.ok) process.exit(0); } catch (_) {} if (Date.now() >= deadline) process.exit(1); setTimeout(ready, 500); } ready();'

nginx -c /tmp/nginx.conf -g "daemon off;" &
proxy_pid=$!

stop_all() {
    kill "$backend_pid" "$frontend_pid" "$proxy_pid" 2>/dev/null || true
}

trap stop_all INT TERM EXIT

while kill -0 "$backend_pid" 2>/dev/null \
    && kill -0 "$frontend_pid" 2>/dev/null \
    && kill -0 "$proxy_pid" 2>/dev/null; do
    sleep 2
done

exit 1
