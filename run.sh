#!/bin/sh
cd plugin
mvn -q hpi:run -Djava.util.logging.config.file=../logging.properties
