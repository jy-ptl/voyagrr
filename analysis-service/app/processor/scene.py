from PIL import Image
from app.core.tag_loader import load_tags
import torchvision.transforms as transforms
import torch
from torchvision import models

model = models.resnet18(weights="IMAGENET1K_V1")
model.eval()

transform = transforms.Compose([transforms.Resize((224, 224)), transforms.ToTensor()])

SCENES = load_tags()


def detect_scene(image_path):

    img = Image.open(image_path).convert("RGB")
    img = transform(img).unsqueeze(0)

    with torch.no_grad():
        outputs = model(img)

    idx = outputs.argmax().item()

    return SCENES[idx % len(SCENES)]
