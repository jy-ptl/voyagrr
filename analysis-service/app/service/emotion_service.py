from fer import FER
import cv2

# Load once
emotion_detector = FER(mtcnn=True)


def analyze_emotion(image_path: str):
    img = cv2.imread(image_path)

    results = emotion_detector.detect_emotions(img)

    if not results:
        return {
            "dominantEmotion": "neutral",
            "emotionConfidence": 0.0,
        }

    emotions = results[0]["emotions"]

    dominant = max(emotions, key=emotions.get)

    return {
        "dominantEmotion": dominant,
        "emotionConfidence": float(emotions[dominant]),
    }
