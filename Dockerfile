FROM openjdk:11
ADD target/automated-ticketing.jar automated-ticketing.jar
EXPOSE 8181
CMD ["java", "-jar", "automated-ticketing.jar"]

