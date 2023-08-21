package com.example.weatherapp


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.compose.WeatherAppTheme
import kotlin.math.roundToInt


class View : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                    WeatherAppPreview()
            }
        }
    }
}
@Preview
@Composable
fun WeatherAppPreview() {
    WeatherAppTheme {
        WeatherAppUI()
    }
}
@Composable
fun WeatherAppUI(viewModel: ViewModel = remember { ViewModel()}) {
    val context = LocalContext.current
    viewModel.init(context)
    DisposableEffect(Unit) {
        viewModel.fetchGeoData(context)
        onDispose { /* Clean up if needed */ }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ZipCodeTextField(viewModel, context)
            if (viewModel.weatherData.value == null) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
            Row(
                Modifier.fillMaxWidth()
            ) {
                    TemperatureUI(viewModel, context)
                    Spacer(modifier = Modifier.weight(1f))
                    CurrentWeatherUI(viewModel)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(50.dp))
                    val selectedIndex = rememberSaveable { mutableStateOf(0) }
                    LazyRow(
                        modifier = Modifier
                    ) {
                        if (viewModel.foreCastData.value != null) {
                            item {
                                for (i in 0 until 40) {
                                    ForeCast(
                                        viewModel,
                                        viewModel.foreCastData.value?.list?.get(i),
                                        selectedIndex,
                                        i
                                    )
                                }
                            }
                        }
                    }
                    WeatherDetails(viewModel, selectedIndex)
                }
            }
        }
    }
}


