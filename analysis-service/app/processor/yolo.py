from ultralytics import YOLO

model = YOLO("yolov8n.pt")


def detect_objects(image_path):

    results = model(image_path)

    objects = []

    for r in results:
        for box in r.boxes:
            objects.append(
                {"label": r.names[int(box.cls)], "confidence": float(box.conf)}
            )

    return objects
