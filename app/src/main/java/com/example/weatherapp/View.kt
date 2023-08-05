package com.example.weatherapp

import android.graphics.Matrix.ScaleToFit
import android.icu.number.Scale
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.weatherapp.ui.theme.WeatherAppTheme
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class View : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherAppPreview()

                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WeatherAppPreview() {
    WeatherAppTheme {
        WeatherAppUI()
    }
}
@Composable
fun WeatherAppUI(viewModel: ViewModel = remember { ViewModel()}){
    val weatherData by viewModel.weatherData
    val weatherIconCode = weatherData?.current?.weather?.get(0)?.icon

    val baseUrl = "https://openweathermap.org/img/wn/"
    val iconSize = "@2x.png"
    val iconUrl = "$baseUrl$weatherIconCode$iconSize"

    val painter = rememberAsyncImagePainter(model = iconUrl)




    DisposableEffect(Unit) {
        viewModel.fetchWeatherData()
        onDispose { /* Clean up if needed */ }
}
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .background(MaterialTheme.colorScheme.primary)
    )
    Column(modifier = Modifier.fillMaxSize()){
Column(
    modifier = Modifier
        .background(MaterialTheme.colorScheme.primary)
        .fillMaxSize(),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = iconUrl,
            contentDescription = null,
            modifier = Modifier
                .size(300.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = weatherData?.current?.weather?.get(0)?.description.toString())
    }
    Row(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Time Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewModel.formattedTime,
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = viewModel.formattedAmPm,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewModel.formattedDay,
                fontSize = 40.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = viewModel.formattedMonth,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
    Column(

    ) {

    }
}
    }
    }
}




