FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN groupadd --gid 10001 cobra && useradd --uid 10001 --gid cobra --no-create-home cobra
COPY --chown=cobra:cobra cobra360.jar /app/cobra360.jar
USER cobra
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=65.0","-jar","/app/cobra360.jar"]
