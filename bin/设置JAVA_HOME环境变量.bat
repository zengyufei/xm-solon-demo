chcp 65001
@echo off

set JAVA_HOME_PATH=
echo 请输入java安装路径：
set /p JAVA_HOME_PATH=

echo 安装路径为%JAVA_HOME_PATH%
set JAVA_HOME=%JAVA_HOME_PATH%
setx JAVA_HOME "%JAVA_HOME_PATH%"
setx -m JAVA_HOME "%JAVA_HOME_PATH%"
echo 安装成功！路径为%JAVA_HOME%
pause