with open("app/build.gradle.kts", "r") as f:
    content = f.read()

if "androidx.documentfile:documentfile" not in content:
    content = content.replace(
        'implementation("androidx.core:core-ktx:1.12.0")',
        'implementation("androidx.core:core-ktx:1.12.0")\n    implementation("androidx.documentfile:documentfile:1.0.1")'
    )

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
