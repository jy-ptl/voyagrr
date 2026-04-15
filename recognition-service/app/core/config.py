import os
import yaml
from functools import lru_cache

ENV = os.getenv("ENV", "local")


@lru_cache
def get_config():
    path = f"/app/config/{ENV}.yml"

    with open(path) as f:
        content = f.read()
        expanded_content = os.path.expandvars(content)
        return yaml.safe_load(expanded_content)
