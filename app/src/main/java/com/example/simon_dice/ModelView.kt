package com.example.simon_dice
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar el estado del juego y la lógica de la secuencia de colores
 */
class ModelView : ViewModel() {
    val TAG_LOG: String = "::miDebug" // Tag para logcat
    val estadoLiveData: MutableLiveData<Estados> = MutableLiveData(Estados.INICIO) // Estado actual del juego
    val secuenciaColores: MutableLiveData<List<Int>> = MutableLiveData(emptyList()) // Secuencia de colores generada
    val indiceActual: MutableLiveData<Int> = MutableLiveData(0) // Índice actual en la secuencia de colores

    init {
        Log.d(TAG_LOG, "Inicializamos ViewModel - Estado: ${estadoLiveData.value}")
    }

    /**
     * Inicia el juego (también resetea la secuencia de colores y el contador de rondas)
     */
    fun iniciarJuego() {
        estadoLiveData.value = Estados.GENERANDO
        Log.d(TAG_LOG, "Estado cambiado a: ${estadoLiveData.value}")
        secuenciaColores.value = emptyList()
        Datos.numeroLiveData.value = 0 // Reinicia el contador de rondas
        agregarColorSecuencia()
    }

    /**
     * Agrega un color aleatorio a la secuencia de colores
     */
    fun agregarColorSecuencia() {
        val nuevaSecuencia = secuenciaColores.value!!.toMutableList()
        nuevaSecuencia.add(Random.nextInt(1, 5))
        secuenciaColores.value = nuevaSecuencia
        mostrarSecuencia()
    }

    /**
     * Muestra la secuencia de colores generada
     * Utiliza una corutina para manejar los delays entre colores
     */
    fun mostrarSecuencia() {
        viewModelScope.launch {
            estadoLiveData.value = Estados.GENERANDO
            Log.d(TAG_LOG, "Estado cambiado a: ${estadoLiveData.value}")
            for (color in secuenciaColores.value!!) {
                Datos.numeroRandom = color
                Log.d(TAG_LOG, "Mostrando color: $color")
                delay(1000) // Espera 1 segundo entre colores
            }
            estadoLiveData.value = Estados.ADIVINANDO
            Log.d(TAG_LOG, "Estado cambiado a: ${estadoLiveData.value}")
            indiceActual.value = 0
        }
    }

    /**
     * Verifica si el color seleccionado por el usuario es correcto
     * @param color El color seleccionado por el usuario
     * @return true si el color es correcto, false en caso contrario
     */
    fun verificarColor(color: Int): Boolean {
        if (estadoLiveData.value != Estados.ADIVINANDO) return false

        return if (color == secuenciaColores.value!![indiceActual.value!!]) {
            indiceActual.value = indiceActual.value!! + 1
            if (indiceActual.value == secuenciaColores.value!!.size) {
                Datos.numeroLiveData.value = Datos.numeroLiveData.value!! + 1 // Incrementa el contador de rondas
                agregarColorSecuencia()
            }
            true
        } else {
            estadoLiveData.value = Estados.INICIO
            Log.d(TAG_LOG, "Estado cambiado a: ${estadoLiveData.value}")
            false
        }
    }
}