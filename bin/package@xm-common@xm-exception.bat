chcp 65001
@echo off

echo package project, gen jar
cd %~dp0
cd..
call ./bin/.mvn/mvnw.cmd -U -T 1C clean package -pl xm-common/xm-exception -am -Dmaven.test.skip=true
cd ./bin
pause
