from fer import FER
import cv2

detector = FER(mtcnn=True)


def detect_emotion(image_path):
    try:
        img = cv2.imread(image_path)
        if img is None:
            return []

        results = detector.detect_emotions(img)

        emotions = []

        for r in results:
            if not r.get("emotions"):
                continue
            emotion = max(r["emotions"], key=r["emotions"].get)

            emotions.append(
                {"emotion": emotion, "confidence": float(r["emotions"][emotion])}
            )

        return emotions
    except Exception as e:
        print(f"Error in emotion detection: {e}")
        return []
