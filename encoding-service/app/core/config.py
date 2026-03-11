import os
import yaml
from functools import lru_cache

ENV = os.getenv("ENV", "local")


@lru_cache
def get_config():
    path = f"/app/config/{ENV}.yml"

    with open(path) as f:
        return yaml.safe_load(f)
