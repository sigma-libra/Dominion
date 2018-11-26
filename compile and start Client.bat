@echo off
setlocal

REM cd into the batch file directory
cd "%~dp0"

call "compile and install Shared.bat" && cd client && mvnw compile exec:java
