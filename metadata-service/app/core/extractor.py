import os
import magic
import ffmpeg
from PIL import Image, ExifTags
from typing import Optional, Tuple, Dict
from logging import getLogger

GPSDict = Dict[str, Optional[float]]

logger = getLogger(__name__)


# =========================================================
# IMAGE EXIF
# =========================================================
def _extract_image_exif(
    path: str,
) -> Tuple[Optional[str], Optional[str], Optional[str], GPSDict]:
    created_on: Optional[str] = None
    updated_on: Optional[str] = None
    device: Optional[str] = None

    gps: GPSDict = {"lat": None, "lon": None}

    try:
        with Image.open(path) as img:
            exif = img.getexif()
            if not exif:
                return created_on, updated_on, device, gps

            # ----------------- Basic EXIF -----------------
            exif_data = {
                ExifTags.TAGS.get(k): v for k, v in exif.items() if k in ExifTags.TAGS
            }

            created_on = exif_data.get("DateTimeOriginal")
            updated_on = exif_data.get("DateTime")

            make = exif_data.get("Make", "")
            model = exif_data.get("Model", "")
            device = (make + " " + model).strip() or None

            # ----------------- GPS -----------------

            gps_ifd = None

            if 34853 in exif:
                try:
                    gps_ifd = exif.get_ifd(34853)
                except Exception:
                    gps_ifd = None

            if gps_ifd:
                gps_parsed = {
                    ExifTags.GPSTAGS.get(tag, tag): value
                    for tag, value in gps_ifd.items()
                }

                def _to_float(value):
                    if isinstance(value, tuple):
                        return float(value[0]) / float(value[1])
                    return float(value)

                def _convert(coord):
                    d, m, s = coord
                    return _to_float(d) + _to_float(m) / 60 + _to_float(s) / 3600

                if "GPSLatitude" in gps_parsed and "GPSLongitude" in gps_parsed:
                    lat = _convert(gps_parsed["GPSLatitude"])
                    lon = _convert(gps_parsed["GPSLongitude"])

                    if gps_parsed.get("GPSLatitudeRef") == "S":
                        lat = -lat
                    if gps_parsed.get("GPSLongitudeRef") == "W":
                        lon = -lon

                    gps["lat"] = round(lat, 6)
                    gps["lon"] = round(lon, 6)

    except Exception as e:
        logger.warning("EXIF extraction failed: %s", e)
        pass

    return created_on, updated_on, device, gps


# =========================================================
# VIDEO META
# =========================================================
def _extract_video_meta(path: str) -> Tuple[Optional[str], Optional[str]]:
    created_on: Optional[str] = None
    device: Optional[str] = None

    try:
        probe = ffmpeg.probe(path)
        fmt = probe.get("format", {})
        tags = fmt.get("tags", {})

        created_on = tags.get("creation_time")
        device = tags.get("encoder") or tags.get("com.apple.quicktime.make")

    except Exception:
        pass

    return created_on, device


# =========================================================
# PUBLIC API
# =========================================================
def extract_metadata(file_path: str) -> dict:
    mime = magic.from_file(file_path, mime=True)
    size = os.path.getsize(file_path)

    width = height = duration = None
    created_on = updated_on = device = None
    gps: GPSDict = {"lat": None, "lon": None}

    # ---------- IMAGE ----------
    if mime.startswith("image"):
        with Image.open(file_path) as img:
            width, height = img.size

        created_on, updated_on, device, gps = _extract_image_exif(file_path)

    # ---------- VIDEO ----------
    elif mime.startswith("video"):
        probe = ffmpeg.probe(file_path)

        duration = int(float(probe["format"]["duration"]))

        for stream in probe["streams"]:
            if stream["codec_type"] == "video":
                width = stream.get("width")
                height = stream.get("height")

        created_on, device = _extract_video_meta(file_path)

    # Optional: return None instead of empty GPS
    location = gps if gps["lat"] is not None else None

    return {
        "mime": mime,
        "size": size,
        "width": width,
        "height": height,
        "duration": duration,
        "createdOn": created_on,
        "updatedOn": updated_on,
        "device": device,
        "location": location,
    }
