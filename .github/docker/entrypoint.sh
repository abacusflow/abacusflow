#!/bin/bash
set -e

echo "→ Starting PostgreSQL..."
sudo service postgresql start

echo "→ PostgreSQL is up. Running your commands..."
exec "$@"
