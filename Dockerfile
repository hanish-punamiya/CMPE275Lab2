FROM openjdk:16-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=target/lab2-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} .
CMD [ "java", "-jar",  "/lab2-0.0.1-SNAPSHOT.jar"]