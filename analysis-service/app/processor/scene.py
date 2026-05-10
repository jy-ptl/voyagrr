from app.processor.clip import clip_tags


def detect_scene(image_path):
    """
    Detects the most likely scene from the tag vocabulary using CLIP.
    """
    results = clip_tags(image_path, top_k=1)

    if results:
        return results[0]["tag"]

    return "unknown"
