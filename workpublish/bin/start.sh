#!/bin/bash

# 数组包含每个应用程序的可执行JAR文件路径和名称
JAR_FILES=(
    "../dataServer/dataServer.jar --spring.profiles.active=four"
    "../dataServer/dataServer.jar --spring.profiles.active=sec"
    "../dataServer/dataServer.jar --spring.profiles.active=thir"
	"../dataServer/dataServer.jar"
	"../metaServer/metaServer.jar --spring.profiles.active=replica"
	"../metaServer/metaServer.jar"
)

# 遍历数组并启动每个应用程序
for jar_file in "${JAR_FILES[@]}"; do
    if [ ! -f "$jar_file" ]; then
        echo "ERROR: JAR file not found: $jar_file"
        exit 1
    fi

    # 启动应用程序
    java -jar "$jar_file" &
done

echo "Started multiple Spring Web applications."