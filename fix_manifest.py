import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

# Add service registration
if "<service" not in content:
    service_xml = '\n        <service\n            android:name=".service.DownloadService"\n            android:exported="false" />\n'
    content = content.replace("</application>", f"{service_xml}    </application>")

# Remove landscape forced orientation
content = content.replace('android:screenOrientation="sensorLandscape"', '')

# Add POST_NOTIFICATIONS
if "POST_NOTIFICATIONS" not in content:
    content = content.replace('<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />', '<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />\n    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />')

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
