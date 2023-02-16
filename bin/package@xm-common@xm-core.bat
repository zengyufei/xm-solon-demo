chcp 65001
@echo off

echo 打包xm-core
cd %~dp0
cd..
call ./bin/.mvn/mvnw.cmd -U -T 1C clean package -pl xm-common/xm-core -am -Dmaven.test.skip=true
cd ./bin
pause
