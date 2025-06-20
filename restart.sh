#!/bin/bash

APP_NAME=app.jar
APP_DIR=$(dirname "$0")
PID=$(pgrep -f $APP_NAME)

if [ -n "$PID" ]; then
  echo "Stopping running app (PID: $PID)..."
  kill -9 $PID
  sleep 2
fi

echo "Starting app...(후후후...)"
cd $APP_DIR
nohup java -jar $APP_NAME > logs_$(date +%Y%m%d_%H%M%S).out 2>&1 &
