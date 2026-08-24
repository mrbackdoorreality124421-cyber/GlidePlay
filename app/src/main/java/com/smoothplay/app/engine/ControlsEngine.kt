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

data class VirtualButton(val id: String, val cx: Float, val cy: Float, val r: Float, val mappedKey: String)
data class VirtualJoystick(val id: String, val cx: Float, val cy: Float, val r: Float, val mappedKey: String)

@Composable
fun ControlsOverlay(activeProfile: String, onInput: (String, Boolean) -> Unit) {
    val buttons = listOf(VirtualButton("fire", 0.85f, 0.70f, 0.06f, "MOUSE_LEFT"), VirtualButton("jump", 0.90f, 0.50f, 0.05f, "SPACE"))
    val joystick = VirtualJoystick("move", 0.15f, 0.70f, 0.1f, "WASD")
    var joyThumbOffset by remember { mutableStateOf(Offset.Zero) }

    Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures(onPress = { offset ->
            val w = size.width; val h = size.height
            buttons.forEach { b ->
                val bx = b.cx * w; val by = b.cy * h; val br = b.r * w
                if ((offset.x - bx)*(offset.x - bx) + (offset.y - by)*(offset.y - by) <= br*br) {
                    onInput(b.mappedKey, true)
                    tryAwaitRelease()
                    onInput(b.mappedKey, false)
                }
            }
        })
    }.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { },
            onDragEnd = { joyThumbOffset = Offset.Zero; onInput("WASD_RELEASE", false) },
            onDrag = { change, dragAmount -> 
                joyThumbOffset += dragAmount
                onInput("WASD_MOVE", true) 
            }
        )
    }) {
        val w = size.width; val h = size.height
        drawCircle(Color.White.copy(alpha = 0.2f), radius = joystick.r * w, center = Offset(joystick.cx * w, joystick.cy * h), style = Stroke(4f))
        drawCircle(Color.White.copy(alpha = 0.5f), radius = (joystick.r * w) * 0.4f, center = Offset(joystick.cx * w, joystick.cy * h) + joyThumbOffset)
        buttons.forEach { b -> drawCircle(Color.Green.copy(alpha = 0.3f), radius = b.r * w, center = Offset(b.cx * w, b.cy * h)) }
    }
}\n