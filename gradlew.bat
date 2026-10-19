@echo off
setlocal

set APP_HOME=%~dp0
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

if not defined JAVA_HOME goto use_java
set JAVACMD=%JAVA_HOME%\bin\java.exe
goto run

:use_java
set JAVACMD=java.exe

:run
if not exist "%CLASSPATH%" (
  echo Gradle wrapper JAR is missing: %CLASSPATH%
  exit /b 1
)

"%JAVACMD%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
endlocal
