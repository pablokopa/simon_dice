package com.example.simon_dice
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.simon_dice.ui.theme.Simon_diceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val miModelView: ModelView = ModelView()

        enableEdgeToEdge()
        setContent {
            Simon_diceTheme {
                UI(miModelView)
            }
        }
    }
}