@Composable
fun TemperatureUI(viewModel: ViewModel, context: Context) {
    var expanded by remember { mutableStateOf(false) }
        Column {
            Row {
                Text(
                    text = viewModel.currentTemperature.toString(),
                    fontSize = 80.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "o",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Left,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .offset(y = 20.dp)
                )
                Box{
                    Text(
                        text = viewModel.unitLetter,
                        color = MaterialTheme.colorScheme.onSurface,
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
                            text = {Text(text = "Celsius (C)")},
                            onClick = {
                                changeUnitAndletter(viewModel,"metric","C",context)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {Text(text = "Kelvin (K)")},
                            onClick = {
                                changeUnitAndletter(viewModel,"standard", "K",context)
                                expanded = false
                            }
                        )
                    }
                }
            }
            Row {
                Text(
                    text = "Feels Like ${viewModel.currentTemperatureFeelsLike.toString()}",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "o",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
fun changeUnitAndletter(viewModel: ViewModel, unit: String, unitLetter: String, context: Context){
    viewModel.unit = unit
    viewModel.unitLetter = unitLetter
    WeatherAPI.clearCache()
    ForecastAPI.clearCache()
    viewModel.weatherData.value = null
    viewModel.foreCastData.value = null
    viewModel.fetchWeatherData()
    viewModel.temperatureList.clear()
    ViewModel.SaveData.saveData(context,"unitLetter",unitLetter)
    ViewModel.SaveData.saveData(context,"unit",unit)
}
@Composable
fun CurrentWeatherUI(viewModel: ViewModel) {
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
                    text = viewModel.currentWeatherDescription.toString().replaceFirstChar { it.uppercase() },
                    fontSize = 25.sp,
                    textAlign = TextAlign.Left,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ZipCodeTextField(viewModel: ViewModel, context: Context) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = viewModel.zipCode.toString(),
        onValueChange = { viewModel.zipCode = it },
        modifier = Modifier
            .width(200.dp),
        label = {
            Text(text = "Zip Code")
                },
        placeholder = {
            if (!viewModel.isValidZipCode) {
                Text(text = "  Invalid Zip Code")
            } else {
                Text(text = "  Enter Your Zip Code")
            }
                      },
        textStyle = androidx.compose.ui.text.TextStyle(
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(
            onDone = {
                viewModel.fetchGeoData(context)
                keyboardController?.hide()
            }
        ),
        )
    Spacer(modifier = Modifier.size(20.dp))
   if (viewModel.weatherData.value != null) {
       Text(text = viewModel.cityName.toString(), color = MaterialTheme.colorScheme.onSurface)
   }
}

@Composable
fun  ForeCast(
    viewModel: ViewModel,
    list: ForecastItem?,
    selectedIndex: MutableState<Int>?,
    index: Int,
) {
        val foreCastTime =  rememberSaveable{viewModel.convertUnixTimeToLocalTime(list?.dt!!, "MM-dd hh:mm a") }
        Column(
            modifier = Modifier
                .padding(5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
                val selectedColor: Color = if (selectedIndex?.value == index){
                MaterialTheme.colorScheme.onSurface
            } else {
                Color.Transparent
            }
                Column(
                    modifier = Modifier
                        .border(5.dp, selectedColor, RoundedCornerShape(30.dp))
                        .height(200.dp)
                        .width(100.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(30.dp))
                        .clickable {
                            selectedIndex?.value = index
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
                {
                    Text(
                        text = foreCastTime.substringBefore(" ").replace("-", "/"),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    //Time
                    Text(
                        text = foreCastTime.substringAfter(" "),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    AsyncImage(
                        model = viewModel.getWeatherIcon(list?.weather?.get(0)?.icon!!),
                        contentDescription = "Weather Icon",
                        modifier = Modifier
                            .size(100.dp)
                    )
                    Row {
                        Text(
                            text = list.main.temp.roundToInt().toString(),
                            fontSize = 30.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "o",
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .offset(y = (-2).dp)
                        )
                        Text(
                            text = viewModel.unitLetter,
                            fontSize = 30.sp
                        )
                    }
                }
                //Check for Value been Forgot after Recomposition
                val rainTextColor: Color = if (isSystemInDarkTheme()) {
                    Color(173, 216, 230)
                } else {
                    Color.Blue
                }
                val percentageOfPrecipitation = rememberSaveable { list!!.pop * 100 }
                if (list!!.weather[0].id in 200..622) {
                    Text(
                        text = "$percentageOfPrecipitation%",
                        color = rainTextColor
                    )
                }
            }
        }

@Composable
fun WeatherDetails(viewModel: ViewModel, selectedIndex: MutableState<Int>) {
    if (viewModel.foreCastData.value == null) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
    } else {
            val foreCastData = viewModel.foreCastData.value?.list?.get(selectedIndex.value)
            val percentageOfPrecipitation = viewModel.foreCastData.value!!.list[selectedIndex.value].pop * 100

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Text(
                    text = viewModel.foreCastData.value!!.list[selectedIndex.value].weather[0].description.replaceFirstChar { it.uppercase() },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 30.sp
                    )
                Text(
                    text = "Details", fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row {
                    //Right Side Colum
                    Column {
                        Text(
                            text = "Max\n" +
                                    "Min\n" +
                                    "Feels Like\n" +
                                    "Humidity\n" +
                                    "Precipitation\n" +
                                    "Pressure",
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        Text(
                            text = "${foreCastData?.main?.temp_max?.roundToInt()}${viewModel.unitLetter}\n" +
                                    "${foreCastData?.main?.temp_min?.roundToInt()}${viewModel.unitLetter}\n" +
                                    "${foreCastData?.main?.feels_like?.roundToInt()}\n" +
                                    "${foreCastData?.main?.humidity}%\n" +
                                    if (percentageOfPrecipitation.roundToInt() == 0){
                                        "No precipitation expected\n"
                                    } else {
                                        "${percentageOfPrecipitation.roundToInt()}%\n"
                                    } + "${foreCastData?.main?.pressure} hPa\n"

                            ,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End
                        )
                        Log.d("Max", foreCastData?.main?.temp_max.toString())
                        Log.d("min", foreCastData?.main?.temp_min.toString())
                    }
                }
            }
        }
    }
