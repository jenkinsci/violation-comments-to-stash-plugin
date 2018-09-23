#!/bin/bash
mvn versions:update-properties
mvn release:prepare release:perform -B || exit 1
mvn package
git commit -a --amend --no-edit
git push -f
