set http_proxy=http://127.0.0.1:1080
set https_proxy=https://127.0.0.1:1080

mvn -N  clean install deploy -P release -Dmaven.test.skip=true
pause
