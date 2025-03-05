package com.example.appnews.core.fcm

import android.content.Context
import android.util.Log
import com.example.appnews.core.fcm.FCMService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


object FCMUtils {
    private const val TAG = "FCMUtils"

    /**
     * Obtiene el token FCM actual o solicita uno nuevo si no existe.
     * @param context Contexto de la aplicación
     * @param callback Función a ejecutar cuando se obtenga el token
     */
    fun getToken(context: Context, callback: (String?) -> Unit) {
        // Verificar si ya tenemos el token en memoria
        FCMService.getToken()?.let {
            callback(it)
            return
        }

        // Verificar si el token está guardado en SharedPreferences
        val sharedPrefs = context.getSharedPreferences("FCMPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPrefs.getString("fcm_token", null)

        if (savedToken != null) {
            // Guardar en la variable estática y devolver
            FCMService.setToken(savedToken)
            callback(savedToken)
            return
        }

        // Si no tenemos el token, solicitarlo a Firebase
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                callback(null)
                return@OnCompleteListener
            }

            // Obtener el nuevo token
            val token = task.result

            // Guardar el token en SharedPreferences
            with(sharedPrefs.edit()) {
                putString("fcm_token", token)
                apply()
            }

            // Guardar en la variable estática
            FCMService.setToken(token)

            Log.d(TAG, "FCM Token: $token")
            callback(token)
        })
    }

    /**
     * Suscribe al dispositivo a un tema específico para recibir notificaciones.
     * @param topic Nombre del tema
     * @param callback Función a ejecutar al completar la suscripción
     */
    fun subscribeTopic(topic: String, callback: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Suscrito al tema: $topic")
                    callback(true)
                } else {
                    Log.e(TAG, "Error al suscribirse al tema: $topic", task.exception)
                    callback(false)
                }
            }
    }

    /**
     * Cancela la suscripción a un tema.
     * @param topic Nombre del tema
     * @param callback Función a ejecutar al completar la desuscripción
     */
    fun unsubscribeTopic(topic: String, callback: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Desuscrito del tema: $topic")
                    callback(true)
                } else {
                    Log.e(TAG, "Error al desuscribirse del tema: $topic", task.exception)
                    callback(false)
                }
            }
    }
}