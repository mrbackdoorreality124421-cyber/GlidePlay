package com.smoothplay.app.engine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

data class VirtualButton(val id: String, val cx: Float, val cy: Float, val r: Float, val mappedKey: String, val label: String = "")
data class VirtualJoystick(val id: String, val cx: Float, val cy: Float, val r: Float, val mappedKey: String)

@Composable
fun ControlsOverlay(activeProfile: String, onInput: (String, Boolean) -> Unit) {
    val buttons = remember {
        listOf(
            VirtualButton("fire", 0.88f, 0.75f, 0.06f, "MOUSE_LEFT", "FIRE"),
            VirtualButton("aim", 0.76f, 0.85f, 0.045f, "MOUSE_RIGHT", "AIM"),
            VirtualButton("jump", 0.92f, 0.55f, 0.05f, "SPACE", "JUMP"),
            VirtualButton("reload", 0.75f, 0.55f, 0.04f, "R", "RELOAD")
        )
    }
    val joystick = remember { VirtualJoystick("move", 0.15f, 0.75f, 0.10f, "WASD") }
    var joyThumbOffset by remember { mutableStateOf(Offset.Zero) }
    var pressedButton by remember { mutableStateOf<String?>(null) }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onPress = { offset ->
                val w = size.width; val h = size.height
                val hit = buttons.find { b ->
                    val bx = b.cx * w; val by = b.cy * h; val br = b.r * w
                    val dx = offset.x - bx; val dy = offset.y - by
                    dx * dx + dy * dy <= br * br
                }
                if (hit != null) {
                    pressedButton = hit.id
                    onInput(hit.mappedKey, true)
                    tryAwaitRelease()
                    onInput(hit.mappedKey, false)
                    pressedButton = null
                }
            })
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragEnd = { joyThumbOffset = Offset.Zero; onInput("WASD_RELEASE", false) },
                onDragCancel = { joyThumbOffset = Offset.Zero; onInput("WASD_RELEASE", false) },
                onDrag = { change, dragAmount ->
                    val maxOffset = joystick.r * size.width * 0.6f
                    val newOffset = joyThumbOffset + dragAmount
                    val dist = newOffset.getDistance()
                    joyThumbOffset = if (dist > maxOffset) newOffset * (maxOffset / dist) else newOffset
                    change.consume()
                    val normX = (joyThumbOffset.x / maxOffset).coerceIn(-1f, 1f)
                    val normY = (joyThumbOffset.y / maxOffset).coerceIn(-1f, 1f)
                    onInput("WASD_MOVE|$normX|$normY", true)
                }
            )
        }
    ) {
        val w = size.width
        drawCircle(Color.White.copy(alpha = 0.15f), radius = joystick.r * w, 
                   center = Offset(joystick.cx * w, joystick.cy * h), style = Stroke(4f))
        drawCircle(Color.White.copy(alpha = 0.7f), radius = (joystick.r * w) * 0.4f,
                   center = Offset(joystick.cx * w, joystick.cy * h) + joyThumbOffset)
        buttons.forEach { b ->
            val color = if (pressedButton == b.id) Color.Green.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.25f)
            drawCircle(color, radius = b.r * w, center = Offset(b.cx * w, b.cy * h))
        }
    }
}
