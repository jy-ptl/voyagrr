#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: ./run.sh [full|infra|stop|logs] [build|nobuild] [logs|nologs]"
  exit 1
fi

MODE=$1

# Handle stop mode first
if [ "$MODE" = "stop" ]; then
  echo "Stopping and cleaning up all containers..."
  docker compose down
  docker system prune -f
  echo "All containers stopped and system pruned."
  exit 0
fi

# Handle logs-only mode
if [ "$MODE" = "logs" ]; then
  echo "Attaching to running container logs..."
  docker compose logs -f
  exit 0
fi

# Defaults
BUILD_OPTION="nobuild"
LOG_OPTION="nologs"

# If two arguments provided
if [ -n "$2" ] && [ -z "$3" ]; then
  if [[ "$2" == "build" || "$2" == "nobuild" ]]; then
    BUILD_OPTION=$2
  elif [[ "$2" == "logs" || "$2" == "nologs" ]]; then
    LOG_OPTION=$2
  else
    echo "Invalid second argument: $2"
    echo "Usage: ./run.sh [full|infra|stop|logs] [build|nobuild] [logs|nologs]"
    exit 1
  fi
fi

# If three arguments provided
if [ -n "$2" ] && [ -n "$3" ]; then
  BUILD_OPTION=$2
  LOG_OPTION=$3
fi

# Validate mode
if [[ "$MODE" != "full" && "$MODE" != "infra" ]]; then
  echo "Invalid mode: $MODE"
  echo "Usage: ./run.sh [full|infra|stop|logs] [build|nobuild] [logs|nologs]"
  exit 1
fi

# Validate build option
if [[ "$BUILD_OPTION" != "build" && "$BUILD_OPTION" != "nobuild" ]]; then
  echo "Invalid build option: $BUILD_OPTION"
  echo "Usage: ./run.sh [full|infra|stop|logs] [build|nobuild] [logs|nologs]"
  exit 1
fi

# Validate log option
if [[ "$LOG_OPTION" != "logs" && "$LOG_OPTION" != "nologs" ]]; then
  echo "Invalid logs option: $LOG_OPTION"
  echo "Usage: ./run.sh [full|infra|stop|logs] [build|nobuild] [logs|nologs]"
  exit 1
fi

if [ "$MODE" = "full" ]; then
  echo "Running full mode..."

  mvn clean install -pl common-lib -am
  docker compose down
  docker system prune -f

  if [ "$BUILD_OPTION" = "build" ]; then
    echo "Starting containers with build..."
    docker compose up -d --build
  else
    echo "Starting containers without build..."
    docker compose up -d
  fi

elif [ "$MODE" = "infra" ]; then
  echo "Starting infrastructure services only..."

  SERVICES="keycloak-database user-service-database storage-service-database encoding-service-database trip-service-database analysis-service-database recognition-service-database keycloak minio prometheus grafana"

  docker compose down
  docker system prune -f

  if [ "$BUILD_OPTION" = "build" ]; then
    echo "Starting infra services with build..."
    docker compose up -d --build $SERVICES
  else
    echo "Starting infra services without build..."
    docker compose up -d $SERVICES
  fi
fi

if [ "$LOG_OPTION" = "logs" ]; then
  docker compose logs -f
else
  echo "Skipped logs. Containers are running in background."
fi
