package com.smoothplay.app.engine

object ProfileRecommender {
    val PROFILES = listOf(
        "Super Smooth", "Smooth", "Balance", "High", "Ultra", "Extreme", "Super Extreme"
    )

    fun recommendProfile(deviceScore: Int, gameWeight: Int): String {
        val capabilityDelta = deviceScore - gameWeight
        return when {
            capabilityDelta >= 50 -> "Super Extreme"
            capabilityDelta in 30..49 -> "Extreme"
            capabilityDelta in 10..29 -> "Ultra"
            capabilityDelta in -10..9 -> "Balance"
            capabilityDelta in -30..-11 -> "Smooth"
            else -> "Super Smooth"
        }
    }
}
