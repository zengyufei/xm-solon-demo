chcp 65001
@echo on

echo package project, gen jar
cd %~dp0
cd..
call ./bin/.mvn/mvnw.cmd -U -T 1C clean package -pl xm-common -am -Dmaven.test.skip=true
cd ./bin
pause
