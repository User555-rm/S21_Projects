# ---- стадия сборки: JDK 25 + обёртка mvnw из проекта ----
FROM eclipse-temurin:25-jdk AS build

WORKDIR /build

COPY pom.xml mvnw ./
COPY .mvn ./.mvn
RUN chmod +x mvnw && ./mvnw -B -q -DskipTests dependency:go-offline || true

COPY src ./src
RUN ./mvnw -B -q -DskipTests package && \
    find target -maxdepth 1 -type f -name '*.jar' \
         ! -name '*.plain.jar' ! -name '*.original' \
         -exec cp {} /build/app.jar \;

FROM eclipse-temurin:25-jdk

WORKDIR /app
COPY --from=build /build/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
