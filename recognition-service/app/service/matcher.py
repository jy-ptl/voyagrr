import numpy as np
from app.core.config import get_config

cfg = get_config()

THRESHOLD = cfg["app"]["match_threshold"]


def average_embedding(embeddings):
    if not embeddings:
        return None

    embeddings = np.array(embeddings, dtype=np.float32)
    avg_embedding = np.mean(embeddings, axis=0)
    norm = np.linalg.norm(avg_embedding)
    if norm == 0:
        return avg_embedding

    return avg_embedding / norm


def cosine_similarity(a, b):
    return np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b))
