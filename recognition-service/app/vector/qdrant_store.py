import uuid
import numpy as np
from typing import List
from concurrent.futures import ThreadPoolExecutor
from qdrant_client import QdrantClient
from qdrant_client.models import VectorParams, Distance
from qdrant_client.models import Filter, FieldCondition, MatchValue
from app.core.config import get_config
from app.core.logging_config import get_logger

logger = get_logger(__name__)

cfg = get_config()

THRESHOLD = cfg["app"]["match_threshold"]
MAX_WORKERS = cfg["app"].get("max_workers", 4)


class QdrantVectorStore:
    def __init__(self):
        self.collection_name = "user_embeddings"
        self.client = QdrantClient(host="qdrant", port=6333)

    def _normalize(self, v):
        v = np.array(v, dtype=np.float32)
        norm = np.linalg.norm(v)
        return (v / norm).tolist() if norm > 0 else v.tolist()

    def init_collection(self):
        collections = self.client.get_collections().collections
        exists = any(c.name == self.collection_name for c in collections)

        if not exists:
            self.client.create_collection(
                collection_name=self.collection_name,
                vectors_config=VectorParams(size=512, distance=Distance.COSINE),
            )

    def upsert_user_embedding(self, user_id: str, embedding: List[float]):
        vector = self._normalize(embedding)
        point_id = str(uuid.uuid4())
        self.client.upsert(
            collection_name=self.collection_name,
            points=[{"id": point_id, "vector": vector, "payload": {"userId": user_id}}],
        )

    def search(self, embedding, top_k=5):
        vector = self._normalize(embedding)

        results = self.client.query_points(
            collection_name=self.collection_name,
            query=vector,
            limit=top_k,
            with_payload=True,
        )

        matches = []

        for point in results.points:
            if point.payload is None:
                continue

            user_id = point.payload.get("userId")
            if not user_id:
                continue

            if point.score >= THRESHOLD:
                matches.append((user_id, float(point.score)))

        return matches

    def search_batch(self, embeddings, top_k: int = 5):
        logger.info(
            "Searching %d face embeddings (workers=%d)", len(embeddings), MAX_WORKERS
        )

        def _search_one(emb):
            return self.search(emb, top_k=top_k)

        with ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
            all_matches = list(executor.map(_search_one, embeddings))

        matched_count = sum(1 for m in all_matches if m)
        logger.info(
            "Search complete: %d/%d faces had matches above threshold",
            matched_count,
            len(embeddings),
        )

        return all_matches

    def delete_user(self, user_id: str):

        self.client.delete(
            collection_name=self.collection_name,
            points_selector=Filter(
                must=[
                    FieldCondition(
                        key="userId",
                        match=MatchValue(value=user_id),
                    )
                ]
            ),
        )

    def get_count(self):
        try:
            return self.client.count(collection_name=self.collection_name).count
        except Exception:
            return 0

