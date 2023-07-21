FROM openjdk:17-jdk-alpine
WORKDIR /server
COPY target/grego-chat-server.jar grego-chat-server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "grego-chat-server.jar"]

# docker build -t grego-chat-server .
# docker run --name grego-chat-server -dp 8080:8080 --detach grego-chat-server