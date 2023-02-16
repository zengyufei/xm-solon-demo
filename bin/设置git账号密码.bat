chcp 65001
@echo off

echo ##################################################
echo ###该脚本可以永久的配置http和https协议的git密码###
echo ###             power by zengyufei             ###
echo ##################################################
echo __

rem input
set gitusername=
set gitpassword=
echo 请输入username：
set /p gitusername=
echo 请输入password：
set /p gitpassword=

rem output
echo __
echo https://%gitusername%:%gitpassword%@github.com > %USERPROFILE%/.git-credentials
git config --global credential.helper store
echo 'Sucess!'
pause