#!/bin/sh
echo "start backup..." 

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
#MySQL Info 
sql_user=root
sql_psw=$MYSQL_SERVICE_ROOT_PASSWORD
backup_path=/home/backup/mysql/
array=(
$MYSQL_SERVICE_DATABASE_NAME
)
mkdir -p $backup_path
file_suffix=$(date +%Y_%m_%d_%H_%M_%S).sql 
for db_name in ${array[@]}
do 
  cd $backup_path
 
  if [ ! -d $backup_path/$db_name ]; then
  mkdir $db_name
  fi
 
  cd $db_name
 
  save_name=$db_name-$file_suffix
 
  echo "start backup...db: "$db_name" to "$save_name
  docker exec -i mysql bash <<EOF 
  mysqldump -u$sql_user -p$sql_psw $db_name > $backup_path$db_name/$save_name 
  exit 
EOF
done 
