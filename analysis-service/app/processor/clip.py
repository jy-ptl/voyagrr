from app.core.tag_loader import load_tags
from transformers import CLIPProcessor, CLIPModel
from PIL import Image
import torch

device = "cuda" if torch.cuda.is_available() else "cpu"
model_id = "openai/clip-vit-base-patch32"

model = CLIPModel.from_pretrained(model_id).to(device)
processor = CLIPProcessor.from_pretrained(model_id)

TAGS = load_tags()


def clip_tags(image_path, tags=None, top_k=5):
    if tags is None:
        tags = TAGS

    image = Image.open(image_path).convert("RGB")
    # Use prompts for better context
    text_prompts = [f"a photo of {tag}" for tag in tags]

    inputs = processor(
        text=text_prompts, images=image, return_tensors="pt", padding=True
    ).to(device)

    with torch.no_grad():
        outputs = model(**inputs)

    probs = outputs.logits_per_image.softmax(dim=1)

    results = []
    for i, tag in enumerate(tags):
        results.append({"tag": tag, "confidence": float(probs[0][i])})

    results.sort(key=lambda x: x["confidence"], reverse=True)

    return results[:top_k]
