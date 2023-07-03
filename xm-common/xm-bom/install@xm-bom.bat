chcp 65001
@echo off

echo 打包bom
call ../../bin/.mvn/mvnw.cmd -U -T 1C clean install -N -f=./pom.xml -Dmaven.test.skip=true
pause
