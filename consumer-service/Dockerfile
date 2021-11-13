FROM openjdk:8-jdk

VOLUME /tmp

RUN mkdir ./report

ADD target/consumer-service-1.0.jar app.jar

ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]