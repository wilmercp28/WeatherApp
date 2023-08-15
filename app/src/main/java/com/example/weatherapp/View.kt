package com.example.weatherapp


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.android.gms.fitness.data.DataPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt


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
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
            ZipCodeTextField(viewModel, context)
            Row(
                Modifier.fillMaxWidth()
            ) {
                TemperatureUI(viewModel, context)
                Spacer(modifier = Modifier.weight(1f))
                CurrentWeatherUI(viewModel)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                Column {
                    Row {
                        if (viewModel.foreCastData.value != null) {
                            for (i in 0 until 40) {
                                ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(i))
                            }
                        }
                    }

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
    ForecastAPI.clearCache()
    viewModel.weatherData.value = null
    viewModel.foreCastData.value = null
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
    val foreCastTime = viewModel.convertUnixTimeToLocalTime(list?.dt!!,"MM-dd hh:mm a")
    val foreCastTimeStringToDate = viewModel.stringToDate(foreCastTime,"MM-dd hh:mm a")
    val currentTime = viewModel.getCurrentTime("MM-dd hh:mm a")
    val isPastTime = foreCastTimeStringToDate?.after(viewModel.stringToDate(currentTime,"MM-dd hh:mm a"))
    if (list == null) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
    }
    Column(
        modifier = Modifier
            .padding(5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        if (isPastTime!!) {
            //Date
            Text(
                text = foreCastTime.substringBefore(" ").replace("-","/"),
                textAlign = TextAlign.Center
            )
            //Time
            Text(
                text = foreCastTime.substringAfter(" "),
                textAlign = TextAlign.Center
            )
            AsyncImage(
                model = viewModel.getWeatherIcon(list.weather[0].icon),
                contentDescription = "Weather Icon",
                modifier = Modifier
                    .size(100.dp)
            )
            Row {
                Text(
                    text = list.main.temp.roundToInt().toString(),
                fontSize = 30.sp
                )
                Text(text = "o",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .offset(y = (-2).dp)
                )
                Text(
                    text = viewModel.unitLetter,
                fontSize = 30.sp
                )
            }
            //Check for Value been Forgot after Recomposition
            val rainTextColor: Color = if (isSystemInDarkTheme()){
                Color(173, 216, 230)
            }else{
                Color.Blue
            }
            val percentageOfPrecipitation = list.pop * 100
            if (list.weather[0].id in 200..622 ) {
                Text(
                    text = "$percentageOfPrecipitation%",
                color = rainTextColor
                )
            }
        }
    }
}














