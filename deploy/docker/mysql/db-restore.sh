#!/bin/sh

init() {
	#以换行为分隔符取变量
	IFS=$'\n'
	j=0
	i=1
	for line in `cat ./config.env` #使用循环按顺序读取/opt/iptest/ip中的IP
	do
	 IFS="="
	 let j=i++ #循环次数
	 array=($line) 
	 temp_param_first=${array[0]}
	 temp_param_two=${array[1]}
	 #echo "${temp_param_first}=${temp_param_two}"
	 param_first=$(echo $temp_param_first | sed -e 's/^[ ]*//g' | sed -e 's/[ ]*$//g')
	 param_two=$(echo $temp_param_two | sed -e 's/^[ ]*//g' | sed -e 's/[ ]*$//g')
	 #echo "${param_first}=${param_two}"
	 eval $param_first=$param_two
	done
	#恢复shell默认分割符配置
	IFS=$' \t\n'
}
 
init

filedir=/home/tmp
dbNames=(
$MYSQL_SERVICE_DATABASE_NAME
)
sql_user=root
sql_psw=$MYSQL_SERVICE_ROOT_PASSWORD

for dbname in ${dbNames[@]}
do
docker exec -i mysql mysql -u$sql_user -p$sql_psw <<EOF
    DROP DATABASE IF EXISTS $dbname;
    CREATE DATABASE $dbname DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    USE $dbname; 
    SOURCE $filedir/$dbname.sql;
    exit
EOF
done 
