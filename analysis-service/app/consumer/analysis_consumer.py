import json
from opentelemetry.trace import SpanKind
from opentelemetry import context as otel_context
from app.core.config import get_config
from app.core.logging_config import get_logger
from app.core.kafka_client import create_consumer
from app.core.tracing import get_tracer, extract_context_from_headers
from app.service.analysis_service import process_event

logger = get_logger(__name__)
cfg = get_config()
tracer = get_tracer("analysis-service")


def start_consumer():
    consumer = create_consumer()

    for msg in consumer:
        parent_ctx = extract_context_from_headers(msg.headers or [])
        token = otel_context.attach(parent_ctx)
        try:
            with tracer.start_as_current_span(
                "analysis-service receive",
                kind=SpanKind.CONSUMER,
                attributes={
                    "messaging.system": "kafka",
                    "messaging.destination": msg.topic,
                    "messaging.operation": "receive",
                },
            ):
                process_event(msg.value)
                logger.info("processed: %s", msg.value.get("minioObjectKey"))
        except Exception as e:
            logger.exception("processing failed: %s", e)
        finally:
            otel_context.detach(token)
