package com.example.simon_dice
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay

/**
 * Composable para mostrar la secuencia de colores
 * @param secuencia La secuencia de colores para mostrar
 * @param estadoLiveData El estado actual del juego
 */
@Composable
fun MostrarSecuencia(secuencia: List<Int>, estadoLiveData: MutableLiveData<Estados>) {
    var colorActual by remember { mutableStateOf(Color.Gray) }

    // Se lanza cuando cambia la secuencia de colores o el estado del juego
    LaunchedEffect(secuencia, estadoLiveData.value) {
        if (estadoLiveData.value == Estados.GENERANDO) {
            for (color in secuencia) {
                colorActual = when (color) {
                    1 -> ColorBoton.ROJO.color
                    2 -> ColorBoton.VERDE.color
                    3 -> ColorBoton.AZUL.color
                    4 -> ColorBoton.AMARILLO.color
                    else -> Color.Gray
                }
                delay(1000) // Esperar 1 segundo entre colores
                colorActual = Color.Gray
                delay(500) // Esperar 0.5 segundos antes de mostrar el siguiente color
            }
            estadoLiveData.value = Estados.ADIVINANDO
        }
    }

    // Rectángulo donde se muestra la secuencia de colores
    Box(
        modifier = Modifier
            .size(340.dp, 50.dp) // Tamaño del rectángulo
            .background(colorActual)
            .border(2.dp, Color.DarkGray)
            .padding(8.dp)
    )
}

/**
 * Composable principal para la UI del juego
 * @param modelView El ViewModel del juego
 */
@Composable
fun UI(modelView: ModelView) {
    var _resultado by remember { mutableStateOf("") }
    var juegoIniciado by remember { mutableStateOf(false) }

    LaunchedEffect(modelView.secuenciaColores.value) {
        if (modelView.estadoLiveData.value == Estados.ADIVINANDO) {
            _resultado = "Adivina la secuencia"
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        // Mostrar el contador de rondas
        Text(
            text = "Rondas: ${Datos.numeroLiveData.value}",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        // Mostrar la secuencia de colores
        MostrarSecuencia(modelView.secuenciaColores.value ?: emptyList(), modelView.estadoLiveData)

        // Campo de texto para mostrar el resultado
        TextField(
            value = _resultado,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
                .border(2.dp, Color.DarkGray, RoundedCornerShape(6.dp))
        )

        // Filas de botones de colores
        listOf(
            listOf(ColorBoton.ROJO, ColorBoton.VERDE),
            listOf(ColorBoton.AZUL, ColorBoton.AMARILLO)
        ).forEach { coloresFilas ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                coloresFilas.forEach { colorBoton ->
                    BotonColor(
                        modelView = modelView,
                        color = colorBoton.color,
                        etiqueta = colorBoton.etiqueta,
                        forma = RoundedCornerShape(0.dp),
                        juegoIniciado = juegoIniciado
                    ) { valor ->
                        if (modelView.verificarColor(valor)) {
                            _resultado = "Correcto"
                        } else {
                            _resultado = "Incorrecto. Has perdido."
                            juegoIniciado = false
                            modelView.estadoLiveData.value = Estados.INICIO
                        }
                    }
                }
            }
        }

        // Fila para el botón de inicio
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            BotonStart(
                modelView = modelView,
                _activo = !juegoIniciado,
                onStartClick = {
                    modelView.iniciarJuego()
                    juegoIniciado = true
                    _resultado = ""
                }
            )
        }
    }
}

/**
 * Composable para los botones de colores
 * @param modelView El ViewModel del juego
 * @param color El color del botón
 * @param etiqueta La etiqueta del botón
 * @param forma La forma del botón
 * @param juegoIniciado Indica si el juego está iniciado
 * @param onColorClick Acción a realizar al hacer clic en el botón
 */
@Composable
fun BotonColor(
    modelView: ModelView,
    color: Color,
    etiqueta: String,
    forma: RoundedCornerShape,
    juegoIniciado: Boolean,
    onColorClick: (Int) -> Unit
) {
    val botonColor = if (juegoIniciado) color else Color.Gray
    Button(
        onClick = {
            val valor = when (etiqueta) {
                "Rojo" -> 1
                "Verde" -> 2
                "Azul" -> 3
                "Amarillo" -> 4
                else -> 0
            }
            onColorClick(valor)
            Log.d("::Random", "Botón $etiqueta presionado, valor: $valor")
        },
        enabled = juegoIniciado,
        colors = ButtonDefaults.buttonColors(
            containerColor = botonColor
        ),
        modifier = Modifier
            .padding(8.dp)
            .size(width = 170.dp, height = 170.dp)
            .border(
                width = 2.dp,
                color = Color.DarkGray,
                shape = forma
            )
            .background(botonColor)
            .shadow(elevation = 0.dp, shape = forma),
        shape = forma
    ) {
    }
}

/**
 * Composable para el botón de start
 * @param modelView El ViewModel del juego
 * @param _activo Indica si el botón está activo
 * @param onStartClick Acción a realizar al hacer clic en el botón de start
 */
@Composable
fun BotonStart(
    modelView: ModelView,
    _activo: Boolean,
    onStartClick: () -> Unit
) {
    Button(
        onClick = {
            onStartClick()
        },
        enabled = _activo,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (_activo) ColorBoton.START.color else Color.LightGray,
            disabledContainerColor = Color.LightGray,
            contentColor = Color.White,
            disabledContentColor = Color.Gray
        ),
        modifier = Modifier
            .padding(top = 20.dp)
            .size(width = 220.dp, height = 50.dp)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(ColorBoton.START.etiqueta, color = if (_activo) Color.White else Color.Gray, fontSize = 20.sp)
    }
}