from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from pathlib import Path
import json
import base64

# Correct import for solana-py >=0.30 (for Pubkey representation)
from solders.pubkey import Pubkey

app = FastAPI()

IDL_PATH = Path(__file__).parent / "idl.json"

def load_idl():
    try:
        with open(IDL_PATH, encoding="utf-8") as f:
            return json.load(f)
    except FileNotFoundError:
        raise HTTPException(status_code=500, detail="IDL file not found")
    except json.JSONDecodeError as e:
        raise HTTPException(status_code=500, detail=f"IDL file is not valid JSON: {e}")

@app.get("/")
def root():
    return {"status": "ok"}

@app.get("/idl")
def get_idl():
    return load_idl()

@app.get("/program_id")
def program_id():
    idl = load_idl()
    try:
        program_id_str = idl["address"]
        program_pubkey = Pubkey.from_string(program_id_str)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to extract program ID from IDL: {e}")
    return {
        "program_id": program_id_str,
        "program_pubkey_base58": str(program_pubkey)
    }

# -- ACTUAL SOLANA ON-CHAIN CODE PLACEHOLDER HERE --
def store_hash_on_chain(hash_bytes: bytes) -> str:
    # TODO: Put your actual Solana writing logic here
    # For now, this just returns a dummy tx signature
    # Replace this code with solana-py, or a shell command, or however you interact with Anchor/Solana.
    return "dummy_tx_signature_FOR_HASH_" + base64.b64encode(hash_bytes[:8]).decode('utf-8')

# ------------------ POST endpoint to receive hash from Java backend -------------------
class StoreHashRequest(BaseModel):
    hash_bytes_b64: str

@app.post("/store_hash")
def store_hash(req: StoreHashRequest):
    try:
        hash_bytes = base64.b64decode(req.hash_bytes_b64)
        tx_signature = store_hash_on_chain(hash_bytes)
        return {"tx_signature": tx_signature}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
