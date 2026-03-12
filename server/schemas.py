from pydantic import BaseModel
from datetime import datetime

class UserCreate(BaseModel):
    username: str
    email: str
    password: str

class UserResponse(BaseModel):
    id: int
    username: str
    email: str
    
    class Config:
        from_attributes = True

class LoginRequest(BaseModel):
    email: str
    password: str

class MatchCreate(BaseModel):
    title: str
    location: str
    match_time: str
    created_by: int

class MatchResponse(BaseModel):
    id: int
    title: str
    location: str
    match_time: datetime
    created_by: int
    
    class Config:
        from_attributes = True

class MatchJoin(BaseModel):
    user_id: int
    match_id: int