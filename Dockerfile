FROM openjdk:11
COPY target/springboot-hhh-i-clean.jar springboot-hhh-i-clean.jar
ENTRYPOINT ["java", "-jar", "/springboot-hhh-i-clean.jar"]