@rem Simplified Gradle Wrapper launcher (NOT the official generated script).
@rem Needs gradle\wrapper\gradle-wrapper.jar. Restore official files with: gradle wrapper --gradle-version 8.10.2
@echo off
set APP_HOME=%~dp0
set JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar
if not exist "%JAR%" (
  echo gradle-wrapper.jar is missing. Open the project in Android Studio or run: gradle wrapper --gradle-version 8.10.2 1>&2
  exit /b 1
)
if defined JAVA_HOME (set JAVACMD=%JAVA_HOME%\bin\java.exe) else (set JAVACMD=java.exe)
"%JAVACMD%" -Xmx64m -Xms64m -Dorg.gradle.appname=gradlew -classpath "%JAR%" org.gradle.wrapper.GradleWrapperMain %*
