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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import com.google.android.gms.maps.CameraUpdateFactory
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

    val mapView = rememberMapViewWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White)
    ) {
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            item {
                AndroidView({ mapView }, modifier = Modifier./*fillParentMaxHeight()*/height(360.dp)) { mapView ->
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
                            PolylineOptions().add(
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
            }
            item {
                Box (modifier = Modifier.fillMaxWidth().height(335.dp).background(Color.Yellow)) {
                    Text("2021.11.12")
                }
            }
            item {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.Cyan))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTutorialTheme {
        Greeting("Android")
    }
}