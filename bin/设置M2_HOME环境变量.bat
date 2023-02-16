chcp 65001
@echo off

set mavenpath=
echo 请输入maven安装路径：
set /p mavenpath=

echo 安装路径为%mavenpath%
set M2_HOME=%mavenpath%
setx M2_HOME "%mavenpath%"
setx -m M2_HOME "%mavenpath%"
echo 安装成功！路径为%M2_HOME%
pause