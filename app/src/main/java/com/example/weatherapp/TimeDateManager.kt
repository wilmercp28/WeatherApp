package com.example.weatherapp

import androidx.compose.runtime.MutableState
import java.util.Calendar
import java.util.Date

class TimeDateManager(private val currentTime: MutableState<Date>) {
    private val calendar: Calendar = Calendar.getInstance()
    fun updateTime() {
        currentTime.value = calendar.time
    }
}