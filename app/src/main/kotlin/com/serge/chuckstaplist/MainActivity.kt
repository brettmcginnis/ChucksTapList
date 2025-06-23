package com.serge.chuckstaplist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.serge.chuckstaplist.ui.circuit.ChucksCircuitApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ChucksCircuitApp(Modifier.fillMaxSize()) }
    }
}
