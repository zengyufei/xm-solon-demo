#!/bin/bash
project_path=$(pwd)
echo $project_path
NAME="${project_path##*/}"
echo $NAME

case "$1" in
    start)
        echo "Starting $NAME…"
	docker network create es_default
        docker-compose  up -d
        echo "Finished!"
        ;;
    stop)
        echo "Stoping $NAME…"
        docker-compose down
	docker network remove es_default
        echo "Finished!"
        ;;
    restart)
        bash $0 stop
        bash $0 start
        ;;
    *)
        echo "Usage: $NAME { start | stop | restart } "
        exit 1
esac
