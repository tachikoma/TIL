package com.example.composetutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTutorialTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White)
    ) {
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            item {
                mapViewItem(name)
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(360/335f)
                        .background(Color.Yellow)
                ) {
                    Text("2021.11.12")
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(360/120f)
                        .background(Color.Cyan)
                )
            }
        }
    }
}

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun mapViewItem(name: String) {
    Box {
        val mapView1 = rememberMapViewWithLifecycle()
        AndroidView(
            { mapView1 },
            modifier = Modifier.aspectRatio(1f)
        ) { mapView ->
            CoroutineScope(Dispatchers.Main).launch {
                val map = mapView.awaitMap()
                map.uiSettings.isZoomControlsEnabled = true

                val pickUp = LatLng(-35.016, 143.321)
                val destination = LatLng(-32.491, 147.309)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 6f))
                val markerOptions = MarkerOptions()
                    .title("Sydney Opera House")
                    .position(pickUp)
                map.addMarker(markerOptions)

                val markerOptionsDestination = MarkerOptions()
                    .title("Restaurant Hubert")
                    .position(destination)
                map.addMarker(markerOptionsDestination)

                map.addPolyline(
                    PolylineOptions().apply {
                        width(16f)
                        geodesic(true)
                        jointType(JointType.ROUND)
                    }.add(
                        pickUp,
                        LatLng(-34.747, 145.592),
                        LatLng(-34.364, 147.891),
                        LatLng(-33.501, 150.217),
                        LatLng(-32.306, 149.248),
                        destination
                    )
                )

            }
        }
        Text(
            name,
            Modifier
                .background(Color(0f, 0f, 0f, 0.6f))
                .fillMaxWidth()
                .height(38.dp)
                .padding(20.dp, 8.dp),
            color = Color.White,
            fontSize = TextUnit(dpToSp(14.dp).value, TextUnitType.Sp),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.W500,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTutorialTheme {
        Greeting("Android")
    }
}