# Etapa 1: compilar el proyecto
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /workspace

COPY servicios-comunes ./servicios-comunes
COPY CarritoCompra ./CarritoCompra

RUN mvn -f servicios-comunes/pom.xml clean install -DskipTests
RUN mvn -f CarritoCompra/pom.xml clean install -DskipTests

# Despegar la aplicacion
FROM eclipse-temurin:21-jre

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=docker
ENV SPRING_CONFIG_LOCATION=classpath:/application-docker.yml

COPY --from=builder /workspace/CarritoCompra/target/*.jar CarritoCompra.jar

EXPOSE 6005

ENTRYPOINT [ "java", "-jar", "CarritoCompra.jar" ]