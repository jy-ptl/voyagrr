import os
import uuid
import shutil
from app.core.config import get_config
from app.core.minio_client import download_file, upload_file
from app.core.encoder import encode_to_hls
from app.core.logging_config import get_logger
from app.producer.video_processed_producer import get_producer

cfg = get_config()
logger = get_logger(__name__)
bucket = cfg["minio"]["bucket"]


def process_event(event):
    file_id = event["fileId"]
    object_key = event["minioObjectKey"]

    temp_dir = cfg.get("app", {}).get("temp_dir", "/tmp/encoding")
    os.makedirs(temp_dir, exist_ok=True)

    local_input = os.path.join(temp_dir, f"{uuid.uuid4()}.mp4")
    local_output = os.path.join(temp_dir, file_id)

    try:
        logger.info("downloading: %s", object_key)
        download_file(bucket, object_key, local_input)

        logger.info("encoding to HLS")
        encode_to_hls(local_input, local_output)

        logger.info("uploading HLS segments")
        for file in os.listdir(local_output):
            upload_file(
                "processed-videos",
                f"{file_id}/{file}",
                os.path.join(local_output, file),
            )

        logger.info("encoding completed for fileId=%s", file_id)
        encoded_event = {
            "fileId": file_id,
            "minioObjectKey": object_key,
        }

        producer = get_producer()
        producer.send(
            "file.encoded.v1",
            encoded_event,
        )
        producer.flush()
        logger.info("published metadata event for %s", object_key)

    except Exception as e:
        logger.exception("encoding failed for fileId=%s", file_id)
        raise e

    finally:
        try:
            if os.path.exists(local_input):
                os.remove(local_input)

            if os.path.exists(local_output):
                shutil.rmtree(local_output)

        except Exception:
            logger.warning("cleanup failed for fileId=%s", file_id)
