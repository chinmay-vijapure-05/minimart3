# ---------- Step 1: Build Stage ----------
FROM gradle:8.5.0-jdk21 AS build
COPY . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build -x test

# ---------- Step 2: Run Stage ----------
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
