def load_tags():

    path = f"/app/config/tag_vocabulary.txt"
    with open(path) as f:
        return [line.strip() for line in f if line.strip()]
