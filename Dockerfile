FROM amazoncorretto:21
WORKDIR /app
COPY build/libs/minicti.jar    ./app.jar
EXPOSE 8080
CMD["java", "-jar", "app.jar"]
LABEL authors="g0ld3"