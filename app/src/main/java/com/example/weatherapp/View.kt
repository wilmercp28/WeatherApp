package com.example.weatherapp

import android.graphics.ColorSpace
import android.os.Bundle
import android.widget.TextClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.Locale

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
Box(
    modifier = Modifier
        .background(MaterialTheme.colorScheme.primary)
        .fillMaxSize(),
) {
    Row(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .align(Alignment.TopCenter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Time Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = SimpleDateFormat("hh:mm").format(viewModel.currentTime),
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = SimpleDateFormat("a").format(viewModel.currentTime),
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Image(
            bitmap = viewModel.weatherIcon(), contentDescription = "",
            modifier = Modifier
                .size(150.dp),
            contentScale = ContentScale.Fit
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = SimpleDateFormat("dd").format(viewModel.currentTime),
                fontSize = 60.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = SimpleDateFormat("MM").format(viewModel.currentTime),
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    }


    }

}
    }

}