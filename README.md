# LG_CT_BACKEND
ing Frontend Build  on port 9001:

serve -s . -l 9001

Docker Configurations:

After using the dockr file defined,
Start the services defined in it by running:docker-compose up -d


docker ps list all the docker containers running

My container:
docker exec -it livinggoodsbackend-kafka-1 bash


My case my container name is :livinggoodsbackend-kafka-1

Clean everything not in use:
docker system prune -a --volumes -f


Removing image using image id:
docker rmi b50082bc3670





Create topics :kafka-topics --create --topic test-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

How to stream data rom kafka of a user-topic:
kafka-console-consumer --topic user-topic --from-beginning --bootstrap-server localhost:9092


Other Docker commands:
# Create a topic
kafka-topics --create --topic test-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# Produce messages
kafka-console-producer --topic test-topic --bootstrap-server localhost:9092

# In a new terminal: Consume messages
kafka-console-consumer --topic test-topic --from-beginning --bootstrap-server localhost:9092
