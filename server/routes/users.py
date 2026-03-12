from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from database import SessionLocal
from models import User
from utils.auth import hash_password, verify_password

router = APIRouter()

# Dependency DB
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# Endpoint de register
@router.post("/register")

def register(username: str, email: str, password: str, db: Session = Depends(get_db)):

    if db.query(User).filter((User.username==username)|(User.email==email)).first():
        raise HTTPException(status_code=400, detail="Username sau email deja folosit")
    
    hashed = hash_password(password)
    new_user = User(username=username, email=email, hashed_password=hashed)
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    return {"id": new_user.id, "username": new_user.username, "email": new_user.email}

# Endpoint de login
@router.post("/login")

def login(email: str, password: str, db: Session = Depends(get_db)):

    user = db.query(User).filter(User.email == email).first()

    if not user or not verify_password(password, user.hashed_password):
        raise HTTPException(status_code=401, detail="Email sau parola incorecta")
    
    return {"id": user.id, "username": user.username, "email": user.email, "message": "Login OK"}