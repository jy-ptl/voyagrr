import logging
import sys


def setup_logging(level=logging.INFO):
    logging.basicConfig(
        level=level,
        format=f"%(asctime)s [analysis-service] %(levelname)s %(message)s",
        handlers=[logging.StreamHandler(sys.stdout)],
        force=True,
    )


def get_logger(name):
    return logging.getLogger(name)
