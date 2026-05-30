import logging
from opentelemetry import trace, context, propagate
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor, ConsoleSpanExporter
from opentelemetry.exporter.otlp.proto.http.trace_exporter import OTLPSpanExporter
from opentelemetry.sdk.resources import Resource, SERVICE_NAME
from opentelemetry.propagators.composite import CompositePropagator
from opentelemetry.trace.propagation.tracecontext import TraceContextTextMapPropagator

logger = logging.getLogger(__name__)

_propagator = TraceContextTextMapPropagator()


def init_tracing(service_name: str, endpoint: str = "http://tempo:4318/v1/traces"):
    """Initialize OpenTelemetry tracing. Call once at startup."""
    resource = Resource.create({SERVICE_NAME: service_name})
    provider = TracerProvider(resource=resource)

    otlp_exporter = OTLPSpanExporter(endpoint=endpoint)
    provider.add_span_processor(BatchSpanProcessor(otlp_exporter))

    # Set as global provider
    trace.set_tracer_provider(provider)

    # Ensure W3C TraceContext propagation is globally configured
    propagate.set_global_textmap(CompositePropagator([TraceContextTextMapPropagator()]))

    logger.info("OTel tracing initialized: service=%s endpoint=%s", service_name, endpoint)


def get_tracer(name: str = __name__):
    return trace.get_tracer(name)


def extract_context_from_headers(headers):
    """
    Extract W3C trace context from kafka-python message headers.
    headers is a list of (key_str_or_bytes, value_bytes) tuples.
    Returns an OTel Context object.
    """
    if not headers:
        return context.get_current()

    carrier = {}
    for key, value in headers:
        k = key.decode("utf-8") if isinstance(key, bytes) else key
        v = value.decode("utf-8") if isinstance(value, bytes) else str(value)
        carrier[k] = v

    return _propagator.extract(carrier=carrier)


def inject_trace_headers():
    """
    Inject current span context into Kafka message headers.
    Returns a list of (key_bytes, value_bytes) tuples for kafka-python.
    """
    carrier = {}
    _propagator.inject(carrier=carrier)
    return [(k.encode("utf-8"), v.encode("utf-8")) for k, v in carrier.items()]
