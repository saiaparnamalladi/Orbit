package com.orbit.app.utils

import java.util.Calendar

enum class DayPhase {
    MORNING, AFTERNOON, EVENING, NIGHT
}

object PhaseCalculator {
    fun currentPhase(): DayPhase {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 6..10  -> DayPhase.MORNING
            in 11..16 -> DayPhase.AFTERNOON
            in 17..20 -> DayPhase.EVENING
            else      -> DayPhase.NIGHT
        }
    }

    fun phaseLabel(phase: DayPhase): String = when (phase) {
        DayPhase.MORNING   -> "good morning"
        DayPhase.AFTERNOON -> "good afternoon"
        DayPhase.EVENING   -> "good evening"
        DayPhase.NIGHT     -> "good night"
    }
}
