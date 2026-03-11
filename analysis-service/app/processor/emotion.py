from fer import FER
import cv2

detector = FER(mtcnn=True)


def detect_emotion(image_path):

    img = cv2.imread(image_path)
    results = detector.detect_emotions(img)

    emotions = []

    for r in results:
        emotion = max(r["emotions"], key=r["emotions"].get)

        emotions.append(
            {"emotion": emotion, "confidence": float(r["emotions"][emotion])}
        )

    return emotions
