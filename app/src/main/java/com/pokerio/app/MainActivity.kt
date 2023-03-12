package com.pokerio.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { MainActivityView() }
    }

    @Composable
    fun MainActivityView() {
        Card {
            var expanded by remember { mutableStateOf(false) }
            Column(Modifier.clickable { expanded = !expanded }) {
                Image(painterResource(R.drawable.ic_launcher_foreground), "example image")
                AnimatedVisibility(expanded) {
                    Text(
                        text = "Jetpack Compose",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}