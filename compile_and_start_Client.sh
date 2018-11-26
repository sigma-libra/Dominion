#!/usr/bin/env sh

cd "$(dirname "$0")"

sh "compile_and_install_Shared.sh"

cd client
../mvnw clean compile test exec:java
