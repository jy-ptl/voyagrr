from app.core.minio_client import list_objects, get_object_bytes
from app.core.config import get_config
from app.service.face_engine import extract_faces
from app.vector.factory import get_vector_store
from app.core.kafka_client import get_producer
from app.core.logging_config import get_logger
from app.core.tracing import inject_trace_headers

logger = get_logger(__name__)
vector_store = get_vector_store()
cfg = get_config()

MIN_FACE_SIZE = cfg["app"].get("min_face_size", 80)


def _face_size(bbox):
    """Return the minimum dimension (width or height) of a bounding box."""
    return min(bbox["x2"] - bbox["x1"], bbox["y2"] - bbox["y1"])


def process_trip_event(event):
    trip_id = event["tripId"]
    trip_dir = event["tripDirectory"]
    group_members = event["groupMembers"]

    allowed_users = set(member["keycloakUserId"] for member in group_members)
    logger.info("Processing trip %s (%d allowed users)", trip_id, len(allowed_users))

    producer = get_producer()
    trip_images = list_objects(trip_dir, extensions=[".jpg", ".jpeg", ".png", ".webp"])
    logger.info("Found %d images in trip directory", len(trip_images))

    for img_path in trip_images:
        process_image(trip_id, img_path, allowed_users, producer)

    logger.info("Completed trip %s", trip_id)


def process_image(trip_id, img_path, allowed_users, producer):
    try:
        img_bytes = get_object_bytes(img_path)
        faces = extract_faces(img_bytes)

        if not faces:
            result = {"tripId": trip_id, "imagePath": img_path, "faces": []}
            producer.send("trip.analyzed.v1", result, headers=inject_trace_headers())
            return

        large_faces = []
        small_faces = []
        for face in faces:
            if _face_size(face["bbox"]) >= MIN_FACE_SIZE:
                large_faces.append(face)
            else:
                small_faces.append(face)

        if small_faces:
            logger.info(
                "Skipped %d tiny faces (< %dpx) in %s",
                len(small_faces),
                MIN_FACE_SIZE,
                img_path,
            )

        result_faces = []

        if large_faces:
            embeddings = [face["embedding"] for face in large_faces]
            batch_results = vector_store.search_batch(embeddings, top_k=5)

            for face, matches in zip(large_faces, batch_results):
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

        for face in small_faces:
            result_faces.append(
                {"userId": None, "confidence": None, "bbox": face["bbox"]}
            )

        result = {"tripId": trip_id, "imagePath": img_path, "faces": result_faces}

        matched = sum(1 for f in result_faces if f["userId"] is not None)
        logger.info(
            "%s: %d faces detected, %d recognized", img_path, len(faces), matched
        )

        producer.send("trip.analyzed.v1", result, headers=inject_trace_headers())

    except Exception as e:
        logger.error("Error processing %s: %s", img_path, str(e))
