from app.core.logging_config import setup_logging, get_logger
from app.core.tracing import init_tracing
from app.consumer.encoding_consumer import start_consumer

setup_logging()
init_tracing()
logger = get_logger(__name__)

if __name__ == "__main__":
    logger.info("starting encoding-service...")
    start_consumer()
