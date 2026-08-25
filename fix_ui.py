import re

with open("app/src/main/java/com/smoothplay/app/ui/screens/SetupScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    'if (!isInstalling) {',
    'if (!isInstalling && statusText != "Installation Complete!") {'
)

with open("app/src/main/java/com/smoothplay/app/ui/screens/SetupScreen.kt", "w") as f:
    f.write(content)
