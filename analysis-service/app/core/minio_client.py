from minio import Minio
from app.core.config import get_config

cfg = get_config()

client = Minio(
    cfg["minio"]["endpoint"],
    access_key=cfg["minio"]["access-key"],
    secret_key=cfg["minio"]["secret-key"],
    secure=cfg["minio"]["secure"],
)


def download_file(bucket: str, object_key: str, local_path: str):
    client.fget_object(bucket, object_key, local_path)
