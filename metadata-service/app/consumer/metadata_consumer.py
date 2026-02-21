import time
import json
from kafka import KafkaConsumer
from kafka.errors import NoBrokersAvailable
from app.core.config import get_config
from app.core.logging_config import get_logger
from app.service.metadata_service import process_event

logger = get_logger(__name__)
cfg = get_config()


def create_consumer():
    while True:
        try:
            consumer = KafkaConsumer(
                cfg["kafka"]["topic_uploaded"],
                bootstrap_servers=cfg["kafka"]["bootstrap_servers"],
                group_id="metadata-workers",
                value_deserializer=lambda m: json.loads(m.decode()),
                auto_offset_reset="earliest",
            )
            logger.info("connected to kafka ")
            return consumer
        except NoBrokersAvailable:
            logger.info("kafka not ready, retrying in 5 seconds...")
            time.sleep(5)


def start_consumer():

    consumer = create_consumer()

    for msg in consumer:
        try:
            process_event(msg.value)
            logger.info("processed: %s", msg.value["objectKey"])
        except Exception as e:
            logger.exception("processing failed : %s", e)
