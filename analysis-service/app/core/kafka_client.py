import time
import json
from kafka import KafkaConsumer, KafkaProducer
from kafka.errors import NoBrokersAvailable
from app.core.config import get_config
from app.core.logging_config import get_logger

cfg = get_config()
logger = get_logger(__name__)


def create_consumer():
    while True:
        try:
            consumer = KafkaConsumer(
                cfg["kafka"]["topic_analyze"],
                bootstrap_servers=cfg["kafka"]["bootstrap_servers"],
                group_id="analysis-workers",
                value_deserializer=lambda m: json.loads(m.decode()),
                auto_offset_reset="earliest",
            )
            logger.info("connected to kafka ")
            return consumer
        except NoBrokersAvailable:
            logger.info("kafka not ready, retrying in 5 seconds...")
            time.sleep(5)


def create_producer():
    return KafkaProducer(
        bootstrap_servers=cfg["kafka"]["bootstrap_servers"],
        value_serializer=lambda v: json.dumps(v).encode(),
    )
