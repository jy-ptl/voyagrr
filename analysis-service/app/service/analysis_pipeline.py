from app.processor.emotion import detect_emotion
from app.processor.yolo import detect_objects
from app.processor.clip import clip_tags
from app.processor.scene import detect_scene


def analyze_image(image_path):

    # Object detection with confidence threshold
    objects = detect_objects(image_path, confidence_threshold=0.3)

    # Scene detection using CLIP
    scene = detect_scene(image_path)

    # General tags using CLIP (top 5)
    tags = clip_tags(image_path, top_k=5)

    # Emotion detection
    emotions = detect_emotion(image_path)

    return {
        "scene": scene,
        "objects": objects,
        "tags": tags,
        "emotions": emotions,
    }
