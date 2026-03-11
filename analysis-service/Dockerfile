FROM python:3.11-slim

WORKDIR /app

# Install system dependencies required by opencv / transformers
RUN apt-get update && apt-get install -y \
    ffmpeg \
    libgl1 \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .

# Install CPU-only torch
RUN pip install --no-cache-dir \
    torch==2.3.1 \
    torchvision==0.18.1 \
    --index-url https://download.pytorch.org/whl/cpu

# Install remaining dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Set HuggingFace cache directory
ENV HF_HOME=/models
ENV TRANSFORMERS_CACHE=/models

# Preload model into cache
RUN python - <<EOF
from transformers import pipeline
pipeline("image-classification", model="google/mobilenet_v2_1.0_224")
EOF

COPY app ./app
COPY config ./config

CMD ["python", "-u", "-m", "app.main"]
