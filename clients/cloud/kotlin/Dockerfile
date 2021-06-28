FROM azul/zulu-openjdk-alpine:16

COPY target/kotlin-clients-example-6.2.0-jar-with-dependencies.jar /app.jar

WORKDIR /

ENTRYPOINT ["java", "-jar", "app.jar"]

