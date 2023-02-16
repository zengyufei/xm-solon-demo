# 部署方案

## 1.创建

1. 创建 5.deploy.sh 的 $SOURCE_PATH 路径文件夹
```
mkdir -p /home/gdj/build
```
2. 创建
```
mkdir -p /docker
mkdir -p /home/deploy
```


## 2.上传
1. 上传 deploy/*.sh 目录到 /home/ 下
2. 上传 deploy/docker/* 目录到根目录 /docker/ 下
3. 上传 jar 包到 $SOURCE_PATH 路径文件夹

## 3.安装
#### A. 安装环境, 执行命令
```
bash /home/deploy/1.sync_time.sh
bash /home/deploy/2.install-docker.sh
bash /home/deploy/3.install-compose.sh
bash /home/deploy/4.install-jdk.sh
```

#### B. 安装 docker 容器

* 安装 mysql
```
cd /docker/mysql
bash install.sh
bash run.sh start
```
* 安装 rabbitmq
```
cd /docker/rabbitmq
bash install.sh
bash run.sh start
```
* 安装 redis
```
cd /docker/redis
bash run.sh start
```
* 安装 openresty
```
cd /docker/openresty
bash run.sh start
```
* 安装 es
```
cd /docker/es
bash run.sh start
```

## 4.创建

2. 上传 jar 包到 $SOURCE_PATH

3. 执行部署命令

```
bash 5.deploy.sh deploy
```
