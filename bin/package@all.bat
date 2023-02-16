chcp 65001
@echo off

echo 打包项目
cd %~dp0
cd..
call ./bin/.mvn/mvnw.cmd -U -T 1C clean package -Dmaven.test.skip=true
cd ./bin
pause
