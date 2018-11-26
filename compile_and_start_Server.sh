#!/usr/bin/env sh

cd "$(dirname "$0")"

sh "compile_and_install_Shared.sh"

cd server
../mvnw clean compile test install
java -jar target/Dominion-Server-0.0.1-SNAPSHOT.jar
