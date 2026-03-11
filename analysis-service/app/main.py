from app.core.logging_config import setup_logging, get_logger
from app.consumer.analysis_consumer import start_consumer

setup_logging()
logger = get_logger(__name__)

if __name__ == "__main__":
    logger.info("starting analysis-service...")
    start_consumer()
