from opentelemetry.trace import SpanKind
from app.core.config import get_config
from app.core.logging_config import get_logger
from app.core.kafka_client import create_consumer
from app.core.tracing import get_tracer, extract_context_from_message
from app.service.analysis_service import process_event

logger = get_logger(__name__)
cfg = get_config()
tracer = get_tracer(__name__)


def start_consumer():

    consumer = create_consumer()

    for msg in consumer:
        ctx = extract_context_from_message(msg)
        with tracer.start_as_current_span(
            "analysis-service process",
            context=ctx,
            kind=SpanKind.CONSUMER,
            attributes={"messaging.system": "kafka", "messaging.destination": msg.topic},
        ):
            try:
                process_event(msg.value)
                logger.info("processed: %s", msg.value["minioObjectKey"])

            except Exception as e:
                logger.exception("processing failed: %s", e)
