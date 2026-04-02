import os
import uuid
from app.core.config import get_config
from app.core.minio_client import download_file
from app.core.logging_config import get_logger
from app.service.analysis_pipeline import analyze_image
from app.core.kafka_client import get_producer

logger = get_logger(__name__)
cfg = get_config()

TEMP_DIR = cfg["app"]["temp_dir"]
os.makedirs(TEMP_DIR, exist_ok=True)
bucket = cfg["minio"]["bucket"]


def process_event(event):

    local_path = os.path.join(TEMP_DIR, f"{uuid.uuid4()}.jpg")
    try:
        file_id = event["fileId"]
        object_key = event["minioObjectKey"]

        logger.info("downloading image %s", object_key)
        download_file(bucket, object_key, local_path)

        logger.info("analyzing scene")
        result = analyze_image(local_path)
        result_event = {
            "fileId": file_id,
            "minioObjectKey": object_key,
            "result": result,
        }

        producer = get_producer()
        producer.send("file.analyzed.v1", result_event)

        logger.info("result %s", result_event)
        logger.info("analysis completed for file %s", file_id)

    except Exception as e:
        logger.exception("analysis failed for file %s", event.get("fileId"))

    finally:
        if "local_path" in locals() and os.path.exists(local_path):
            os.remove(local_path)
