from minio import Minio
from app.core.config import get_config

cfg = get_config()

client = Minio(
    cfg["minio"]["endpoint"],
    access_key=cfg["minio"]["access-key"],
    secret_key=cfg["minio"]["secret-key"],
    secure=cfg["minio"].get("secure", False),
)


def upload_file(bucket_name, object_name, file_path, content_type="application/octet-stream"):
    client.fput_object(bucket_name, object_name, file_path, content_type=content_type)
