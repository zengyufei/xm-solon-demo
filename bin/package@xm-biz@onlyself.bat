chcp 65001
@echo off

echo 打包xm-biz
cd %~dp0
cd..
call ./bin/.mvn/mvnw.cmd -U -T 1C clean package -N -f=./xm-biz/pom.xml -Dmaven.test.skip=true
cd ./bin
pause
