import os
import tempfile

from app.core.config import get_config
from app.core.minio_client import client
from app.core.extractor import extract_metadata
from app.repository.db import SessionLocal
from app.core.logging_config import get_logger
from app.producer.metadata_producer import get_producer

logger = get_logger(__name__)
cfg = get_config()
bucket = cfg["minio"]["bucket"]


def process_event(event):

    key = event["minioObjectKey"]
    fileId = event["fileId"]

    response = client.get_object(bucket, key)

    tmp_path = None

    try:
        with tempfile.NamedTemporaryFile(delete=False) as tmp:
            for chunk in response.stream(32 * 1024):
                tmp.write(chunk)
            tmp_path = tmp.name

        meta = extract_metadata(tmp_path)

        logger.info("EXTRACTED META: %s", meta)

        metadata_event = {
            "fileId": fileId,
            "minioObjectKey": key,
            "metadata": meta,
        }

        producer = get_producer()

        producer.send(
            "file.metadata.v1",
            metadata_event,
        )

        producer.flush()

        logger.info("published metadata event for %s", key)

        session = SessionLocal()
        # store metadata
        session.commit()

    finally:
        response.close()
        response.release_conn()

        if tmp_path and os.path.exists(tmp_path):
            os.remove(tmp_path)
