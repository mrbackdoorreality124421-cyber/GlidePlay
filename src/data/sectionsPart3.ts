import { CodeBlock, Section } from './sectionsPart1';

export const architectureSectionsPart3: Section[] = [
  {
    id: 13,
    title: "13. JSON Config Templates",
    description: "Exportable data structures for community sharing.",
    codeBlocks: [
      {
        language: "json",
        title: "control_template_fps.json",
        code: `{
  "id": "fps_standard",
  "name": "Standard FPS",
  "joysticks": [
    { "id": "move", "cx": 0.15, "cy": 0.75, "radius": 0.1, "mappedKey": "WASD" }
  ],
  "swipeZones": [
    { "id": "look", "x": 0.5, "y": 0, "w": 0.5, "h": 1.0, "mappedAxis": "MOUSE_XY", "sensitivity": 1.2 }
  ],
  "buttons": [
    { "id": "fire", "x": 0.85, "y": 0.75, "r": 0.05, "mappedKey": "MOUSE_LEFT" },
    { "id": "aim", "x": 0.75, "y": 0.85, "r": 0.04, "mappedKey": "MOUSE_RIGHT" },
    { "id": "jump", "x": 0.9, "y": 0.6, "r": 0.04, "mappedKey": "SPACE" },
    { "id": "reload", "x": 0.7, "y": 0.65, "r": 0.03, "mappedKey": "R" }
  ]
}`
      },
      {
        language: "json",
        title: "optimization_profile.json",
        code: `{
  "id": "balance",
  "resolutionScale": 0.75,
  "renderer": "vulkan_turnip",
  "dxvkAsync": true,
  "fpsCap": 60,
  "cpuHintsEnabled": true,
  "gpuClockMin": "perf",
  "box64Dynarec": 1
}`
      }
    ]
  },
  {
    id: 14,
    title: "14. AndroidManifest.xml",
    description: "Required system permissions for high-performance and storage.",
    codeBlocks: [
      {
        language: "xml",
        title: "AndroidManifest.xml",
        code: `<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.smoothplay.app">

    <!-- Extracting ZIPs and managing Game Library -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="29"/>
    <!-- Required on Android 11+ for full library management -->
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
    
    <!-- Thermal management & background services -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    
    <!-- Gamepads -->
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

    <application
        android:name=".SmoothPlayApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:requestLegacyExternalStorage="true"
        android:theme="@style/Theme.SmoothPlay">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:configChanges="orientation|screenSize|keyboardHidden"
            android:screenOrientation="sensorLandscape">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>`
      }
    ]
  },
  {
    id: 15,
    title: "15. build.gradle.kts Suggestions",
    description: "App-level gradle dependencies.",
    codeBlocks: [
      {
        language: "kotlin",
        title: "build.gradle.kts",
        code: `dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    
    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    
    // Room DB
    val room_version = "2.6.0"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    
    // Dependency Injection (Hilt)
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-android-compiler:2.48")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Optional: JNI for direct Box64/Wine native integration
    // implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
}`
      }
    ]
  },
  {
    id: 16,
    title: "16. Build Instructions",
    description: "Step-by-step Android Studio setup.",
    content: `1. Open **Android Studio**.
2. Select **File > New > Project from Version Control** (or New Project if starting blank).
3. Ensure JDK 17 is selected in **Settings > Build, Execution, Deployment > Build Tools > Gradle**.
4. Sync Gradle.
5. In \`local.properties\`, specify Android SDK path.
6. Connect a physical Android 12+ device (Emulators do not have Vulkan/Turnip support required for DirectX translation).
7. Press **Run (Shift + F10)**.

*Note on Runtime:* To fully test, you must drop the compiled Box64/Wine native libraries (.so) into \`app/src/main/jniLibs/arm64-v8a/\`.`
  },
  {
    id: 17,
    title: "17. Testing Checklist",
    description: "QA matrix to ensure stability across Android variants.",
    content: `- [ ] **Extraction:** Test importing a 5GB+ ZIP to ensure OutOfMemory errors do not occur. (Use chunked streams).
- [ ] **SAF Permissions:** Verify SAF directory selection on Android 11+ and Android 13+.
- [ ] **Thermal Throttling:** Run a heavy game for 20 minutes; verify \`OptimizationEngine\` receives thermal events and drops resolution.
- [ ] **Controls Overlay:** Test 5-finger multi-touch. Ensure joystick doesn't lock when second finger taps the fire button.
- [ ] **Controller Hotplug:** Connect/disconnect a Bluetooth Xbox controller mid-game; ensure seamless transition.
- [ ] **Background Death:** Press Home button mid-game. Verify the game state is frozen or gracefully exited, not leaking memory.`
  },
  {
    id: 18,
    title: "18. Future Roadmap",
    description: "Long-term vision for SmoothPlay evolution.",
    content: `**Phase 2: Community Ecosystem**
- Cloud-synced optimization profiles. Users can upload their configurations for specific games.
- Peer-to-peer control layout sharing.

**Phase 3: Advanced Runtimes**
- Direct integration of FEX-Emu.
- Support for multiple Windows versions (Win 7/10/11) configurations dynamically.
- Modding support: Dedicated UI to drop \`.asi\` or \`dinput8.dll\` files into game folders.

**Phase 4: Game Streaming Fallback**
- If the device is too weak (Device Score < 20), offer an API hook to stream the game from a local PC via Moonlight/Sunshine protocols.`
  }
];
