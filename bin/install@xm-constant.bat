chcp 65001
@echo off

echo 打包bom
cd %~dp0
cd..
@REM call ./bin/.mvn/mvnw.cmd -U -T 1C clean install -pl xm-common/xm-constant -amd -Dmaven.test.skip=true
call ./bin/.mvn/mvnw.cmd -U -T 1C clean install -N -f=./xm-common/xm-constant/pom.xml -Dmaven.test.skip=true
cd ./bin
pause
