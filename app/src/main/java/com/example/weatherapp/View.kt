package com.example.weatherapp


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.ui.theme.WeatherAppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
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
fun WeatherAppUI(viewModel: ViewModel = remember { ViewModel()}){
    val context = LocalContext.current
    viewModel.init(context)
    DisposableEffect(Unit) {
        viewModel.fetchGeoData(context)
        onDispose { /* Clean up if needed */ }
}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(40.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ZipCodeTextField(viewModel,context)
                Row(
                    Modifier.fillMaxWidth()
                ) {
                    TemperatureUI(viewModel, context)
                    Spacer(modifier = Modifier.weight(1f))
                    CurrentWeatherUI(viewModel)
                }
            // Make it Into a Row
                Column(
                ) {
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(0))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(1))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(2))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(3))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(4))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(5))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(6))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(7))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(9))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(10))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(11))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(12))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(13))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(14))
                    ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(15))
                    val currentTime = Calendar.getInstance().time
                    val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    Text(text = formattedTime.format(currentTime))
                }
            }
        }
    }

@Composable
fun TemperatureUI(viewModel: ViewModel, context: Context) {
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
                Box{
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
                            ) {
                        DropdownMenuItem(
                            modifier = Modifier,
                            text = {Text(text = "Fahrenheit (F)")},
                            onClick = {
                                changeUnitAndletter(viewModel,"imperial","F",context)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {Text(text = "Celsius (C)",)},
                            onClick = {
                                changeUnitAndletter(viewModel,"metric","C",context)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {Text(text = "Kelvin (K)")},
                            onClick = {
                                changeUnitAndletter(viewModel,"standar", "K",context)
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
fun changeUnitAndletter(viewModel: ViewModel, unit: String, unitLetter: String, context: Context){
    viewModel.unit = unit
    viewModel.unitLetter = unitLetter
    WeatherAPI.clearCache()
    viewModel.weatherData.value = null
    viewModel.fetchWeatherData()
    ViewModel.SaveData.saveData(context,"unitLetter",unitLetter)
    ViewModel.SaveData.saveData(context,"unit",unit)
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




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZipCodeTextField(viewModel: ViewModel, context: Context) {
        TextField(
            value = viewModel.zipCode.toString(),
            onValueChange = { viewModel.zipCode = it },
            modifier = Modifier
                .width(200.dp),
            label = {
                Text(
                    text = "Zip Code",
                    )
                    },
            placeholder = {
                if (!viewModel.isValidZipCode) {
                    Text(
                        text = "  Invalid Zip Code",
                        )
                } else {
                    Text(
                        text = "  Enter Your Zip Code",
                        )
                }
                          },
            textStyle = androidx.compose.ui.text.TextStyle(
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(
                onDone = {
                    viewModel.fetchGeoData(context)
                }
            ),
            )
    Spacer(modifier = Modifier.size(20.dp))
    if (viewModel.cityName == null){
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
    }else {
        Text(text = viewModel.cityName.toString())
    }
}

@Composable
fun ForeCast(viewModel: ViewModel, list: ForecastItem?){
    if (list == null){
        CircularProgressIndicator()
    } else {
        val forecastTime = viewModel.convertUnixTimeToLocalTime(list?.dt!!)
        Column {
            Text(text = forecastTime)
        }
    }
}













