#!/bin/sh
mvn versions:update-properties
mvn -q hpi:run -Djava.util.logging.config.file=logging.properties -Djenkins.version=2.263.2 -Denforcer.skip=true
