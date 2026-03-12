from sqlalchemy import Column, Integer, ForeignKey
from database import Base

class MatchPlayer(Base):
    __tablename__ = "match_players"

    user_id = Column(Integer, ForeignKey("users.id"), primary_key=True)
    match_id = Column(Integer, ForeignKey("matches.id"), primary_key=True)