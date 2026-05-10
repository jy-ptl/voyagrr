import os
from app.processor.emotion import detect_emotion
from app.processor.yolo import detect_objects
from app.processor.clip import clip_tags
from app.core.image_utils import resize_image_if_needed


def analyze_image(image_path):
    temp_dir = os.path.dirname(image_path)
    # Resize image if too large to save memory
    proc_path = resize_image_if_needed(image_path, temp_dir, max_dim=1024)

    try:
        # Object detection
        objects = detect_objects(proc_path, confidence_threshold=0.3)

        # CLIP processing (called ONCE for both tags and scene)
        clip_results = clip_tags(proc_path, top_k=5)

        # Extract scene from top CLIP tag
        scene = clip_results[0]["tag"] if clip_results else "unknown"

        # Emotion detection
        emotions = detect_emotion(proc_path)

        return {
            "scene": scene,
            "objects": objects,
            "tags": clip_results,
            "emotions": emotions,
        }
    finally:
        # Clean up resized image if it was created
        if proc_path != image_path and os.path.exists(proc_path):
            os.remove(proc_path)
