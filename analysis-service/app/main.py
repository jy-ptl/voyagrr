from app.core.logging_config import setup_logging, get_logger
from app.core.tracing import init_tracing
from app.consumer.analysis_consumer import start_consumer

setup_logging()
init_tracing()
logger = get_logger(__name__)

if __name__ == "__main__":
    logger.info("starting analysis-service...")
    start_consumer()
