
#!/usr/bin/env sh

cd "$(dirname "$0")/shared"

../mvnw compile test install
