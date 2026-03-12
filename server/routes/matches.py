from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database import SessionLocal, get_db
from models import Match, MatchPlayer
from datetime import datetime
import schemas

router = APIRouter()

@router.post("/create", response_model=schemas.MatchResponse)
def create_match(match_data: schemas.MatchCreate, db: Session = Depends(get_db)):
    try:
        dt = datetime.fromisoformat(match_data.match_time)
    except ValueError:
        raise HTTPException(status_code=400, detail="Format timp invalid, folosește ISO format")
    
    new_match = Match(
        title=match_data.title, 
        location=match_data.location, 
        match_time=dt, 
        created_by=match_data.created_by
    )
    db.add(new_match)
    db.commit()
    db.refresh(new_match)
    return new_match

@router.get("/", response_model=list[schemas.MatchResponse])
def list_matches(db: Session = Depends(get_db)):
    return db.query(Match).all()

@router.post("/join")
def join_match(join_data: schemas.MatchJoin, db: Session = Depends(get_db)):
    exists = db.query(MatchPlayer).filter(
        (MatchPlayer.user_id == join_data.user_id) & 
        (MatchPlayer.match_id == join_data.match_id)
    ).first()

    if exists:
        raise HTTPException(status_code=400, detail="Userul a mai intrat deja în meci")
    
    mp = MatchPlayer(user_id=join_data.user_id, match_id=join_data.match_id)
    db.add(mp)
    db.commit()
    return {"message": "Userul s-a alăturat meciului"}