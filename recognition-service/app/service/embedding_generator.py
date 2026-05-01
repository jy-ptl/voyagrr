from app.core.minio_client import list_objects, get_object_bytes
from app.service.face_engine import extract_faces
from app.vector.factory import get_vector_store
from app.core.logging_config import get_logger

logger = get_logger(__name__)
vector_store = get_vector_store()


def generate_and_store_embedding(user_id, sample_dir):
    logger.info(
        "Generating embeddings for user %s from directory %s", user_id, sample_dir
    )
    face_images = list_objects(
        sample_dir, extensions=[".jpg", ".jpeg", ".png", ".webp"]
    )
    logger.info("Found %d images in sample directory", len(face_images))

    embeddings = []

    for img_path in face_images:
        img_bytes = get_object_bytes(img_path)
        faces = extract_faces(img_bytes)

        if len(faces) == 1:
            logger.info("Found 1 face in %s", img_path)
            embeddings.append(faces[0]["embedding"])
        elif len(faces) > 1:
            logger.warning(
                "Multiple faces (%d) found in sample image %s, skipping to avoid pollution",
                len(faces),
                img_path,
            )
        else:
            logger.warning("No faces found in sample image %s", img_path)

    if not embeddings:
        logger.error(
            "No valid single-face embeddings found for user %s. Database will be empty for this user!",
            user_id,
        )
        return

    logger.info("Storing %d embeddings for user %s in Qdrant", len(embeddings), user_id)
    for emb in embeddings:
        vector_store.upsert_user_embedding(user_id=user_id, embedding=emb)
    logger.info("Successfully updated embeddings for user %s", user_id)
