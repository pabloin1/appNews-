package com.example.appnews.core.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.appnews.MainActivity
import com.example.appnews.R


class FCMService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private var token: String? = null

        // Método estático para acceder al token desde cualquier parte de la app
        fun getToken(): String? {
            return token
        }

        // Método para guardar el token manualmente (útil si lo obtienes de otra manera)
        fun setToken(newToken: String) {
            token = newToken
        }
    }

    // Se llama cuando se genera un nuevo token
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d(TAG, "Refreshed token: $newToken")

        // Guardar el token en la variable estática
        token = newToken

        // Guardar el token en SharedPreferences
        saveTokenToPrefs(newToken)

        // Si el usuario ya está registrado, actualiza el token en el servidor
        if (isUserLoggedIn()) {
            sendTokenToServer(newToken)
        }
    }

    // Se llama cuando se recibe un mensaje
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Verificar si el mensaje contiene datos
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title ?: "Notificación", it.body ?: "")
        }
    }

    // Método para guardar el token en SharedPreferences
    private fun saveTokenToPrefs(token: String) {
        val sharedPrefs = applicationContext.getSharedPreferences("FCMPrefs", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString("fcm_token", token)
            apply()
        }
    }

    // Método para enviar el token al servidor
    private fun sendTokenToServer(token: String) {
        // Implementa el código para enviar el token a tu servidor
        // Esto puede ser usando Retrofit, Volley, etc.
        Log.d(TAG, "Enviando token al servidor: $token")
        // ApiClient.updateFcmToken(token)
    }

    // Verificar si el usuario está logueado
    private fun isUserLoggedIn(): Boolean {
        // Implementa la lógica para verificar si el usuario está logueado
        val sharedPrefs = applicationContext.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.contains("auth_token")
    }

    // Método para manejar mensajes de datos
    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: return

        when (type) {
            "new_order" -> {
                val orderId = data["order_id"] ?: return
                // Realizar acciones específicas para nuevos pedidos
            }
            "order_assigned" -> {
                val orderId = data["order_id"] ?: return
                val courierName = data["courier_name"] ?: "Un repartidor"
                // Realizar acciones específicas para pedidos asignados
            }
            "order_completed" -> {
                val orderId = data["order_id"] ?: return
                // Realizar acciones específicas para pedidos completados
            }
        }
    }

    // Método para mostrar una notificación
    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.default_notification_channel_id)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Para Android Oreo y superior, es necesario crear un canal de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Canal de notificaciones de la aplicación",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}