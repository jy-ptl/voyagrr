import subprocess
import os


def encode_to_hls(input_path, output_dir):
    os.makedirs(output_dir, exist_ok=True)

    command = [
        "ffmpeg",
        "-i",
        input_path,
        "-preset",
        "fast",
        "-g",
        "48",
        "-sc_threshold",
        "0",
        "-map",
        "0:v:0",
        "-map",
        "0:a:0?",
        "-s:v:0",
        "1280x720",
        "-c:v:0",
        "libx264",
        "-b:v:0",
        "2800k",
        "-c:a",
        "aac",
        "-f",
        "hls",
        "-hls_time",
        "6",
        "-hls_playlist_type",
        "vod",
        os.path.join(output_dir, "index.m3u8"),
    ]

    subprocess.run(command, check=True)
