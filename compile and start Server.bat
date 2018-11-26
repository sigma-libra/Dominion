@echo off
setlocal

REM cd into the batch file directory
cd "%~dp0"

call "compile and install Shared.bat" && mvnw clean compile test install && java -jar "%~dp0server\target\Dominion-Server-0.0.1-SNAPSHOT.jar"
