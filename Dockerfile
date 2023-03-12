FROM openjdk:17-oracle
VOLUME /tmp
EXPOSE 8090
ADD target/CloudStorage-0.0.1-SNAPSHOT.jar app-cloud.jar
ENTRYPOINT ["java", "-jar", "/app-cloud.jar"]


#EXPOSE 8090
#
#ADD target/CloudStorage-0.0.1-SNAPSHOT.jar app.jar
#
#ENTRYPOINT ["java","-jar","app.jar"]