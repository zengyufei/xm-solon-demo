set http_proxy=http://127.0.0.1:1080
set https_proxy=https://127.0.0.1:1080

call mvn -DskipTests=true spring-javaformat:apply
call mvn clean install -Drevision=1.0.6 deploy -P release -Dmaven.test.skip=true
pause
