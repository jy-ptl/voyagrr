from app.processor.emotion import detect_emotion
from app.processor.yolo import detect_objects
from app.processor.clip import clip_tags
from app.processor.scene import detect_scene


def analyze_image(image_path):

    objects = detect_objects(image_path)
    scene = detect_scene(image_path)
    tags = clip_tags(image_path)
    emotions = detect_emotion(image_path)

    return {
        "scene": scene,
        "objects": objects,
        "tags": tags[:5],
        "emotions": emotions,
    }
