FROM openjdk:21
WORKDIR /app
COPY ./build/libs/SongRequest-all.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "SongRequest-all.jar"]