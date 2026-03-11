import json
import time
from kafka import KafkaProducer
from kafka.errors import NoBrokersAvailable
from app.core.config import get_config
from app.core.logging_config import get_logger

logger = get_logger(__name__)
cfg = get_config()

_producer = None


def get_producer():
    global _producer

    if _producer:
        return _producer

    while True:
        try:
            logger.info("connecting producer to kafka...")
            _producer = KafkaProducer(
                bootstrap_servers=cfg["kafka"]["bootstrap_servers"],
                value_serializer=lambda v: json.dumps(v).encode("utf-8"),
            )
            logger.info("producer connected to kafka")
            return _producer

        except NoBrokersAvailable:
            logger.info("kafka not ready for producer, retrying in 5 sec...")
            time.sleep(5)
