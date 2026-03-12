from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database import SessionLocal
from models import Match, MatchPlayer
from datetime import datetime

router = APIRouter()

# Dependency DB
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# Creează meci
@router.post("/create")
def create_match(title: str, location: str, match_time: str, created_by: int, db: Session = Depends(get_db)):
    try:
        dt = datetime.fromisoformat(match_time)
    except:
        raise HTTPException(status_code=400, detail="Format timp invalid, folosește ISO format")
    
    new_match = Match(title=title, location=location, match_time=dt, created_by=created_by)
    db.add(new_match)
    db.commit()
    db.refresh(new_match)
    return {"id": new_match.id, "title": new_match.title, "location": new_match.location, "match_time": new_match.match_time.isoformat()}

# Listează toate meciurile
@router.get("/")
def list_matches(db: Session = Depends(get_db)):
    matches = db.query(Match).all()
    return [
        {
            "id": m.id,
            "title": m.title,
            "location": m.location,
            "match_time": m.match_time.isoformat(),
            "created_by": m.created_by
        } for m in matches
    ]

# Join la meci
@router.post("/join")
def join_match(user_id: int, match_id: int, db: Session = Depends(get_db)):
    
    exists = db.query(MatchPlayer).filter((MatchPlayer.user_id==user_id) 
                                          & (MatchPlayer.match_id==match_id)).first()

    if exists:
        raise HTTPException(status_code=400, detail="Userul a mai intrat deja în meci")
    
    mp = MatchPlayer(user_id=user_id, match_id=match_id)
    db.add(mp)
    db.commit()
    return {"message": "Userul s-a alăturat meciului"}