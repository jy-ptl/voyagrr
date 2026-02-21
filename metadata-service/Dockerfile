FROM python:3.11-slim

WORKDIR /app

RUN apt-get update && apt-get install -y \
    libmagic1 \
    file \
    ffmpeg \
    exiftool \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY app ./app
COPY config ./config

CMD ["python", "-u", "-m", "app.main"]
