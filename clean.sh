#!/bin/sh
rm -rf target
rm -rf work

cd plugin
rm -rf target
rm -rf work
mvn -q clean
cd ..

cd plugin/violations-plugin
mvn -q clean
cd ../..

cd plugin-test
mvn -q clean
cd ..

find -name *.class | xargs rm
