FROM openjdk:11.0.1-jre-slim-stretch
MAINTAINER ar@alryj.com
COPY target/adserv-admin-0.0.1-SNAPSHOT.jar adserv-admin-0.0.1-SNAPSHOT.jar
COPY application.properties application.properties
CMD java -jar adserv-admin-0.0.1-SNAPSHOT.jar
EXPOSE 8080
