import time
from kafka import KafkaConsumer
from kafka.errors import NoBrokersAvailable
import json
from app.config import get_config

cfg = get_config()

def create_consumer():
    while True:
        try:
            print("Connecting to Kafka...")
            consumer = KafkaConsumer(
                cfg["kafka"]["topic_uploaded"],
                bootstrap_servers=cfg["kafka"]["bootstrap_servers"],
                group_id="metadata-workers",
                value_deserializer=lambda m: json.loads(m.decode()),
                auto_offset_reset="earliest"
            )
            print("Connected to Kafka ")
            return consumer
        except NoBrokersAvailable:
            print("Kafka not ready, retrying in 5 seconds...")
            time.sleep(5)

consumer = create_consumer()

