package com.example.weatherapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import com.example.weatherapp.ui.theme.WeatherAppTheme


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
    DisposableEffect(Unit) {
        viewModel.fetchWeatherData()
        onDispose { /* Clean up if needed */ }
}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(40.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CityName(viewModel)
            Row(
                Modifier.fillMaxWidth()
            ) {
                TemperatureUI(viewModel)
                Spacer(modifier = Modifier.weight(1f))
                CurrentWeatherUI(viewModel)
            }
            Spacer(modifier = Modifier.size(30.dp))
            ForecastHourly(viewModel)
        }
    }
}
@Composable
fun TemperatureUI(viewModel: ViewModel) {
    var expanded by remember { mutableStateOf(false) }
    if (viewModel.currentTemperature == null){
        CircularProgressIndicator()
    } else {
        Column {
            Row {
                Text(
                    text = viewModel.currentTemperature.toString(),
                    fontSize = 80.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "o",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .offset(y = 20.dp)
                )
                Box(
                ) {
                    Text(
                        text = viewModel.unitLetter,
                        modifier = Modifier
                            .offset(y = 20.dp)
                            .clickable { expanded = !expanded },
                        fontSize = 50.sp,
                        textAlign = TextAlign.Left,
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primary),
                            ) {
                        DropdownMenuItem(
                            modifier = Modifier,
                            text = {Text(text = "Fahrenheit")},
                            onClick = {
                                viewModel.unit = "imperial"
                                WeatherAPI.clearCache()

                            }
                        )
                        DropdownMenuItem(
                            text = {Text(text = "Celsius")},
                            onClick = {
                                viewModel.unit = "metric"
                                WeatherAPI.clearCache()

                            }
                        )
                        DropdownMenuItem(
                            text = {Text(text = "Kelvin")},
                            onClick = {viewModel.unit = "standar"}
                        )
                    }
                }
            }
            Row {
                Text(
                    text = "Feels Like ${viewModel.currentTemperatureFeelsLike.toString()}",
                    fontSize = 20.sp
                )
                Text(
                    text = "o",
                    fontSize = 10.sp
                )
            }
        }
    }
}
@Composable
fun CurrentWeatherUI(viewModel: ViewModel) {
    if(viewModel.currentWeatherIcon == ""){
        CircularProgressIndicator()
    } else {
        Column {
            AsyncImage(
                model = viewModel.currentWeatherIcon,
                contentDescription = "WeatherIcon",
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Fit
            )
            if (viewModel.currentWeatherDescription == null) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = viewModel.currentWeatherDescription.toString(),
                    fontSize = 25.sp,
                    textAlign = TextAlign.Left
                )
            }
        }
    }
}

@Composable
fun CityName(viewModel: ViewModel) {
   Text(
       text = viewModel.cityName.toString(),
       fontSize = 40.sp,
       textAlign = TextAlign.Center
   )

}

@Composable
fun ForecastHourly(viewModel: ViewModel){
    Row(
    modifier = Modifier
        .fillMaxWidth()
    ) {
    Column{
Text(text = viewModel.forecastTime.toString())
    }
    }
}









