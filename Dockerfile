FROM openjdk:17
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/libs/*.jar /app/prothesen-manager-server.jar
WORKDIR /app
ENTRYPOINT ["java","-jar","/app/prothesen-manager-server.jar"]