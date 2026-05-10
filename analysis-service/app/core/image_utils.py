import cv2
import os
import uuid
from app.core.logging_config import get_logger

logger = get_logger(__name__)

def resize_image_if_needed(image_path, temp_dir, max_dim=1024):
    """
    Resizes an image if any dimension exceeds max_dim.
    Returns the path to the resized image (new temp file) or the original path.
    """
    try:
        img = cv2.imread(image_path)
        if img is None:
            logger.error("Could not read image for resizing: %s", image_path)
            return image_path

        h, w = img.shape[:2]
        if h <= max_dim and w <= max_dim:
            return image_path

        # Calculate aspect ratio
        if h > w:
            new_h, new_w = max_dim, int(w * max_dim / h)
        else:
            new_h, new_w = int(h * max_dim / w), max_dim

        logger.info("Resizing image from %dx%d to %dx%d", w, h, new_w, new_h)
        resized_img = cv2.resize(img, (new_w, new_h), interpolation=cv2.INTER_AREA)
        
        resized_path = os.path.join(temp_dir, f"resized_{uuid.uuid4()}.jpg")
        cv2.imwrite(resized_path, resized_img)
        
        return resized_path
    except Exception as e:
        logger.exception("Error during image resizing: %s", e)
        return image_path
