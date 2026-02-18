import os
import yaml
from functools import lru_cache

ENV = os.getenv("ENV", "local")

@lru_cache
def get_config():
    base_path = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    path = os.path.join(base_path, "config", f"{ENV}.yml")

    print("Loading config from:", path)

    with open(path) as f:
        return yaml.safe_load(f)
