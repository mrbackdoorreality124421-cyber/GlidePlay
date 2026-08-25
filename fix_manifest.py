import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

# Add service registration
if "<service" not in content:
    service_xml = '\n        <service\n            android:name=".service.DownloadService"\n            android:exported="false" />\n'
    content = content.replace("</application>", f"{service_xml}    </application>")

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
