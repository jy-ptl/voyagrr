from transformers import pipeline, AutoModelForImageClassification, AutoImageProcessor

MODEL_NAME = "google/mobilenet_v2_1.0_224"
MODEL_DIR = "/models/mobilenet"

processor = AutoImageProcessor.from_pretrained(MODEL_NAME, cache_dir=MODEL_DIR)
model = AutoModelForImageClassification.from_pretrained(MODEL_NAME, cache_dir=MODEL_DIR)

classifier = pipeline("image-classification", model=model, image_processor=processor)


def analyze_scene(image_path: str):
    results = classifier(image_path)
    top = results[0]

    return {
        "sceneTag": top["label"],
        "sceneConfidence": float(top["score"]),
    }
