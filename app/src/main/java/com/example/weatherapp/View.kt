package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun WeatherAppUI(viewModel: ViewModel = ViewModel()){
   LaunchedEffect(Unit) {
        Log.d("Hello", getWeatherData().toString())
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
    Row(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        // Time Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewModel.formattedTime,
                fontSize = 60.sp,
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
        Image(
            bitmap = viewModel.weatherIcon(), contentDescription = "",
            modifier = Modifier
                .size(100.dp),
            contentScale = ContentScale.Fit
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewModel.formattedDay,
                fontSize = 60.sp,
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
    Row(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
    ) {
        Text(text = "")
    }

    }
}
    }
}

suspend fun getWeatherData(): HttpResponse? {
    val apiKey =
        "49338c40fc6197e95db0757dfc52177f" // Replace this with your actual OpenWeatherMap API key
    val weatherAPI = WeatherAPI("49338c40fc6197e95db0757dfc52177f")
    return weatherAPI.getWeatherData(40.713050, -74.007230, "minutely")


}