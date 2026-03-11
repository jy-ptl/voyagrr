from minio import Minio
from app.core.config import get_config

cfg = get_config()

client = Minio(
    cfg["minio"]["endpoint"],
    access_key=cfg["minio"]["access-key"],
    secret_key=cfg["minio"]["secret-key"],
    secure=cfg["minio"].get("secure", False),
)


def download_file(bucket, object_key, local_path):
    client.fget_object(bucket, object_key, local_path)


def upload_file(bucket, object_key, local_path):
    client.fput_object(bucket, object_key, local_path)
