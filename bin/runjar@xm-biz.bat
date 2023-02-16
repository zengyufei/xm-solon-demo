chcp 65001
@echo off
echo ###################
echo ###运行jar包文件###
echo ###################
echo __
echo 运行jar包文件
cd %~dp0
cd..
cd /D ./xm-biz/target
set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m
java -jar %JAVA_OPTS% xm-biz.jar
cd ../../bin
pause