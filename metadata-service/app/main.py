from app.core.logging_config import setup_logging, get_logger
from app.core.tracing import init_tracing
from app.consumer.metadata_consumer import start_consumer

setup_logging()
init_tracing(service_name="metadata-service", endpoint="http://tempo:4318/v1/traces")
logger = get_logger(__name__)

if __name__ == "__main__":
    logger.info("starting metadata-service...")
    start_consumer()
