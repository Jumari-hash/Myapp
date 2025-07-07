#!/bin/sh

# Gradle wrapper script for Unix

APP_BASE_NAME=`basename "$0"`
DIRNAME=`dirname "$0"`

# Locate Java
if [ -z "$JAVA_HOME" ]; then
  JAVA_CMD=$(which java)
else
  JAVA_CMD="$JAVA_HOME/bin/java"
fi

if [ ! -x "$JAVA_CMD" ]; then
  echo "ERROR: JAVA_HOME is not set correctly or java not found in PATH."
  exit 1
fi

CLASSPATH="$DIRNAME/gradle/wrapper/gradle-wrapper.jar"

exec "$JAVA_CMD" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
