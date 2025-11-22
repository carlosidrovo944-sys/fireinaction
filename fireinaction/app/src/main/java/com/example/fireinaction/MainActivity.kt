package com.example.fireinaction

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity

// =================================================================================
// 1. DEFINICIONES DE LA LÓGICA DEL JUEGO
// Estas son las clases y funciones que definen CÓMO funciona el juego.
// =================================================================================

// --- Constantes del Juego ---
const val VIDA_JUGADOR_BASE = 250
const val VIDA_JEFE_ZOMBIE = 450

// --- Clases de Datos y Personajes ---
data class Arma(
    val nombre: String,
    val danio: Int,
    val esMitica: Boolean = false,
    val tieneExplosion: Boolean = false
) {
    fun disparar(objetivo: Personaje) {
        println("💥 ${this.nombre} dispara a ${objetivo.nombre}!")
        objetivo.recibirDano(this.danio)
    }
}

interface Personaje {
    val nombre: String
    var vida: Int
    val vidaMaxima: Int
    fun recibirDano(cantidad: Int)
    fun estaVivo(): Boolean
}

class Jugador(
    override val nombre: String,
    override val vidaMaxima: Int = VIDA_JUGADOR_BASE
) : Personaje {
    override var vida: Int = vidaMaxima
    var armaEquipada: Arma? = null

    override fun recibirDano(cantidad: Int) {
        vida -= cantidad
        if (vida < 0) vida = 0
        Log.d("GameLogic", "💀 $nombre recibe $cantidad de daño. Vida restante: $vida/$vidaMaxima")
    }

    override fun estaVivo(): Boolean = vida > 0

    fun equiparArma(arma: Arma) {
        this.armaEquipada = arma
        Log.d("GameLogic", "🛠️ $nombre ha equipado ${arma.nombre}.")
    }

    fun atacar(objetivo: Personaje) {
        armaEquipada?.disparar(objetivo)
            ?: Log.d("GameLogic", "¡Error! $nombre no tiene un arma equipada.")
    }
}

class Enemigo(
    override val nombre: String,
    override val vidaMaxima: Int
) : Personaje {
    override var vida: Int = vidaMaxima
    override fun recibirDano(cantidad: Int) {
        vida -= cantidad
        if (vida < 0) vida = 0
        Log.d("GameLogic", "🎯 $nombre (Enemigo) recibe $cantidad de daño. Vida restante: $vida/$vidaMaxima")
    }

    override fun estaVivo(): Boolean = vida > 0
}

// --- Funciones de Inicialización ---
fun crearArmas(): Map<String, Arma> {
    return mapOf(
        "famas" to Arma(nombre = "Famas", danio = 35),
        "ak47" to Arma(nombre = "AK-47", danio = 40),
        "m4a1" to Arma(nombre = "M4A1", danio = 30),
        "vector" to Arma(nombre = "Vector", danio = 25),
        "mp5" to Arma(nombre = "MP5", danio = 28),
        "mp40" to Arma(nombre = "MP40", danio = 27),
        "rayito" to Arma(nombre = "⚡ Rayito", danio = 70, esMitica = true, tieneExplosion = true),
        "thunder" to Arma(nombre = "💥 Thunder", danio = 90, esMitica = true, tieneExplosion = true)
    )
}

// =================================================================================
// 2. GAMEVIEW (EL MOTOR GRÁFICO DEL JUEGO)
// Dibuja el juego en la pantalla y gestiona el bucle principal (game loop).
// =================================================================================
class GameView(context: Context) : SurfaceView(context), Runnable {

    private var gameThread: Thread? = null
    @Volatile
    private var running = false
    private val paint = Paint()

    private lateinit var bitmap: Bitmap
    private var x = 100f
    private var y = 100f

    private val jugadorHenrik = Jugador("Henrik")
    // Opcional: Crear el jefe aquí para interactuar con él
    private val jefeGigante = Enemigo("Jefe Gigante", VIDA_JEFE_ZOMBIE)


    init {
        try {
            // Intenta cargar un ícono de la app como imagen del jugador.
            // En un juego real, aquí iría el nombre de tu imagen, ej: "soldado"
            val drawableId = context.resources.getIdentifier("ic_launcher_foreground", "drawable", context.packageName)
            val tempBitmap = BitmapFactory.decodeResource(context.resources, drawableId)
            bitmap = Bitmap.createScaledBitmap(tempBitmap, 150, 150, false) // Hacemos el personaje más grande

            jugadorHenrik.equiparArma(crearArmas()["ak47"]!!)
        } catch (e: Exception) {
            Log.e("GameView", "Error al cargar el bitmap: ${e.message}")
            bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888) // Crea un bitmap vacío si falla
        }

        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                resumeGame()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                pauseGame()
            }
        })
    }

    fun resumeGame() {
        if (gameThread == null) {
            running = true
            gameThread = Thread(this)
            gameThread?.start()
        }
    }

    fun pauseGame() {
        running = false
        try {
            gameThread?.join() // Espera a que el hilo termine de forma segura.
            gameThread = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun update() {
        // En este ejemplo, el movimiento se gestiona en onTouchEvent.
        // Aquí podríamos añadir lógica del enemigo, como moverse hacia el jugador.
    }

    private fun draw() {
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()
            canvas?.let {
                // 1. Fondo del juego (color oscuro)
                it.drawColor(Color.rgb(30, 30, 30))

                // 2. Dibujar personaje (Henrik) centrado en el toque
                if (::bitmap.isInitialized) {
                    it.drawBitmap(bitmap, x - bitmap.width / 2, y - bitmap.height / 2, paint)
                }

                // 3. Dibujar la información (UI)
                paint.color = Color.WHITE
                paint.textSize = 50f
                paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                it.drawText("Vida: ${jugadorHenrik.vida}/${jugadorHenrik.vidaMaxima}", 50f, 70f, paint)
                it.drawText("Arma: ${jugadorHenrik.armaEquipada?.nombre ?: "Ninguna"}", 50f, 140f, paint)

                holder.unlockCanvasAndPost(it)
            }
        }
    }

    override fun run() {
        val targetFPS = 60
        val targetTime = (1000 / targetFPS).toLong()

        while (running) {
            val startTime = System.nanoTime()

            update()
            draw()

            val timeMillis = (System.nanoTime() - startTime) / 1000000
            val waitTime = targetTime - timeMillis

            if (waitTime > 0) {
                Thread.sleep(waitTime)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // El personaje sigue el toque del dedo
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            x = event.x
            y = event.y
        }

        // Simula un disparo al tocar la pantalla
        if (event.action == MotionEvent.ACTION_DOWN) {
            // Atacamos al jefe que creamos antes
            if(jefeGigante.estaVivo()) {
                jugadorHenrik.atacar(jefeGigante)
            } else {
                Log.d("GameLogic", "El jefe ya ha sido derrotado.")
            }
        }

        return true
    }
}

// =================================================================================
// 3. MAINACTIVITY (PUNTO DE ENTRADA DE LA APP ANDROID)
// Inicia el GameView y gestiona el ciclo de vida de la aplicación.
// =================================================================================
class MainActivity : AppCompatActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameView = GameView(this)
        setContentView(gameView)

        // Manejo del botón "Atrás" para que no cierre la app bruscamente
        onBackPressedDispatcher.addCallback(this) {
            gameView.pauseGame()
            finish() // Cierra la actividad de forma ordenada
        }
    }

    override fun onResume() {
        super.onResume()
        gameView.resumeGame()
    }

    override fun onPause() {
        super.onPause()
        gameView.pauseGame()
    }
}
