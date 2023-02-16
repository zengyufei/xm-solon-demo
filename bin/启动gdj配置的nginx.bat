@echo off
chcp 65001

SET BIZ_NAME=轨道局
SET BIZ_CONF=%CD%/openresty/nginx-gdj.conf
SET NGINX_DIR=%CD%/openresty


color 0a 
TITLE Nginx Management  
GOTO MENU 
:MENU 
CLS 
ECHO %NGINX_DIR%
ECHO. 
ECHO. * * * *  Nginx Management  * * * * * * * * * * * 
ECHO. * * 
ECHO. * 1 启动 %BIZ_NAME%Nginx * 
ECHO. * * 
ECHO. * 2 关闭 %BIZ_NAME%Nginx * 
ECHO. * * 
ECHO. * 3 重启 %BIZ_NAME%Nginx * 
ECHO. * * 
ECHO. * 4 测试 %BIZ_NAME%配置文件 * 
ECHO. * *
ECHO. * 5 退 出 * 
ECHO. * * 
ECHO. * * * * * * * * * * * * * * * * * * * * * * * * 
ECHO. 
ECHO.请输入选择项目的序号： 

set /p ID= 
IF "%id%"=="1" GOTO cmd1 
IF "%id%"=="2" GOTO cmd2 
IF "%id%"=="3" GOTO cmd3 
IF "%id%"=="4" GOTO cmd4 
IF "%id%"=="5" EXIT 



:cmd1 
ECHO. 
ECHO.启动 %BIZ_NAME%Nginx......
IF NOT EXIST %NGINX_DIR%/nginx.exe ECHO %NGINX_DIR%/nginx.exe不存在 
cd %NGINX_DIR% 
IF EXIST %NGINX_DIR% start %NGINX_DIR%/nginx.exe -c %BIZ_CONF%
ECHO.OK 
PAUSE 
GOTO MENU 

:cmd2 
ECHO. 
ECHO.关闭 %BIZ_NAME%Nginx...... 
taskkill /F /IM nginx.exe > nul 
ECHO.OK 
PAUSE 
GOTO MENU 

:cmd3 
ECHO. 
ECHO.关闭 %BIZ_NAME%Nginx...... 
taskkill /F /IM nginx.exe > nul 
ECHO.OK 
GOTO cmd1
GOTO MENU

:cmd4 
ECHO. 
ECHO.测试 %BIZ_NAME%的配置文件...... 
IF EXIST %NGINX_DIR% call %NGINX_DIR%/nginx.exe -t -c %BIZ_CONF%
ECHO.OK 
PAUSE 
GOTO MENU 