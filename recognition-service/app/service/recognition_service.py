from app.core.minio_client import list_objects, get_object_bytes
from app.service.face_engine import extract_faces
from app.vector.factory import get_vector_store
from app.core.kafka_client import get_producer
from app.core.logging_config import get_logger

logger = get_logger(__name__)
vector_store = get_vector_store()


def process_trip_event(event):
    trip_id = event["tripId"]
    trip_dir = event["tripDirectory"]
    group_members = event["groupMembers"]

    allowed_users = set(member["keycloakUserId"] for member in group_members)

    producer = get_producer()
    trip_images = list_objects(trip_dir)

    for img_path in trip_images:
        process_image(trip_id, img_path, allowed_users, producer)

    logger.info("Completed trip %s", trip_id)


def process_image(trip_id, img_path, allowed_users, producer):
    try:
        img_bytes = get_object_bytes(img_path)
        faces = extract_faces(img_bytes)

        if not faces:
            result = {"tripId": trip_id, "imagePath": img_path, "faces": []}
            producer.send("trip.analyzed.v1", result)
            return

        embeddings = [face["embedding"] for face in faces]

        batch_results = vector_store.search_batch(embeddings, top_k=5)

        result_faces = []

        for face, matches in zip(faces, batch_results):
            user_id = None
            confidence = None

            if matches:
                best_per_user = {}

                for uid, score in matches:
                    if uid not in best_per_user or score > best_per_user[uid]:
                        best_per_user[uid] = score

                sorted_users = sorted(best_per_user.items(), key=lambda x: -x[1])

                for uid, score in sorted_users:
                    if uid in allowed_users:
                        user_id = uid
                        confidence = score
                        break

            result_faces.append(
                {"userId": user_id, "confidence": confidence, "bbox": face["bbox"]}
            )

        result = {"tripId": trip_id, "imagePath": img_path, "faces": result_faces}

        logger.info("result %s", result)
        producer.send("trip.analyzed.v1", result)

    except Exception as e:
        logger.error("Error processing %s: %s", img_path, str(e))
