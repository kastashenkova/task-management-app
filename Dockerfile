FROM maven:3.9.9-eclipse-temurin-17 AS base
WORKDIR /app
RUN apt-get update && apt-get install -y ca-certificates ca-certificates-java && \
    update-ca-certificates && \
    update-ca-certificates -f && \
    /var/lib/dpkg/info/ca-certificates-java.postinst configure
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B dependency:go-offline

FROM base AS dev
WORKDIR /app
COPY src ./src
EXPOSE 8090
CMD ["mvn", "spring-boot:run"]

FROM base AS builder
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B package -DskipTests

FROM eclipse-temurin:17-jre AS prod
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8090
CMD ["java", "-jar", "app.jar"]
