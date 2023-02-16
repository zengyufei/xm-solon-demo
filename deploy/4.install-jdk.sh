#!/bin/bash
set -e

# 检查软件是否安装
check_install() {
	#echo "检查软件是否安装"
	name=$1
	#echo "检查${name}是否安装"
	if ! [ -x "$(command -v $name)" ]; then
			#echo "${name} 未安装"
			return 0
	else
			#echo "${name} 已安装"
			return 1
	fi
}

install_before() {
	echo "install_before"
}
install_after() {
	echo "install_after"
}
# deian
install_debian() {
	echo "安装命令 sudo apt install openjdk-8-jre-headless"
	sudo apt install -y openjdk-8-jre-headless
}

# centos
install_centos() {
	echo "安装命令 sudo yum install -y java-1.8.0-openjdk"
	sudo yum install -y java-1.8.0-openjdk
}

# deian
check_install_debian() {
	echo "deian 系统安装 $1 $2 $3 $4"
	name1=$1
	name2=$2
	name3=$3
	name4=$4
	for ((i=1;i<=4;i++))     #此处也可以写  for i in {1..9}
	do
		temp=name${i}
		bl=${!temp}
		#echo "参数 name${i} = ${bl}"
		if [ -z "$bl" ]; then
			#echo "参数 name${i} 为空， 跳出"
			continue
		fi
		check_install $bl
		is_install=$?
    if [ $is_install == 0 ];then
      echo "未安装 ${bl}"
      echo "安装 $name1 $name2 $name3 $name4"
      install_before
      install_debian $name1 $name2 $name3 $name4
      install_after
      break
    elif [ $is_install == 1 ];then
      echo "已安装 ${bl}"
      continue
    else
      echo "发生异常"
    fi
	done
}

# centos
check_install_centos() {
	echo "centos 系统安装 $1 $2 $3 $4"
	name1=$1
	name2=$2
	name3=$3
	name4=$4
	is_install=$(check_install $name1 $name2 $name3 $name4)
	echo "检查安装返回值${is_install}"
	if [ ! $is_install ];then
		install_before
		install_centos $name1 $name2 $name3 $name4
		install_after
	fi
}

# 判断是否安装，没安装则自动安装
if [ -f /etc/debian_version ]; then
   echo "当前是基于debian的系统"
   check_install_debian java
elif [ -f /etc/redhat-release ]; then
   echo "当前是centos系统"
   check_install_centos java
else
   echo "不知道当前是什么系统，无法继续"
   exit;
fi

# 主命令
echo "执行命令："
java -version
