# ==============================================================================
# Dockerfile - image (сервис изображений)
# Порт наружу НЕ публикуется (внутренний); слушает 8081
# ==============================================================================

# --- Стадия 1: сборка ----------------------------------------------------------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Сначала только pom.xml - зависимости кэшируются между пересборками
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Затем исходники и сборка JAR
COPY src ./src
RUN mvn -B -q package -DskipTests

# --- Стадия 2: runtime ---------------------------------------------------------
FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
