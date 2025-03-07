FROM openjdk:17-jdk
ARG JAR_FILE=target/db-skins-0.0.1-SNAPSHOT.jar
RUN mkdir /jars
WORKDIR /jars
COPY ${JAR_FILE} /jars
ENTRYPOINT java -jar /jars/db-skins-0.0.1-SNAPSHOT.jar