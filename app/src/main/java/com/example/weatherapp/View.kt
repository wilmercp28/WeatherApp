package com.example.weatherapp

import android.os.Bundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        Text(
            text = viewModel.getTime(),
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Image(

            bitmap = viewModel.weatherIcon(), contentDescription = "",
            modifier = Modifier
                .size(200.dp),
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewModel.getDay(),
                fontSize = 60.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = viewModel.getMonth(),
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