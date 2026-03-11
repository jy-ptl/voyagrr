from app.core.tag_loader import load_tags
from transformers import CLIPProcessor, CLIPModel
from PIL import Image
import torch

model = CLIPModel.from_pretrained("openai/clip-vit-base-patch32")
processor = CLIPProcessor.from_pretrained("openai/clip-vit-base-patch32")

TAGS = load_tags()


def clip_tags(image_path):

    image = Image.open(image_path).convert("RGB")
    inputs = processor(text=TAGS, images=image, return_tensors="pt", padding=True)
    outputs = model(**inputs)
    probs = outputs.logits_per_image.softmax(dim=1)

    results = []

    for i, tag in enumerate(TAGS):
        results.append({"tag": tag, "confidence": float(probs[0][i])})

    results.sort(key=lambda x: x["confidence"], reverse=True)

    return results
