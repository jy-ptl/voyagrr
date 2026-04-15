import faiss
import numpy as np
from app.core.config import get_config

cfg = get_config()


THRESHOLD = cfg["app"]["match_threshold"]


def build_faiss_index(user_embeddings):
    user_ids = list(user_embeddings.keys())
    embeddings = np.array(list(user_embeddings.values())).astype("float32")

    faiss.normalize_L2(embeddings)

    index = faiss.IndexFlatIP(embeddings.shape[1])
    index.add(embeddings)

    return index, user_ids


def match_face_faiss(face_embedding, index, user_ids, threshold=THRESHOLD):
    face_embedding = np.array([face_embedding]).astype("float32")
    faiss.normalize_L2(face_embedding)

    scores, indices = index.search(face_embedding, k=1)

    best_score = scores[0][0]
    best_idx = indices[0][0]

    if best_score >= threshold:
        return user_ids[best_idx], float(best_score)

    return None, None
