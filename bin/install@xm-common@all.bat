chcp 65001
@echo off

echo package project, gen jar
cd %~dp0
cd..
call ./bin/.mvn/mvnw.cmd -U -T 1C clean install -f=./xm-common/pom.xml -Dmaven.test.skip=true
cd ./bin
pause
