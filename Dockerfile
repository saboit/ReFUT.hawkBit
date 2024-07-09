######## Build CONTAINER
FROM maven:3.8-eclipse-temurin-11-alpine AS build
WORKDIR /app
COPY . .

RUN mvn clean install --batch-mode -DskipTests

######## Prod CONTAINER
FROM eclipse-temurin:11-jre-alpine
WORKDIR /app

ARG HAWKBIT_VERSION
ENV HAWKBIT_VERSION


COPY --from=build \
    /app/hawkbit-runtime/hawkbit-update-server/target/hawkbit-update-server-${HAWKBIT_VERSION}-SNAPSHOT.jar \
    /app/hawkbit-update-server.jar

VOLUME "/app/data"
VOLUME "/app/artifactrepo"

EXPOSE 8080

ENTRYPOINT ["java","-jar","hawkbit-update-server.jar", \
    "--spring.profiles.active=mysql", \
    "-Xms768m -Xmx768m -XX:MaxMetaspaceSize=250m -XX:MetaspaceSize=250m -Xss300K -XX:+UseG1GC -XX:+UseStringDeduplication -XX:+UseCompressedOops -XX:+HeapDumpOnOutOfMemoryError"]
