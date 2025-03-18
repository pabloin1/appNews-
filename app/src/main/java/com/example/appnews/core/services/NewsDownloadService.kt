package com.example.appnews.core.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.appnews.MainActivity
import com.example.appnews.R
import com.example.appnews.core.data.local.appDatabase.DatabaseProvider
import com.example.appnews.core.data.local.news.repositories.NewsRepository
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class NewsDownloadService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val isDownloading = AtomicBoolean(false)
    private var currentProgress = 0
    private var totalItems = 0

    // Binder para servicios ligados
    private val binder = LocalBinder()

    // Interfaz para comunicación con actividades/fragmentos
    interface DownloadCallback {
        fun onProgressUpdate(progress: Int, total: Int)
        fun onDownloadComplete()
        fun onDownloadError(error: String)
    }

    // Lista de callbacks registrados
    private val callbacks = mutableListOf<DownloadCallback>()

    // Binder que proporciona acceso a este servicio
    inner class LocalBinder : Binder() {
        fun getService(): NewsDownloadService = this@NewsDownloadService

        // Registrar callback
        fun registerCallback(callback: DownloadCallback) {
            callbacks.add(callback)
        }

        // Desregistrar callback
        fun unregisterCallback(callback: DownloadCallback) {
            callbacks.remove(callback)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Servicio de descarga creado")
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "Servicio ligado")
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ${intent?.action}")

        when (intent?.action) {
            ACTION_START_DOWNLOAD -> {
                val newsJsonArray = intent.getStringArrayExtra(EXTRA_NEWS_IDS)
                if (newsJsonArray != null) {
                    startDownload(newsJsonArray.toList())
                }
            }
            ACTION_STOP_DOWNLOAD -> {
                stopDownload()
            }
        }

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Descargas de Noticias",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Canal para notificaciones de descarga de noticias"
                enableVibration(false)
                setSound(null, null)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotification(progress: Int, total: Int): Notification {
        // Intent para volver a la app
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        // Intent para cancelar la descarga
        val cancelIntent = Intent(this, NewsDownloadService::class.java).apply {
            action = ACTION_STOP_DOWNLOAD
        }
        val cancelPendingIntent = PendingIntent.getService(
            this,
            1,
            cancelIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Crear la notificación con barra de progreso
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Descargando noticias")
            .setContentText("Progreso: $progress de $total")
            .setSmallIcon(R.drawable.default_notification_channel_id)
            .setContentIntent(pendingIntent)
            .setProgress(total, progress, false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setOngoing(true)  // Hace que la notificación sea persistente
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancelar", cancelPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Descargando $progress de $total noticias para acceso offline"))
            .build()
    }

    private fun startDownload(newsIds: List<String>) {
        if (isDownloading.compareAndSet(false, true)) {
            Log.d(TAG, "Iniciando descarga de ${newsIds.size} noticias")

            // Inicia el servicio en primer plano con una notificación visible
            startForeground(NOTIFICATION_ID, getNotification(0, newsIds.size))

            totalItems = newsIds.size
            currentProgress = 0

            serviceScope.launch {
                try {
                    val repository = NewsRepository(DatabaseProvider.getDatabase(this@NewsDownloadService).newsDao())

                    // Para cada ID de noticia
                    newsIds.forEachIndexed { index, newsId ->
                        // Simular la descarga (en una app real, aquí obtendrías la noticia de la API)
                        Log.d(TAG, "Descargando noticia $newsId ($index de $totalItems)")

                        // Obtener la noticia (aquí solo actualizamos una existente)
                        val news = repository.getNewsById(newsId)
                        if (news != null) {
                            // Marcar como descargada
                            val updatedNews = news.copy(isDownloaded = true)
                            repository.updateNews(updatedNews)
                        } else {
                            Log.e(TAG, "Noticia no encontrada: $newsId")
                        }

                        // Actualizar progreso
                        currentProgress = index + 1
                        updateProgress(currentProgress, totalItems)

                        // Simular tiempo de descarga
                        delay(500)
                    }

                    // Completado
                    Log.d(TAG, "Descarga completada")
                    showCompletionNotification(totalItems)
                    onDownloadComplete()

                } catch (e: Exception) {
                    Log.e(TAG, "Error en la descarga", e)
                    showErrorNotification(e.message ?: "Error desconocido")
                    onDownloadError("Error en la descarga: ${e.message}")
                } finally {
                    isDownloading.set(false)
                    stopForeground(true)
                    stopSelf()
                }
            }
        } else {
            Log.d(TAG, "Ya hay una descarga en progreso")
        }
    }

    private fun showCompletionNotification(total: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Descarga completada")
            .setContentText("$total noticias descargadas correctamente")
            .setSmallIcon(R.drawable.default_notification_channel_id)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_COMPLETE_ID, notification)
    }

    private fun showErrorNotification(errorMessage: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Error en la descarga")
            .setContentText(errorMessage)
            .setSmallIcon(R.drawable.default_notification_channel_id)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_ERROR_ID, notification)
    }

    private fun stopDownload() {
        if (isDownloading.compareAndSet(true, false)) {
            Log.d(TAG, "Deteniendo descarga")
            serviceScope.coroutineContext.cancelChildren()
            stopForeground(true)
            stopSelf()
        }
    }

    private fun updateProgress(progress: Int, total: Int) {
        // Actualizar la notificación de progreso
        val notification = getNotification(progress, total)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)

        // Notificar a los callbacks (solo para la interacción con la UI si está abierta)
        callbacks.forEach { it.onProgressUpdate(progress, total) }
    }

    private fun onDownloadComplete() {
        callbacks.forEach { it.onDownloadComplete() }
    }

    private fun onDownloadError(error: String) {
        callbacks.forEach { it.onDownloadError(error) }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Servicio destruido")
        serviceScope.cancel()
    }

    companion object {
        private const val TAG = "NewsDownloadService"
        private const val CHANNEL_ID = "download_channel"
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_COMPLETE_ID = 2
        private const val NOTIFICATION_ERROR_ID = 3

        const val ACTION_START_DOWNLOAD = "com.example.appnews.ACTION_START_DOWNLOAD"
        const val ACTION_STOP_DOWNLOAD = "com.example.appnews.ACTION_STOP_DOWNLOAD"
        const val EXTRA_NEWS_IDS = "news_ids"

        // Método helper para iniciar el servicio
        fun startDownload(context: Context, newsIds: List<String>) {
            val intent = Intent(context, NewsDownloadService::class.java).apply {
                action = ACTION_START_DOWNLOAD
                putExtra(EXTRA_NEWS_IDS, newsIds.toTypedArray())
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        // Método helper para detener el servicio
        fun stopDownload(context: Context) {
            val intent = Intent(context, NewsDownloadService::class.java).apply {
                action = ACTION_STOP_DOWNLOAD
            }
            context.startService(intent)
        }
    }
}