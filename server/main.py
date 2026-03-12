from fastapi import FastAPI
from .utils import auth
from database import engine, Base

import models
from routes import users, matches

Base.metadata.create_all(bind=engine)

app = FastAPI()

app.include_router(auth.router)
app.include_router(users.router)
app.include_router(matches.router)


@app.get("/")
def root():
    return {"message": "API running"}