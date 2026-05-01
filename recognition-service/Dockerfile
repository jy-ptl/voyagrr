FROM python:3.11-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    cmake \
    libgl1 \
    build-essential

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY app ./app
COPY config ./config

CMD ["python", "-u", "-m", "app.main"]
