@echo off
REM Convenience build script for Windows.
REM Ensures JAVA_HOME points at the JDK and clears MAVEN_ARGS, then runs the
REM Maven Wrapper. Usage:  build.cmd [maven goals...]   e.g.  build.cmd verify
set "MAVEN_ARGS="
if "%JAVA_HOME%"=="" set "JAVA_HOME=C:\Program Files\Java\jdk-25"
call mvnw.cmd %*
