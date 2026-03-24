from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database import SessionLocal, get_db
from models import Match, MatchPlayer, User
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

@router.get("/matches/{match_id}/players")
def get_match_players(match_id: int, db: Session = Depends(get_db)):
    players = db.query(User).join(MatchPlayer, User.id == MatchPlayer.user_id).filter(MatchPlayer.match_id == match_id).all()
    
    return [{"id": player.id, "username": player.username} for player in players]

# --- RUTA PENTRU ȘTERGERE MECI ---
@router.delete("/matches/{match_id}")
def delete_match(match_id: int, user_id: int, db: Session = Depends(get_db)):
    match = db.query(Match).filter(Match.id == match_id).first()
    
    if not match:
        raise HTTPException(status_code=404, detail="Meciul nu a fost găsit")
        
    if match.created_by != user_id:
        raise HTTPException(status_code=403, detail="Nu poți șterge meciul altcuiva!")

    db.query(MatchPlayer).filter(MatchPlayer.match_id == match_id).delete()
    
    db.delete(match)
    db.commit()
    
    return {"message": "Meciul a fost șters cu succes!"}

@router.put("/matches/{match_id}")
def update_match(match_id: int, match_data: dict, db: Session = Depends(get_db)):
    match = db.query(Match).filter(Match.id == match_id).first()
    
    if not match:
        raise HTTPException(status_code=404, detail="Meciul nu a fost găsit")
        
    if match.created_by != match_data.get("created_by"):
        raise HTTPException(status_code=403, detail="Nu poți modifica meciul altcuiva!")

    match.title = match_data.get("title", match.title)
    match.location = match_data.get("location", match.location)
    match.match_time = match_data.get("match_time", match.match_time)
    
    db.commit()
    return {"message": "Meciul a fost actualizat cu succes!"}