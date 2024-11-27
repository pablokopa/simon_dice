package com.example.simon_dice
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData

/**
 * Objeto para almacenar datos globales del juego
 */
object Datos {
    var numeroRandom: Int = 0 // Almacena el número random
    var numeroLiveData: MutableLiveData<Int> = MutableLiveData(0) // LiveData para el número de rondas
}

/**
 * Enum para definir los colores de los botones y sus etiquetas
 */
enum class ColorBoton(val color: Color, val etiqueta: String) {
    AZUL(Color(0xFF10ACFF), "Azul"),
    ROJO(Color(0xFFFF3D3D), "Rojo"),
    VERDE(Color(0xFF27EB00), "Verde"),
    AMARILLO(Color(0xFFFCFF3C), "Amarillo"),
    START(Color(0xFF3A3A3A), "START")
}

/**
 * Enum para definir los estados del juego
 */
enum class Estados(val start_activo: Boolean, val boton_activo: Boolean) {
    INICIO(start_activo = true, boton_activo = false),
    GENERANDO(start_activo = false, boton_activo = false),
    ADIVINANDO(start_activo = false, boton_activo = true),
}