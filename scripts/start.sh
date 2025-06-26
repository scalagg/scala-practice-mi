#!/bin/bash

# Configuration
APP_NAME="practice-application"
JAR_FILE="ScalaPractice-application.jar"
LOG_FILE="logs/$APP_NAME.log"
ARGS="--dev=false --redishost=172.18.0.1 --redisport=6379 --redispass=mmJsasamn8tVpKnzOPsrCrH0XFn8raHE --mongodatabase=PotPvP --mongouri=mongodb://scalaadmin:Y9xpm1uK5bSHwEQIWPkymBdhwPTEF8hq@100.125.41.103:27017/?authSource=admin"

# Create logs directory if missing
mkdir -p logs

echo "[$(date)] Starting $APP_NAME..." >> "$LOG_FILE"

while true; do
    java -jar "$JAR_FILE" $ARGS >> "$LOG_FILE" 2>&1
    echo "[$(date)] $APP_NAME crashed or exited. Restarting in 5 seconds..." >> "$LOG_FILE"
    sleep 5
done
