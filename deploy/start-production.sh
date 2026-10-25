#!/bin/sh
set -eu

public_port="${PORT:-8080}"
sed "s/__PORT__/${public_port}/g" /opt/fitnova/nginx.conf.template > /tmp/nginx.conf

PORT=8081 java -jar /opt/fitnova/backend.jar &
backend_pid=$!

cd /opt/fitnova/web
PORT=3000 npm run start &
frontend_pid=$!

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
