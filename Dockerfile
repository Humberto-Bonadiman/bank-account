FROM maven:3.8.6-openjdk-11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
WORKDIR /app/source
COPY . .
RUN mvn clean install -DskipTests

CMD mvn spring-boot:run