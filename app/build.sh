#!/bin/bash
mvn install
java \
-Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n \
-jar target/app-1.0-SNAPSHOT.jar server config.yml 
