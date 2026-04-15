from collections import defaultdict
from app.core.minio_client import list_objects, get_object_bytes
from app.service.face_engine import extract_faces
from app.service.matcher import average_embedding
from app.core.kafka_client import get_producer
from app.core.logging_config import get_logger
from app.service.faiss_service import build_faiss_index, match_face_faiss

logger = get_logger(__name__)
producer = get_producer()


def process_event(event):
    trip_id = event["tripId"]
    trip_dir = event["tripDirectory"]
    group_members = event["groupMembers"]

    user_embeddings = {}

    for member in group_members:
        user_id = member["keycloakUserId"]
        face_dir = member["sampleDirectory"]

        face_images = list_objects(face_dir)
        embeddings = []

        for img_path in face_images:
            img_bytes = get_object_bytes(img_path)
            faces = extract_faces(img_bytes)

            for f in faces:
                embeddings.append(f["embedding"])

        if embeddings:
            user_embeddings[user_id] = average_embedding(embeddings)

    index, user_ids = build_faiss_index(user_embeddings)

    trip_images = list_objects(trip_dir)

    for img_path in trip_images:
        process_image(trip_id, img_path, index, user_ids)


def process_image(trip_id, img_path, index, user_ids):

    img_bytes = get_object_bytes(img_path)
    faces = extract_faces(img_bytes)

    votes = defaultdict(list)

    for face in faces:
        user_id, confidence = match_face_faiss(face["embedding"], index, user_ids)

        if user_id:
            votes[user_id].append(confidence)

    final_user = None
    final_confidence = None

    if votes:
        best_user = max(votes.items(), key=lambda x: len(x[1]))
        final_user = best_user[0]
        final_confidence = float(sum(best_user[1]) / len(best_user[1]))

    result_faces = []

    for face in faces:
        result_faces.append(
            {
                "userId": final_user,
                "confidence": final_confidence,
                "bbox": face["bbox"],
            }
        )

    result = {
        "tripId": trip_id,
        "imagePath": img_path,
        "faces": result_faces,
    }

    logger.info("%s", result)

    producer.send("trip.analyzed.v1", result)
