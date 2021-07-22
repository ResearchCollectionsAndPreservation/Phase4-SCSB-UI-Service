## SCSB-UI-SERVICE

The SCSB Middleware codebase and components are all licensed under the Apache 2.0 license, with the exception of a set of API design components (JSF, JQuery, and Angular JS), which are licensed under MIT X11.

SCSB-UI is a microservice application that provides the User Interface for the application.The User Interface has a user login on successful authentication. Based on the user’s privileges, the User Interface provides search functionality, request, reporting, batch jobs accessibility, and user management.

## Software Required

  - Java 11
  - Docker 19.03.13   

## Prerequisite

1. **Cloud Config Server**
Dspring.cloud.config.uri=http://phase4-scsb-config-server:8888

2. **external-application.properties**

security.oauth2.resource.jwt.key-value = XXXXXXXXXXXX

3. **Copy dist folder to src/main/static**

sudo cp -r Phase4-scsb-ui-angular-build/dist/*  /Phase4-scsb-ui-service/src/main/resources/static/


## Build

Download the Project , navigate inside project folder and build the project using below command

**./gradlew clean build -x test**

## Docker Image Creation

Naviagte Inside project folder where Dockerfile is present and Execute the below command

**sudo docker build -t phase4-scsb-ui-service .**

## Docker Run

User the below command to Run the Docker

**sudo docker run --name phase4-scsb-ui-service -v /data:/recap-vol   --label collect_logs_with_filebeat="true" --label decode_log_event_to_json_object="true" -p 9091:9091 -e   "ENV= -Dorg.apache.activemq.SERIALIZABLE_PACKAGES="*"  -Dspring.config.location=/recap-vol/config/external-application.properties  -Dspring.cloud.config.uri=http://phase4-scsb-config-server:8888 "  --network=scsb  -d phase4-scsb-ui-service**
