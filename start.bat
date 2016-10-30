@echo off 
:: 文件编码 请用 ANSI

title AES加密
::@mode con lines=18 cols=40

color 2f


echo     【-e】: 加密
echo     【-d】: 解密
echo     【 q】: 退出
echo -----------------------------

:encrypt
echo.
set txt=
set /p txt="加密:"
if "%txt%" == "q"  exit
if "%txt%" == "-q" exit
if "%txt%" == "-e" goto encrypt
if "%txt%" == "-d" goto decrypt
if "%txt%" == "-r" goto random
java -jar "%~dp0lib\aes.jar" "-e" %txt%
goto encrypt


:decrypt
echo.
set pwd=
set /p pwd="解密:"
if "%pwd%" == "q"  exit
if "%pwd%" == "-q" exit
if "%pwd%" == "-e" goto encrypt
if "%pwd%" == "-d" goto decrypt
if "%pwd%" == "-r" goto random
java -jar "%~dp0lib\aes.jar" "-d" %pwd%
goto decrypt

:random
echo.
set tmp= 
set num=
set /p num="随机数长度:"
if "%num%" == "q"  exit
if "%num%" == "-q" exit
if "%num%" == "-e" goto encrypt
if "%num%" == "-d" goto decrypt
if "%num%" == "-r" goto random
echo %num%|findstr /be "[0-9]*" >nul && set tmp=%num% || echo 请输入数字
java -jar "%~dp0lib\aes.jar" "-r" %tmp%
goto random


pause
