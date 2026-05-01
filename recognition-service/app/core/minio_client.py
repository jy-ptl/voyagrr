from minio import Minio
from app.core.config import get_config

cfg = get_config()

client = Minio(
    cfg["minio"]["endpoint"],
    access_key=cfg["minio"]["access-key"],
    secret_key=cfg["minio"]["secret-key"],
    secure=cfg["minio"]["secure"],
)


def list_objects(prefix: str, extensions: list[str] = []):
    objects = client.list_objects(cfg["minio"]["bucket"], prefix, recursive=True)
    if extensions:
        return [
            obj.object_name
            for obj in objects
            if any(obj.object_name.lower().endswith(ext) for ext in extensions)
        ]
    return [obj.object_name for obj in objects]


def get_object_bytes(object_name: str):
    response = client.get_object(cfg["minio"]["bucket"], object_name)
    return response.read()
