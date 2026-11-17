#!/bin/sh
# Simplified Gradle Wrapper launcher (NOT the official generated script).
# It needs gradle/wrapper/gradle-wrapper.jar, which could not be created offline.
# Restore the official files with:  gradle wrapper --gradle-version 8.10.2
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$JAR" ]; then
  echo "gradle-wrapper.jar is missing. Open the project in Android Studio (it uses gradle-wrapper.properties)," >&2
  echo "or run: gradle wrapper --gradle-version 8.10.2" >&2
  exit 1
fi
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then JAVACMD="$JAVA_HOME/bin/java"; else JAVACMD=java; fi
exec "$JAVACMD" -Xmx64m -Xms64m -Dorg.gradle.appname=gradlew -classpath "$JAR" org.gradle.wrapper.GradleWrapperMain "$@"
