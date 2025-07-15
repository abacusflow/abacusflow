#!/bin/bash
set -e

echo "→ Starting PostgreSQL..."
sudo service postgresql start

echo "→ Checking PostgreSQL service status..."
sudo service postgresql status

echo "→ Checking if PostgreSQL is listening on port 5432..."
netstat -tlnp | grep 5432 || echo "PostgreSQL not listening on 5432"

echo "→ Checking PostgreSQL processes..."
ps aux | grep postgres || echo "No PostgreSQL processes found"

echo "→ Waiting for PostgreSQL to be ready..."
# 等待 PostgreSQL 真正启动
for i in {1..30}; do
    if pg_isready -h localhost -p 5432 -U postgres; then
        echo "→ PostgreSQL is ready!"
        break
    fi
    echo "→ Waiting... ($i/30)"
    sleep 1
done

echo "→ PostgreSQL is up. Running your commands..."
exec "$@"