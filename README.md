# Simon Dice 🟢🔴🟡🔵

> ⚠️ La mayoría del trabajo fue hecho en clase y guardando los cambios en local, es por eso que no tengo apenas commits subidos y no puedo mostrar el avance correctamente. Tenía otro repositorio creado de hace un mes con algunos commits y 2 ramas creadas. Aunque al final preferí crear uno nuevo y dejar todo más limpio. Te dejo el link del otro repositorio por si quieres comprobarlo igualmente: [Link al repositorio antigüo de SimonDice. ](https://github.com/pablokopa/simon_dicee)

## Índice de contenido

- [Introducción](#introducción)
- [Estructura básica del código](#estructura-básica-del-código)
- [Explicación general del código](#explicación-general-del-código)
    - [1. Uso del esquema MVVM)](#1-uso-del-esquema-mvvm)
    - [2. Uso del patrón Observer y corutinas](#2-uso-del-patron-observer-y-corutinas)
        - [Patrón Observer](#patrón-observer)
        - [Corutinas](#corutinas)
    - [3. Uso de Estados en el programa](#3-uso-de-estados-en-el-programa)
- [Funcionamiento del juego](#funcionamiento-del-juego)
- [Elementos que añadiría a futuro](#elementos-que-añadiría-a-futuro)


## Introducción
Este trabajo consiste en hacer el juego de **Simon Dice** utilizando **Kotlin** y **Jetpack Compose** desde **Android Studio**. Para ello debemos seguir varias pautas, como utilizar el **esquema de diseño MVVM**, el **patrón Observer y corutinas** y el uso de **Estados** para controlar los diferentes estados del juego...

## Estructura básica del código
Mi programa está dividido en las siguientes clases:

- **`MainActivity.kt`**: Aquí se inicializa el programa.
- **`ModelView.kt`**: Implementa toda la parte lógica del programa.
- **`IU.kt`**: En esta clase se definen las funciones @Composable para crear la interfaz gráfica.
- **`Datos.kt`**: En Datos se almacenan los datos compartidos entre todas las clases.

## Explicación general del código
### 1. Uso del esquema MVVM.
En este caso creo que sigo el patrón **MVVM** bastante bien:

1. **Model**: Representa los **datos de la aplicación**. En mi caso, la clase `Datos.kt` actúa como el **modelo**, almacenando datos globales del juego como el número de rondas y el color aleatorio actual.

2. **View**: Vendría siendo la **interfaz de usuario** que muestra los datos y envía las interacciones del usuario al ViewModel. En mi caso la clase `UI.kt`, junto con las funciones `@Composable` que hay en su interior como `UI`, `MostrarSecuencia`, `BotonColor`, y `BotonStart` representan la vista y definen la interfaz de usuario utilizando *Jetpack Compose*.

3. **ViewModel**: Finalmente utilizo `ModelView.kt` como el **ViewModel**. Esta maneja la lógica y actúa como intermediario entre el modelo y la vista. En mi código, la clase `ModelView` extiende `ViewModel` y contiene LiveData para el estado del juego, la secuencia de colores generada y el índice actual en la secuencia. Además, proporciona métodos para iniciar el juego, agregar colores a la secuencia y verificar si el color seleccionado por el usuario es correcto.

Este patrón de diseño es muy útil, ya que asegura una separación clara de las funcionalidades, facilitando el mantenimiento y la escalabilidad de la aplicación.

### 2. Uso del patron Observer y corutinas.
#### Patrón Observer
El patrón Observer lo utilizo a través de `LiveData` en la clase `ModelView.kt`.

`LiveData` permite que la vista observe los cambios en los datos y se actualice automáticamente cuando estos cambian.

Algunos ejemplos que utilizo en mi código:
- `estadoLiveData`: Observa el estado actual del juego.
- `secuenciaColores`: Observa la secuencia de colores generada.
- `indiceActual`: Observa el índice actual en la secuencia de colores.

Utilizando como ejemplo `estadoLiveData`:
```kotlin
// Declarar e inicializar un objeto MutableLiveData (con valor inicial en el estado de Inicio)
val estadoLiveData: MutableLiveData<Estados> = MutableLiveData(Estados.INICIO) // Estado actual del juego
```
Este se encargará de observar y cambiar los estados según sea necesario en cada momento del juego.

#### Corutinas
Las corutinas las utilizo en mi programa para manejar operaciones asincrónicas, como por ejemplo los delays entre la muestra de colores en la secuencia:

Ejemplos:
- En la función `mostrarSecuencia()` de la clase `ModelView.kt`:
    ```kotlin
    fun mostrarSecuencia() {
      viewModelScope.launch {
          estadoLiveData.value = Estados.GENERANDO
          for (color in secuenciaColores.value!!) {
              Datos.numeroRandom = color
              delay(1000) // Espera 1 segundo entre colores
          }
          estadoLiveData.value = Estados.ADIVINANDO
          indiceActual.value = 0
      }
    }
    ```


- En la función **@Composable** `MostrarSecuencia` en el archivo `UI.kt`:
    ```kotlin
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
    ```

### 3. Uso de Estados en el programa
El enum `Estados` lo utilizo para definir los diferentes estados del juego. Cada estado controla la activación del botón de Start y de los botones de colores, permitiendo gestionar la lógica del juego de manera más efectiva.

Los estados disponibles son:

- **INICIO**: Estado inicial del juego. El botón START está activo y los botones de colores están desactivados.
- **GENERANDO**: Estado en el que se genera la secuencia de colores. Tanto el botón START como los botones de colores están desactivados.
- **ADIVINANDO**: Estado en el que el jugador debe adivinar la secuencia de colores. El botón START está desactivado y los botones de colores están activos.

Estos estados permiten controlar el flujo del juego y asegurar que los botones se habiliten o deshabiliten en el momento adecuado, proporcionando una mejor experiencia de juego.

Aquí se muestra como se utilizan los estados, en el caso de que se quiera cambiar a un estado distinto:
```kotlin
// Utilizado para indicar que está en el estado inicial.
estadoLiveData.value = Estados.INICIO

// Utilizado para indicar que se está generando un nuevo número (color)
estadoLiveData.value = Estados.GENERANDO

// Utilizado para indicar que se está en la fase de adivinar los colores
estadoLiveData.value = Estados.ADIVINANDO
```
## Funcionamiento del juego
1. **Inicio del Juego**:
    - El juego comienza en el estado `INICIO`, donde solo el botón **START** está activo.
    - Al presionar el botón de **START**, se llama a la función `iniciarJuego()` en el `ViewModel`, que cambia el estado a `GENERANDO`, resetea la secuencia de colores y el contador de rondas, y agrega un nuevo color a la secuencia.

2. **Generación de la Secuencia**:
    - Ya en el estado `GENERANDO`, se muestra la secuencia de colores generada hasta el momento.
    - La función `mostrarSecuencia()` utiliza una corutina para manejar los delays entre la muestra de cada color.
    - Una vez mostrada la secuencia, el estado cambia a `ADIVINANDO`.

3. **Adivinanza de la Secuencia**:
    - En el estado `ADIVINANDO`, los botones de colores están activos y el jugador debe repetir la secuencia mostrada.
    - Al presionar un botón de color, se llama a la función `verificarColor()` en el `ViewModel`, que verifica si el color seleccionado es correcto o no.
    - Si el jugador acierta toda la secuencia, se incrementa el contador de rondas y se agrega un nuevo color a la secuencia, volviendo al estado `GENERANDO`.
    - Si el jugador se equivoca, el estado vuelve a `INICIO` y el juego se reinicia.

## Imagenes del funcionamiento
![image](https://github.com/user-attachments/assets/bc0b4617-0a64-47f8-81b5-a096c962eab3) ![image](https://github.com/user-attachments/assets/d5a318c3-7f78-4875-ade9-85cd15196f8c)

![image](https://github.com/user-attachments/assets/19861fee-75c8-451a-b3b9-6d35f9fd604e) ![image](https://github.com/user-attachments/assets/77b3602c-722f-467c-9b9b-2716b4c63a72)

1. La **imagen de arriba izquierda** muestra como es el juego antes de pulsar el botón de START (todos los botones bloqueados excepto el de START).
2. La **siguiente imagen (arriba derecha)**, muestra el juego ya iniciado, cuando pide que adivines el color (que en está caso es el azul (el color se enseña solamente durante 1 segundo)). Aquí ya está el botón de START bloqueado y solamente se pueden pulsar los botones de colores.
3. La **siguiente imagen (abajo izquierda)**, muestra que cuando se adivina el color nos manda un mensaje indicando que es correcto e inicia automáticamente la siguiente secuencia de colores.
4. La **última imágen (abajo derecha)**, muestra lo que ocurre cuando se falla: nos indica un mensaje de que es incorrecto y que hemos perdido, vuelve a desbloquear el botón START (para iniciar de nuevo el juego) y a bloquear los botones de colores. También muestra el número de rondas que hemos hecho hasta que se haga click de nuevo en el botón de START, lo que provocará que se reinicien las rondas y genere una nueva secuencia de colores.

## Elementos que añadiría a futuro
- Antes de nada me encargaría de pulir el juego actual para dejarlo perfectamente ya de base.
- Algo que me gustaría aladir sería un ranking de puntuaciones, donde se guarde la puntuación más alta y pueda ser superada como nuevo record.
- También me gustaría añadir más dinamismo al juego, hacer que tenga efectos visuales o sonidos para cada acción.
- Añadir tiempo límite junto con una barra de tiempo para hacerlo más visual.
