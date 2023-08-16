package com.example.weatherapp


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.ui.theme.WeatherAppTheme
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
    val localDensity = LocalDensity.current
    var columnWidthFloat by remember {
        mutableStateOf(0f)
    }
    var forecastBoxWidth by remember {
        mutableStateOf(0f)
    }
    var temperaturesList by remember { mutableStateOf(MutableList(40) { 0.0 }) }
    val context = LocalContext.current
    viewModel.init(context)
    DisposableEffect(Unit) {
        viewModel.fetchGeoData(context)
        onDispose { /* Clean up if needed */ }
}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(40.dp)
            .verticalScroll(rememberScrollState()),
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
                .onGloballyPositioned { coordinates ->
                    columnWidthFloat = coordinates.size.width.toFloat()
                }
                .align(Alignment.CenterHorizontally)
        ) {
            Row {
                if (viewModel.foreCastData.value != null) {
                    for (i in 0 until 40) {
                        Box(
                            modifier = Modifier
                                .onGloballyPositioned { coordinates ->
                                    forecastBoxWidth = coordinates.size.width.toFloat()
                                }
                        ){
                            ForeCast(viewModel, viewModel.foreCastData.value?.list?.get(i))
                        }
                        temperaturesList[i] = viewModel.foreCastData.value?.list?.get(i)?.main?.temp ?: 0.0
                    }
                }
            }
            TemperatureGraph(temperaturesList,columnWidthFloat,forecastBoxWidth)
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




@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ZipCodeTextField(viewModel: ViewModel, context: Context) {
    val keyboardController = LocalSoftwareKeyboardController.current
        TextField(
            value = viewModel.zipCode.toString(),
            onValueChange = { viewModel.zipCode = it },
            modifier = Modifier
                .width(200.dp),
            label = {
                Text(text = "Zip Code",)
                    },
            placeholder = {
                if (!viewModel.isValidZipCode) {
                    Text(text = "  Invalid Zip Code",)
                } else {
                    Text(text = "  Enter Your Zip Code",)
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
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
    }
    Column(
        modifier = Modifier
            .padding(5.dp)
            .border(1.dp, MaterialTheme.colorScheme.onPrimary, RectangleShape),
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
            val percentageOfPrecipitation = rememberSaveable{list.pop * 100}
            if (list.weather[0].id in 200..622 ) {
                Text(
                    text = "$percentageOfPrecipitation%",
                color = rainTextColor
                )
            }
        }
    }
}

@Composable
fun TemperatureGraph(temperatures: List<Double>, columnWidthFloat: Float, forecastBoxWidth: Float){
    val maxValue = temperatures.maxOrNull() ?: 0.0
    val minValue = temperatures.minOrNull() ?: 0.0 // Added to get the minimum value
    val valueRange = (maxValue + 30).roundToInt() - minValue
    Log.d("ddddd",forecastBoxWidth.toString())
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val canvasHeight = size.height
        val pointCount = temperatures.size

        if (pointCount > 1) {
            val xStep = columnWidthFloat / (pointCount - 1)
            val path = androidx.compose.ui.graphics.Path()
            path.moveTo(
                forecastBoxWidth,
                canvasHeight - ((temperatures[0] - minValue) / valueRange).toFloat() * canvasHeight
            )
            for ((index, temperature) in temperatures.withIndex()) {
                val x = 200 + index * xStep
                val y = canvasHeight - ((temperature - minValue) / valueRange).toFloat() * canvasHeight
                path.lineTo(x, y)
                drawLine(
                    color = Color.Gray,
                    start = Offset(x, canvasHeight),
                    end = Offset(x, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
            drawPath(
                path = path,
                color = Color.Blue,
                style = Stroke(width = 2.dp.toPx())
            )

            }
        }
    }
@Composable
fun WeatherDetails(viewModel: ViewModel){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
    ) {
        Text(text = "Details", fontSize = 30.sp)
        Row {
            //Right Side Colum
            Column {
                Text(text = "Humidity", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Text(text = viewModel.weatherData.value?.current?.humidity.toString()+"%", fontSize = 20.sp)
            }
        }
    }
}














