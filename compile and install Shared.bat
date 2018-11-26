@echo off
setlocal

REM cd into the batch file directory
cd "%~dp0"

cd shared
mvnw compile install

