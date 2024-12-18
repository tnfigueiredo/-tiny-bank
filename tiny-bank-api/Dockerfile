# Build steps
FROM gradle:8.11.1-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build --no-daemon

# Creating image
FROM openjdk:17-alpine3.14 AS runtime
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*-SNAPSHOT.jar /app/tiny-bank-api.jar
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "tiny-bank-api.jar"]