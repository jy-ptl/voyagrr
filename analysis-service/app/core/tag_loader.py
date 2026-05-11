import os


def load_tags():
    path = f"/app/config/tag_vocabulary.txt"
    tags = []
    try:
        with open(path) as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith("#"):
                    tags.append(line)
    except FileNotFoundError:
        # Fallback for local development if not in /app
        local_path = "config/tag_vocabulary.txt"
        if os.path.exists(local_path):
            with open(local_path) as f:
                for line in f:
                    line = line.strip()
                    if line and not line.startswith("#"):
                        tags.append(line)
    return tags
