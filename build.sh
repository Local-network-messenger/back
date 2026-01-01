#!/bin/bash

# Docker build с поддержкой кеширования через Buildx
# Требует: docker buildx (встроен в Docker Desktop или устанавливается отдельно)

set -e

echo "=== Building Local Messenger Microservices with Docker Buildx ==="

# Создание builder если его нет
if ! docker buildx ls | grep -q local-messenger-builder; then
    echo "Creating builder: local-messenger-builder"
    docker buildx create --name local-messenger-builder --use
else
    docker buildx use local-messenger-builder
fi

# Переменные
CACHE_DIR="/tmp/.buildx-cache"
mkdir -p "$CACHE_DIR"

echo "Building DB Service..."
docker buildx build \
    --file db/Dockerfile \
    --tag local-messenger/db-handler:latest \
    --cache-from=type=local,src="$CACHE_DIR" \
    --cache-to=type=local,dest="$CACHE_DIR",mode=max \
    --load \
    db/

echo "Building Backend Service..."
docker buildx build \
    --file back/Dockerfile \
    --tag local-messenger/backend:latest \
    --cache-from=type=local,src="$CACHE_DIR" \
    --cache-to=type=local,dest="$CACHE_DIR",mode=max \
    --load \
    back/

echo ""
echo "=== Build Complete ==="
echo "Built images:"
docker images | grep "local-messenger"

echo ""
echo "To start services, run:"
echo "  docker-compose up -d"
echo ""
echo "To view logs:"
echo "  docker-compose logs -f"
echo ""
echo "To stop services:"
echo "  docker-compose down"
