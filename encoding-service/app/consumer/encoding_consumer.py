import time
import json
from concurrent.futures import ThreadPoolExecutor
from kafka import KafkaConsumer
from kafka.errors import NoBrokersAvailable
from opentelemetry.trace import SpanKind
from opentelemetry import context as otel_context
from app.core.config import get_config
from app.core.logging_config import get_logger
from app.core.tracing import get_tracer, extract_context_from_headers
from app.service.encoding_service import process_event

logger = get_logger(__name__)
cfg = get_config()
tracer = get_tracer("encoding-service")

executor = ThreadPoolExecutor(max_workers=2)


def create_consumer():
    while True:
        try:
            consumer = KafkaConsumer(
                cfg["kafka"]["topic_encoding"],
                bootstrap_servers=cfg["kafka"]["bootstrap_servers"],
                group_id="encoding-workers",
                value_deserializer=lambda m: json.loads(m.decode()),
                auto_offset_reset="earliest",
                enable_auto_commit=True,
                max_poll_interval_ms=900000,
                session_timeout_ms=30000,
                request_timeout_ms=120000,
            )
            logger.info("connected to kafka")
            return consumer
        except NoBrokersAvailable:
            logger.info("kafka not ready, retrying in 5 seconds...")
            time.sleep(5)


def handle_message(msg):
    parent_ctx = extract_context_from_headers(msg.headers or [])
    token = otel_context.attach(parent_ctx)
    try:
        with tracer.start_as_current_span(
            "encoding-service receive",
            kind=SpanKind.CONSUMER,
            attributes={
                "messaging.system": "kafka",
                "messaging.destination": msg.topic,
                "messaging.operation": "receive",
            },
        ):
            process_event(msg.value)
            logger.info("encoding complete: %s", msg.value.get("minioObjectKey"))
    except Exception as e:
        logger.exception("processing failed: %s", e)
    finally:
        otel_context.detach(token)


def start_consumer():
    consumer = create_consumer()

    while True:
        records = consumer.poll(timeout_ms=1000)
        for tp, messages in records.items():
            for msg in messages:
                executor.submit(handle_message, msg)
