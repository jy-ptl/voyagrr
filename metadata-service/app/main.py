from app.consumer import consumer
from app.minio_client import client
from app.db import SessionLocal
from app.extractor import extract_metadata
import tempfile

def process(event):

    bucket = event["bucket"]
    key = event["objectKey"]

    response = client.get_object(bucket, key)

    with tempfile.NamedTemporaryFile(delete=False) as tmp:
        for chunk in response.stream(32*1024):
            tmp.write(chunk)
        path = tmp.name

    meta = extract_metadata(path)

    session = SessionLocal()
    # store metadata...
    session.commit()


for msg in consumer:
    try:
        process(msg.value)
        print("processed", msg.value["objectKey"])
    except Exception as e:
        print("error:", e)

