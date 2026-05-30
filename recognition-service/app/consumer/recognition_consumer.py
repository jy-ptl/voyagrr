from opentelemetry.trace import SpanKind
from opentelemetry import context as otel_context
from app.core.config import get_config
from app.core.logging_config import get_logger
from app.core.kafka_client import create_consumer
from app.core.tracing import get_tracer, extract_context_from_headers
from app.service.recognition_service import process_trip_event
from app.service.embedding_generator import generate_and_store_embedding

logger = get_logger(__name__)
cfg = get_config()
tracer = get_tracer("recognition-service")


def start_consumer():
    consumer = create_consumer()

    for msg in consumer:
        topic = msg.topic
        event = msg.value
        parent_ctx = extract_context_from_headers(msg.headers or [])
        token = otel_context.attach(parent_ctx)
        try:
            with tracer.start_as_current_span(
                f"recognition-service receive",
                kind=SpanKind.CONSUMER,
                attributes={
                    "messaging.system": "kafka",
                    "messaging.destination": topic,
                    "messaging.operation": "receive",
                },
            ):
                if topic == cfg["kafka"]["topic_analyze"]:
                    process_trip_event(event)
                elif topic == cfg["kafka"]["topic_embedding"]:
                    generate_and_store_embedding(
                        user_id=event["keycloakUserId"],
                        sample_dir=event["sampleDirectory"],
                    )
        except Exception as e:
            logger.exception("processing failed: %s", e)
        finally:
            otel_context.detach(token)
