import json

INPUT = "../collectible_auth_program/target/idl/collectible_auth_program.json"
OUTPUT = "idl_stripped.json"

with open(INPUT, "r", encoding="utf-8") as f:
    idl = json.load(f)

# Remove Anchor CLI fields that confuse anchorpy
idl.pop("address", None)
idl.pop("metadata", None)

with open(OUTPUT, "w", encoding="utf-8") as f:
    json.dump(idl, f, indent=2)

print(f"Stripped IDL written to {OUTPUT}")
