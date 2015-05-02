#!/bin/sh
rm -rf target
rm -rf work

mvn -q clean

cd plugin-test
mvn -q clean
cd ..

find -name *.class | xargs rm
