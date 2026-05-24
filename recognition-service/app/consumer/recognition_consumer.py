from opentelemetry.trace import SpanKind
from app.core.config import get_config
from app.core.logging_config import get_logger
from app.core.kafka_client import create_consumer
from app.core.tracing import get_tracer, extract_context_from_message
from app.service.recognition_service import process_trip_event
from app.service.embedding_generator import generate_and_store_embedding

logger = get_logger(__name__)
cfg = get_config()
tracer = get_tracer(__name__)


def start_consumer():

    consumer = create_consumer()

    for msg in consumer:
        topic = msg.topic
        event = msg.value
        ctx = extract_context_from_message(msg)
        with tracer.start_as_current_span(
            f"recognition-service process {topic}",
            context=ctx,
            kind=SpanKind.CONSUMER,
            attributes={"messaging.system": "kafka", "messaging.destination": topic},
        ):
            try:
                if topic == cfg["kafka"]["topic_analyze"]:
                    process_trip_event(event)

                elif topic == cfg["kafka"]["topic_embedding"]:
                    generate_and_store_embedding(
                        user_id=event["keycloakUserId"], sample_dir=event["sampleDirectory"]
                    )

            except Exception as e:
                logger.exception("processing failed: %s", e)
