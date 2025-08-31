FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/rfid-emu-service-0.0.1-SNAPSHOT.jar /app/rfid-emu-service.jar

ENTRYPOINT ["java", "-jar", "/app/rfid-emu-service.jar"]

EXPOSE 8083