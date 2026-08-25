package com.smoothplay.app

import kotlinx.coroutines.flow.MutableStateFlow

object OrientationManager {
    val isLandscape = MutableStateFlow(false)
}
