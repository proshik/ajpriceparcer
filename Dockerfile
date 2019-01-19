# First stage to build the application
FROM maven:3.6.0-jdk-11 AS build-env

ADD ./pom.xml pom.xml
ADD ./src src/

#RUN apt-get install --reinstall ca-certificates-java

RUN mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V
RUN cp target/*.jar target/app.jar

# Final stage to define our minimal runtime
FROM openjdk:11-jre

COPY --from=build-env target/app.jar /app/app.jar

CMD ["java","-jar","/app/app.jar","-Xms64m","-Xmx128m"]