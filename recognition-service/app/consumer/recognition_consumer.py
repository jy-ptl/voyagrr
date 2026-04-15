from app.core.config import get_config
from app.core.logging_config import get_logger
from app.core.kafka_client import create_consumer
from app.service.recognition_service import process_event

logger = get_logger(__name__)
cfg = get_config()


def start_consumer():

    consumer = create_consumer()

    for msg in consumer:
        try:
            process_event(msg.value)
            logger.info("processed: %s", msg.value["tripId"])

        except Exception as e:
            logger.exception("processing failed: %s", e)
