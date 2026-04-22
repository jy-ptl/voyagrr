import numpy as np
import cv2
from insightface.app import FaceAnalysis

app = FaceAnalysis(name="buffalo_l")
app.prepare(ctx_id=-1, det_size=(1024, 1024))


def extract_faces(img_bytes):
    np_arr = np.frombuffer(img_bytes, np.uint8)
    image = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    faces = app.get(image)

    results = []
    for face in faces:
        results.append(
            {
                "embedding": face.embedding.tolist(),
                "bbox": {
                    "x1": int(face.bbox[0]),
                    "y1": int(face.bbox[1]),
                    "x2": int(face.bbox[2]),
                    "y2": int(face.bbox[3]),
                },
                "confidence": float(face.det_score),
            }
        )

    return results

