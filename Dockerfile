FROM openjdk:8-jre-alpine
COPY build/libs/linker-1.0.0.jar /linker.jar
CMD ["/usr/bin/java", "-jar", "/linker.jar"]
