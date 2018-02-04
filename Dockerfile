# First stage to build the application
FROM maven:3.5-jdk-8 AS build-env

ADD ./pom.xml pom.xml
ADD ./src src/

#RUN apt-get install --reinstall ca-certificates-java

RUN mvn clean install
RUN cp target/applepriceparcer-jar-with-dependencies.jar target/app.jar

# Final stage to define our minimal runtime
FROM openjdk:8-jre

COPY --from=build-env target/app.jar /app/app.jar

CMD ["java","-jar","app.jar"]