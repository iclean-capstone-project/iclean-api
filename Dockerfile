FROM openjdk:11
ENV TZ=Asia/Ho_Chi_Minh
COPY target/springboot-hhh-i-clean.jar springboot-hhh-i-clean.jar
ENTRYPOINT ["java", "-jar", "/springboot-hhh-i-clean.jar"]