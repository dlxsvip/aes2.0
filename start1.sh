#!/usr/bin/bash

echo -e "\033[32m=============================\033[0m"
echo -e "\033[32m 提示: 退出请按【q + 回车】      \033[0m"
echo -e "\033[32m 解密: 密文后加 -d 参数         \033[0m"
echo -e "\033[32m=============================\033[0m"


encryptFun(){
    java -jar `dirname $0`/lib/aes.jar "$1"
}


dncryptFun(){
    java -jar `dirname $0`/lib/aes.jar "$1" "$2"
}



while :
do
    read -p "请输入密码 :" pwd
    if [ "$pwd" == "" ];then
        echo -e "\033[31merror:not null\033[0m"
    elif [ "$pwd" == "q" ];then
        exit 0
    else
        arr=($pwd)
        if [ ${#arr[@]} == 1 ];then
            encryptFun "$pwd";
        elif [ ${#arr[@]} == 2 ];then
            if [ "-d" == ${arr[0]} ];then
                dncryptFun ${arr[1]}  "-d";
            elif [ "-d" == ${arr[1]} ];then
                dncryptFun ${arr[0]}  "-d";
            else
                echo -e "\033[31m参数错误\033[0m"
            fi
        else
            echo -e "\033[31m参数错误\033[0m";
        fi
    fi
done


