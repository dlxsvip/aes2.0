@echo off 
:: �ļ����� ���� ANSI

title AES����
::@mode con lines=18 cols=40

::	  0 = ��ɫ       8 = ��ɫ
::    1 = ��ɫ       9 = ����ɫ
::    2 = ��ɫ       A = ����ɫ
::    3 = ����ɫ     B = ��ǳ��ɫ
::    4 = ��ɫ       C = ����ɫ
::    5 = ��ɫ       D = ����ɫ
::    6 = ��ɫ       E = ����ɫ
::    7 = ��ɫ       F = ����ɫ

:: ��:2f ��һ��Ϊ�������ڶ�����Ϊǰ��

echo     ��-e��: ����
echo     ��-d��: ����
echo     �� q��: �˳�
echo -----------------------------

:encrypt
color 02
echo.
set txt=
set /p txt="����:"
if "%txt%" == "q"  exit
if "%txt%" == "-q" exit
if "%txt%" == "-e" goto encrypt
if "%txt%" == "-d" goto decrypt
if "%txt%" == "-r" goto random
java -jar "%~dp0lib\aes.jar" "-e" %txt%
goto encrypt


:decrypt
color 04
echo.
set pwd=
set /p pwd="����:"
if "%pwd%" == "q"  exit
if "%pwd%" == "-q" exit
if "%pwd%" == "-e" goto encrypt
if "%pwd%" == "-d" goto decrypt
if "%pwd%" == "-r" goto random
java -jar "%~dp0lib\aes.jar" "-d" %pwd%
goto decrypt

:random
color 06
echo.
set tmp= 
set num=
set /p num="���������:"
if "%num%" == "q"  exit
if "%num%" == "-q" exit
if "%num%" == "-e" goto encrypt
if "%num%" == "-d" goto decrypt
if "%num%" == "-r" goto random
echo %num%|findstr /be "[0-9]*" >nul && set tmp=%num% || echo ����������
java -jar "%~dp0lib\aes.jar" "-r" %tmp%
goto random


:: call:cl
:: :cl
:: color 02
:: echo ����������
:: goto random

pause
