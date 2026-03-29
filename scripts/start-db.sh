#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is not installed or not on PATH."
  exit 1
fi

if docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE_CMD=(docker-compose)
else
  echo "Neither 'docker compose' nor 'docker-compose' is available."
  exit 1
fi

cd "${PROJECT_ROOT}"
"${COMPOSE_CMD[@]}" up -d postgres

echo "Waiting for PostgreSQL to become healthy..."
for _ in {1..30}; do
  status=$(docker inspect --format='{{.State.Health.Status}}' socialnetwork-postgres 2>/dev/null || echo "starting")
  if [[ "${status}" == "healthy" ]]; then
    break
  fi
  sleep 1
done

echo "PostgreSQL container is running on localhost:5432."
echo "Spring defaults already match this container:"
echo "  DB_URL=jdbc:postgresql://localhost:5432/socialnetwork"
echo "  DB_USERNAME=postgres"
echo "  DB_PASSWORD=postgres"

