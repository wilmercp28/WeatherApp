package com.example.weatherapp

import io.ktor.util.StringValues

data class GeoData(
    val zip: String,
    val name: String,
    val lat: String,
    val lon: String,
    val country: String
)
