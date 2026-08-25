import os
import re

def update_file(path, search, replace):
    if not os.path.exists(path):
        return
    with open(path, 'r') as f:
        content = f.read()
    content = content.replace(search, replace)
    with open(path, 'w') as f:
        f.write(content)

# 1. Update AndroidManifest.xml (Orientation & Permissions)
manifest_path = "app/src/main/AndroidManifest.xml"
update_file(manifest_path, 'android:screenOrientation="sensorLandscape"', 'android:screenOrientation="portrait"')
update_file(manifest_path, '<uses-permission android:name="android.permission.INTERNET" />', 
    '<uses-permission android:name="android.permission.INTERNET" />\n    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />')

print("Fixing pipeline...")
