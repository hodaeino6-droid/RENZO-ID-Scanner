#!/bin/sh
# Bootstrap launcher. If the official wrapper jar exists, use it; otherwise download Gradle 8.7 once.
DIR=$(cd "$(dirname "$0")" && pwd)
JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"
if [ -f "$JAR" ]; then
  exec java -classpath "$JAR" org.gradle.wrapper.GradleWrapperMain "$@"
fi
G="$DIR/.gradle-dist/gradle-8.7"
if [ ! -x "$G/bin/gradle" ]; then
  mkdir -p "$DIR/.gradle-dist"
  echo "Downloading Gradle 8.7 ..." >&2
  curl -fsSL -o "$DIR/.gradle-dist/g.zip" https://services.gradle.org/distributions/gradle-8.7-bin.zip \
    && unzip -q "$DIR/.gradle-dist/g.zip" -d "$DIR/.gradle-dist" || { echo "Gradle download failed" >&2; exit 1; }
fi
exec "$G/bin/gradle" "$@"
