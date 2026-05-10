from ultralytics import YOLO

# Use Medium model for better accuracy/performance balance
model = YOLO("yolov8m.pt")


def detect_objects(image_path, confidence_threshold=0.3):

    results = model(image_path, conf=confidence_threshold)

    objects = []

    for r in results:
        for box in r.boxes:
            label = r.names[int(box.cls)]
            confidence = float(box.conf)
            objects.append({"label": label, "confidence": confidence})

    return objects
