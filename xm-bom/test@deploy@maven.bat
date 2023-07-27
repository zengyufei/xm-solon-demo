set http_proxy=http://127.0.0.1:1080
set https_proxy=https://127.0.0.1:1080

call mvn deploy -P releaseafter -Dmaven.test.skip=true
pause
