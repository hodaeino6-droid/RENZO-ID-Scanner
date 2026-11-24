@echo off
rem Bootstrap launcher: run "gradle wrapper --gradle-version 8.7" once to generate the official gradle-wrapper.jar,
rem or open the project in Android Studio. Then use the official wrapper.
if exist "%~dp0gradle\wrapper\gradle-wrapper.jar" (
  java -classpath "%~dp0gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
) else (
  where gradle >nul 2>nul && gradle %* || (echo Install Gradle 8.7 or open in Android Studio & exit /b 1)
)
