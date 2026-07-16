import os
import tempfile

from app.core.config import get_config
from app.core.minio_client import client, upload_file
from app.core.extractor import extract_metadata, generate_thumbnail
from app.repository.db import SessionLocal
from app.core.logging_config import get_logger
from app.producer.metadata_producer import get_producer
from app.core.tracing import inject_trace_headers

logger = get_logger(__name__)
cfg = get_config()
bucket = cfg["minio"]["bucket"]


def process_event(event):

    key = event["minioObjectKey"]
    fileId = event["fileId"]

    response = client.get_object(bucket, key)

    tmp_path = None
    thumb_path = None

    try:
        with tempfile.NamedTemporaryFile(delete=False) as tmp:
            for chunk in response.stream(32 * 1024):
                tmp.write(chunk)
            tmp_path = tmp.name

        meta = extract_metadata(tmp_path)

        logger.info("EXTRACTED META: %s", meta)

        thumbnail_key = None
        with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as thumb_tmp:
            thumb_path = thumb_tmp.name
        
        if generate_thumbnail(tmp_path, thumb_path):
            thumbnail_key = f"thumbnails/{fileId}.jpg"
            upload_file(bucket, thumbnail_key, thumb_path, content_type="image/jpeg")
            logger.info("Generated and uploaded thumbnail for file %s", fileId)

        metadata_event = {
            "fileId": fileId,
            "minioObjectKey": key,
            "metadata": meta,
            "thumbnailKey": thumbnail_key
        }

        producer = get_producer()

        producer.send(
            "file.metadata.v1",
            metadata_event,
            headers=inject_trace_headers(),
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
        
        if thumb_path and os.path.exists(thumb_path):
            os.remove(thumb_path)
