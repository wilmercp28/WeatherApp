package com.example.weatherapp

import android.util.Log
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

object GeocodingAPI{
    private val client = HttpClient()
    private var cachedGeoData: GeoData? = null
    val apiKey = "983609e5f914830a669a8dd853fd34cb"
    suspend fun getGeoData(zipCode: String?): GeoData? {
        if (cachedGeoData != null) {
            Log.d("GeoAPI Cache", "Raw JSON response: $cachedGeoData")
            return cachedGeoData
        } else {
            val httpResponse: HttpResponse =
                client.get("http://api.openweathermap.org/geo/1.0/zip?zip=$zipCode,US&appid=$apiKey")
            val geoDataJson = httpResponse.body<String>()
            Log.d("GeoAPI", "Raw JSON response: $geoDataJson")
            val gson = Gson()
            val geoData = gson.fromJson(geoDataJson, GeoData::class.java)
            cachedGeoData = geoData
            return cachedGeoData
        }
    }
    fun clearCache() {
        cachedGeoData = null

    }

}