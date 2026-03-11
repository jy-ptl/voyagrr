from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from app.core.config import get_config

cfg = get_config()

engine = create_engine(cfg["postgres"]["url"], pool_pre_ping=True)
SessionLocal = sessionmaker(bind=engine)
