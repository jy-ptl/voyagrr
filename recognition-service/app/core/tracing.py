from opentelemetry import trace, context
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor
from opentelemetry.exporter.otlp.proto.http.trace_exporter import OTLPSpanExporter
from opentelemetry.sdk.resources import Resource
from opentelemetry.trace.propagation.tracecontext import TraceContextTextMapPropagator
from app.core.config import get_config

_propagator = TraceContextTextMapPropagator()


def init_tracing():
    cfg = get_config()
    tracing_cfg = cfg.get("tracing", {})
    service_name = tracing_cfg.get("service_name", "unknown-service")
    endpoint = tracing_cfg.get("endpoint", "http://tempo:4318/v1/traces")

    resource = Resource.create({"service.name": service_name})
    provider = TracerProvider(resource=resource)
    exporter = OTLPSpanExporter(endpoint=endpoint)
    provider.add_span_processor(BatchSpanProcessor(exporter))
    trace.set_tracer_provider(provider)


def get_tracer(name: str):
    return trace.get_tracer(name)


def extract_context_from_message(msg):
    """Extract W3C trace context from kafka-python message headers."""
    if not msg.headers:
        return context.get_current()

    carrier = {}
    for key, value in msg.headers:
        if isinstance(value, bytes):
            carrier[key] = value.decode("utf-8")
        else:
            carrier[key] = str(value)

    return _propagator.extract(carrier=carrier)


def inject_trace_headers():
    """Inject current trace context into Kafka message headers."""
    carrier = {}
    _propagator.inject(carrier=carrier)
    return [(k, v.encode("utf-8")) for k, v in carrier.items()]
