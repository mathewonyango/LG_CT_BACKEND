#!/bin/bash

echo "🚀 Preparing Living Goods Commodity Tracker project structure..."

# Create folders
mkdir -p backend frontend

# .env file
cat <<EOF > .env
# DB
DB_USERNAME=postgres
DB_PASSWORD=admin@123

# GitHub (optional for private repo)
GITHUB_USERNAME=your-username
GITHUB_TOKEN=your-token
EOF

# Backend Dockerfile
cat <<EOF > backend/Dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY \$JAR_FILE app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EOF

# docker-compose.yml
cat <<'EOF' > docker-compose.yml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      POSTGRES_DB: commodity_tracker
    ports:
      - "5432:5432"
    volumes:
      - pg_data:/var/lib/postgresql/data

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    depends_on:
      - zookeeper

  backend:
    build: ./backend
    ports:
      - "9000:9000"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/commodity_tracker
      SPRING_DATASOURCE_USERNAME: ${DB_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
    depends_on:
      - postgres
      - kafka

  frontend:
    image: node:20-slim
    working_dir: /app
    volumes:
      - ./frontend:/app
    command: sh -c "npm install && npm run dev -- --host"
    ports:
      - "9001:9001"
    depends_on:
      - backend

volumes:
  pg_data:
EOF

# deploy.sh
cat <<'EOF' > deploy.sh
#!/bin/bash

set -e

source .env

echo "🌀 Cloning frontend repo..."
if [ -d "frontend/.git" ]; then
  echo "✅ Frontend already exists."
else
  git clone https://github.com/Winstone2/living-goods-commodity-tracker.git frontend || {
    echo "❌ Public clone failed. Trying private..."
    if [[ -z "$GITHUB_USERNAME" || -z "$GITHUB_TOKEN" ]]; then
      echo "❌ GitHub credentials missing in .env"
      ./send-mail.sh "Deployment Failed" "Missing GitHub credentials in .env"
      exit 1
    fi
    git clone https://${GITHUB_USERNAME}:${GITHUB_TOKEN}@github.com/Winstone2/living-goods-commodity-tracker.git frontend || {
      echo "❌ Clone failed with credentials too."
      ./send-mail.sh "Deployment Failed" "Private repo clone failed. Check your token or username."
      exit 1
    }
  }
fi

echo "🚀 Building and starting services..."
docker compose up --build -d || {
  ./send-mail.sh "Deployment Failed" "Docker Compose failed to start"
  exit 1
}

echo "✅ All services started successfully."
./send-mail.sh "Deployment Success" "All services for Living Goods Commodity Tracker are running."
EOF

# send-mail.sh
cat <<'EOF' > send-mail.sh
#!/bin/bash

subject="$1"
body="$2"

curl -s -X POST http://localhost:9000/api/v1/notify/email \
-H "Content-Type: application/json" \
-d '{
  "to": "mathewsagumbah@gmail.com",
  "subject": "'"${subject}"'",
  "body": "'"${body}"'"
}'
EOF

# Make scripts executable
chmod +x prepare.sh deploy.sh send-mail.sh

echo "✅ Project setup ready. Run ./deploy.sh next."
