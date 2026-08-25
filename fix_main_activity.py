with open("app/src/main/java/com/smoothplay/app/MainActivity.kt", "r") as f:
    content = f.read()

imports = """
import android.content.pm.ActivityInfo
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
"""

logic = """
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            OrientationManager.isLandscape.collect { landscape ->
                requestedOrientation = if (landscape) {
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }
        }
"""

content = content.replace("import android.os.Bundle", "import android.os.Bundle\n" + imports)
content = content.replace("super.onCreate(savedInstanceState)", logic)

with open("app/src/main/java/com/smoothplay/app/MainActivity.kt", "w") as f:
    f.write(content)
