from app.core.config import get_config
from app.core.logging_config import get_logger
from app.core.kafka_client import create_consumer
from app.service.recognition_service import process_trip_event
from app.service.embedding_generator import generate_and_store_embedding

logger = get_logger(__name__)
cfg = get_config()


def start_consumer():

    consumer = create_consumer()

    for msg in consumer:
        topic = msg.topic
        event = msg.value
        try:
            if topic == cfg["kafka"]["topic_analyze"]:
                process_trip_event(event)

            elif topic == cfg["kafka"]["topic_embedding"]:
                generate_and_store_embedding(
                    user_id=event["keycloakUserId"], sample_dir=event["sampleDirectory"]
                )

        except Exception as e:
            logger.exception("processing failed: %s", e)
