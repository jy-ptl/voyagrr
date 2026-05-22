#!/bin/bash

USAGE="Usage:
  ./run.sh full [build|nobuild] [logs|nologs]   - Run everything
  ./run.sh infra [build|nobuild] [logs|nologs]  - Run infrastructure only
  ./run.sh swap <service1> [service2] ...        - Rebuild & swap specific services
  ./run.sh stop                                  - Stop all containers
  ./run.sh logs [service1] [service2] ...        - Attach to logs"

if [ -z "$1" ]; then
  echo "$USAGE"
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
  shift
  if [ $# -gt 0 ]; then
    echo "Attaching to logs for: $@"
    docker compose logs -f "$@"
  else
    echo "Attaching to all container logs..."
    docker compose logs -f
  fi
  exit 0
fi

# Handle swap mode — rebuild & restart specific services
if [ "$MODE" = "swap" ]; then
  shift
  if [ $# -eq 0 ]; then
    echo "Error: swap mode requires at least one service name."
    echo ""
    echo "Available application services:"
    echo "  user-service, storage-service, trip-service, encoding-service,"
    echo "  processing-service, metadata-service, analysis-service,"
    echo "  recognition-service, api-gateway, eureka"
    echo ""
    echo "Example: ./run.sh swap storage-service processing-service"
    exit 1
  fi

  SERVICES="$@"

  # Java services need Maven build (they COPY pre-built JARs into Docker images)
  # Python services (encoding, metadata, analysis, recognition) build inside Docker
  JAVA_SERVICES=""
  for svc in $SERVICES; do
    case "$svc" in
      user-service|storage-service|trip-service|processing-service|api-gateway|eureka)
        JAVA_SERVICES="$JAVA_SERVICES $svc"
        ;;
    esac
  done
  # Trim leading space
  JAVA_SERVICES=$(echo "$JAVA_SERVICES" | xargs)

  echo "=========================================="
  echo " Swapping services: $SERVICES"
  if [ -n "$JAVA_SERVICES" ]; then
    echo " Java services detected: $JAVA_SERVICES"
  fi
  echo "=========================================="

  # Step 1: Maven build for Java services (common-lib + each service module)
  if [ -n "$JAVA_SERVICES" ]; then
    # Map docker compose service names to Maven module names
    MVN_MODULES=""
    for svc in $JAVA_SERVICES; do
      case "$svc" in
        eureka) MVN_MODULES="$MVN_MODULES,discovery-service" ;;
        *)      MVN_MODULES="$MVN_MODULES,$svc" ;;
      esac
    done
    # Remove leading comma
    MVN_MODULES="${MVN_MODULES#,}"

    echo ""
    echo "[1/4] Building Maven modules: common-lib, $MVN_MODULES"
    mvn clean compile install -DskipTests -pl "common-lib,$MVN_MODULES" -am
    if [ $? -ne 0 ]; then
      echo "Error: Maven build failed. Aborting swap."
      exit 1
    fi
  else
    echo ""
    echo "[1/4] No Java services — skipping Maven build."
  fi

  # Step 2: Rebuild Docker images for the specified services
  echo ""
  echo "[2/4] Building Docker images for: $SERVICES"
  docker compose build $SERVICES
  if [ $? -ne 0 ]; then
    echo "Error: Docker build failed. Aborting swap."
    exit 1
  fi

  # Stop only the specified services
  echo ""
  echo "[3/4] Stopping old containers: $SERVICES"
  docker compose stop $SERVICES
  docker compose rm -f $SERVICES

  # Start the new containers
  echo ""
  echo "[4/4] Starting new containers: $SERVICES"
  docker compose up -d $SERVICES

  echo ""
  echo "=========================================="
  echo " Swap complete! Showing logs..."
  echo "=========================================="
  docker compose logs -f $SERVICES
  exit 0
fi

# Defaults for full/infra modes
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
    echo "$USAGE"
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
  echo "$USAGE"
  exit 1
fi

# Validate build option
if [[ "$BUILD_OPTION" != "build" && "$BUILD_OPTION" != "nobuild" ]]; then
  echo "Invalid build option: $BUILD_OPTION"
  echo "$USAGE"
  exit 1
fi

# Validate log option
if [[ "$LOG_OPTION" != "logs" && "$LOG_OPTION" != "nologs" ]]; then
  echo "Invalid logs option: $LOG_OPTION"
  echo "$USAGE"
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

  # All databases
  SERVICES="keycloak-database user-service-database storage-service-database"
  SERVICES="$SERVICES encoding-service-database processing-service-database metadata-service-database"
  SERVICES="$SERVICES analysis-service-database trip-service-database recognition-service-database"
  # Platform services
  SERVICES="$SERVICES keycloak minio zookeeper kafka redis qdrant"
  # Service mesh (eureka + gateway)
  SERVICES="$SERVICES eureka api-gateway"
  # Observability
  SERVICES="$SERVICES prometheus grafana loki promtail tempo"

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
