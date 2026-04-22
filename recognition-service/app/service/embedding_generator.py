from app.core.minio_client import list_objects, get_object_bytes
from app.service.face_engine import extract_faces
from app.vector.factory import get_vector_store
from app.core.logging_config import get_logger

logger = get_logger(__name__)
vector_store = get_vector_store()


def generate_and_store_embedding(user_id, sample_dir):
    face_images = list_objects(sample_dir)

    embeddings = []

    for img_path in face_images:
        img_bytes = get_object_bytes(img_path)
        faces = extract_faces(img_bytes)

        for f in faces:
            embeddings.append(f["embedding"])

    if not embeddings:
        logger.warning("No faces found for user %s", user_id)
        return

    for emb in embeddings:
        vector_store.upsert_user_embedding(user_id=user_id, embedding=emb)
