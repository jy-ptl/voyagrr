from app.vector.qdrant_store import QdrantVectorStore

_vector_store = None


def get_vector_store():
    global _vector_store

    if _vector_store is None:
        _vector_store = QdrantVectorStore()
        _vector_store.init_collection()

    return _vector_store

