package com.example.weatherapp

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Model: ViewModel() {

    @Composable
    fun DateAndTime(isDateOrTime: String): String {
        // Time
        val currentTime = SimpleDateFormat("hh:mm \n\na", Locale.getDefault())
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
            }
        }
        //Date
        val currentDate = SimpleDateFormat("M:dd", Locale.getDefault())


        if (isDateOrTime == "Time") {
            return currentTime.format(Calendar.getInstance().time)
        } else if (isDateOrTime == "Date") {
        return currentDate.format(Calendar.getInstance().time)
        }
        return "Invalid"
    }
}