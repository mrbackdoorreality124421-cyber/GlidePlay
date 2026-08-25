import os

def fix_file(filepath):
    if not os.path.isfile(filepath): return
    with open(filepath, 'r') as f:
        content = f.read()
    if "\\n" in content:
        content = content.replace("\\n", "\n")
        with open(filepath, 'w') as f:
            f.write(content)
        print(f"Fixed {filepath}")

for root, dirs, files in os.walk("."):
    if ".git" in root or "node_modules" in root: continue
    for file in files:
        if file.endswith((".kt", ".xml", ".kts", ".java", ".gradle", ".json")):
            fix_file(os.path.join(root, file))

