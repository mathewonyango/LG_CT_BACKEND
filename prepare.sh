#!/bin/bash

set -e

echo "🔍 Checking for Docker..."
if ! command -v docker &> /dev/null; then
  echo "❌ Docker is not installed. Please install Docker and try again."
  exit 1
fi

echo "🔍 Checking for Docker Compose..."
if ! command -v docker-compose &> /dev/null; then
  echo "❌ Docker Compose is not installed. Please install it and try again."
  exit 1
fi

echo "🔗 Ensuring shared Docker network 'lg_network' exists..."
if ! docker network ls | grep -q "lg_network"; then
  echo "📡 Creating shared network 'lg_network'..."
  docker network create lg_network
else
  echo "✅ Shared network 'lg_network' already exists."
fi

echo "⚙️ Building and starting services in detached mode..."
docker-compose up -d --build

echo "✅ All services are running!"
docker ps
