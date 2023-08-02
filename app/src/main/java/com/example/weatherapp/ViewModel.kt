package com.example.weatherapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.lifecycle.ViewModel


class ViewModel: ViewModel(){
    @Composable
    fun WeatherIcon(): ImageBitmap {
        val sun = ImageBitmap.imageResource(R.drawable.sun)
        val claudy = ImageBitmap.imageResource(R.drawable.cloudy)
        val raining = ImageBitmap.imageResource(R.drawable.raining)
        return sun
    }



}
