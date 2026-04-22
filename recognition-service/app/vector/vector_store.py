from abc import ABC, abstractmethod
from typing import List, Tuple


class VectorStore(ABC):
    @abstractmethod
    def init_collection(self):
        pass

    @abstractmethod
    def upsert_user_embedding(self, user_id: str, embedding: List[float]):
        pass

    @abstractmethod
    def search(self, embedding: List[float], top_k: int = 5) -> List[Tuple[str, float]]:
        pass

    @abstractmethod
    def search_batch(self, embeddings: List[float], top_k: int = 5):
        pass

    @abstractmethod
    def delete_user(self, user_id: str):
        pass
