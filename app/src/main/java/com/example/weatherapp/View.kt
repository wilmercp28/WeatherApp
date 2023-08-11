package com.example.weatherapp


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import com.example.weatherapp.ui.theme.WeatherAppTheme
import java.time.format.TextStyle


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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ZipCodeTextField(viewModel)
            Row(
                Modifier.fillMaxWidth()
            ) {
                TemperatureUI(viewModel)
                Spacer(modifier = Modifier.weight(1f))
                CurrentWeatherUI(viewModel)
            }
            Spacer(modifier = Modifier.size(30.dp))
            Text(text = "Time of the forecast ${viewModel.forecastTimeDaily}")
            AsyncImage(
                model = viewModel.dailyWeatherIcon,
                contentDescription = "Daily Weather Image",
            modifier = Modifier
                .size(100.dp))
            if (viewModel.dailySummary == null){
                CircularProgressIndicator()
            } else {
                Text(text = viewModel.dailySummary.toString(), textAlign = TextAlign.Center, fontSize = 20.sp)
            }
            Row(
            ) {
                Forecast("Morning",viewModel.morningTemperature.toString(),viewModel.unitLetter)
                Spacer(modifier = Modifier.weight(1f))
                Forecast("Day",viewModel.dayTemperature.toString(),viewModel.unitLetter)
                Spacer(modifier = Modifier.weight(1f))
                Forecast("Evening",viewModel.eveningTemperature.toString(),viewModel.unitLetter)
                Spacer(modifier = Modifier.weight(1f))
                Forecast("Night",viewModel.nightTemperature.toString(),viewModel.unitLetter)
            }

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
                                changeUnitAndletter(viewModel,"imperial","F")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {Text(text = "Celsius")},
                            onClick = {
                                changeUnitAndletter(viewModel,"metric","C")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {Text(text = "Kelvin")},
                            onClick = {
                                changeUnitAndletter(viewModel,"standar", "K")
                                expanded = false
                            }
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
fun changeUnitAndletter(viewModel: ViewModel, unit: String, unitLetter: String){
    viewModel.unit = unit
    viewModel.unitLetter = unitLetter
    WeatherAPI.clearCache()
    viewModel.weatherData.value = null
    viewModel.fetchWeatherData()
}
@Composable
fun CurrentWeatherUI(viewModel: ViewModel) {
    if(viewModel.currentWeatherIcon == null){
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


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ZipCodeTextField(viewModel: ViewModel) {
    var zipCode by remember { mutableStateOf(viewModel.zipCode.toString()) }
    BasicTextField(
        value = zipCode,
        onValueChange = { zipCode = it},
        keyboardActions = KeyboardActions(
            onDone ={
                viewModel.updateZipCode(zipCode)
                viewModel.fetchWeatherData()
            } ),
        textStyle = androidx.compose.ui.text.TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            textAlign = TextAlign.Center),
        singleLine = true
    )
    if (viewModel.geoData == null){
        CircularProgressIndicator()
    } else {
        Text(text = viewModel.cityName.toString())
    }
}

@Composable
fun Forecast(headerText: String,temp: String,unitletter: String){
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text(text = headerText)
            Row{
                Text(
                    text = temp,
                    fontSize = 30.sp
                )
                Text(
                    text = "o"
                )
                Text(
                    text = unitletter,
                fontSize = 20.sp
                )
            }
            }
        }











