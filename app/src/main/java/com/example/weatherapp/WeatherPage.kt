package com.example.weatherapp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.WeatherModel

@Composable
fun WeatherPage(viewModel: WeatherViewModel) {
    var city by remember { mutableStateOf("") }
    val weatherResult = viewModel.weatherResult.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Gradient background
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF90CAF9), Color(0xFF1565C0))
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    value = city,
                    onValueChange = { city = it },
                    label = {
                        Text(
                            text = "Search for any location",
                            color = Color(0xFF1565C0),
                            modifier = Modifier.background(Color.White.copy(alpha = 0.7f))
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1565C0),
                        unfocusedBorderColor = Color(0xFF90CAF9),
                        focusedTextColor = Color(0xFF0D47A1),
                        unfocusedTextColor = Color(0xFF1976D2),
                        cursorColor = Color(0xFF1565C0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedLabelColor = Color(0xFF1565C0),
                        unfocusedLabelColor = Color(0xFF1565C0)
                    ),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Medium
                    )
                )

                IconButton(
                    onClick = {
                        viewModel.getData(city)
                        keyboardController?.hide()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF1565C0), shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search for any location",
                        tint = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        when (val result = weatherResult.value) {
            is NetworkResponse.Error -> {
                Text(
                    text = result.message,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }

            NetworkResponse.Loading -> {
                CircularProgressIndicator(color = Color(0xFF1565C0))
            }

            is NetworkResponse.Success<*> -> {
                WeatherDetails(data = result.data as WeatherModel)
            }

            null -> {}
        }
    }
}

@Composable
fun WeatherDetails(data: WeatherModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.97f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location Icon",
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF1565C0)
                )
                Text(
                    text = data.location.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = data.location.country,
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "${data.current.temp_c} °C",
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = Color(0xFF1565C0)
            )
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                modifier = Modifier
                    .size(140.dp)
                    .shadow(4.dp, RoundedCornerShape(70.dp)),
                model = "https:${data.current.condition.icon}".replace("64x64", "128x128"),
                contentDescription = "Condition icon"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.current.condition.text,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF90CAF9),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherKeyVal("Humidity", data.current.humidity.toString())
                        WeatherKeyVal("Wind Speed", "${data.current.wind_kph} km/h")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherKeyVal("UV", data.current.uv.toString())
                        WeatherKeyVal("Precipitation", "${data.current.precip_mm} mm")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherKeyVal("Local Time", data.location.localtime.split(" ")[1])
                        WeatherKeyVal("Local Date", data.location.localtime.split(" ")[0])
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherKeyVal(key: String, value: String) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(
                color = Color(0xFFE3F2FD),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1565C0)
        )
        Text(
            text = key,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1976D2),
            fontSize = 14.sp
        )
    }
}