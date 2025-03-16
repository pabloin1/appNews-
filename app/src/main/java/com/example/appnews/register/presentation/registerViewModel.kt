package com.example.appnews.register.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnews.register.data.model.CreateUserRequest
import com.example.appnews.register.data.repository.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerRepository: RegisterRepository,
    private val context: Context
) : ViewModel() {

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _fcmToken = MutableLiveData<String>()
    val fcmToken: LiveData<String> = _fcmToken

    private val _isButtonEnabled = MutableLiveData(false)
    val isButtonEnabled: LiveData<Boolean> = _isButtonEnabled

    private val _registrationStatus = MutableLiveData<RegistrationStatus>()
    val registrationStatus: LiveData<RegistrationStatus> = _registrationStatus

    init {
        // Solicitar FCM Token
        requestFCMToken()
    }

    private fun requestFCMToken() {
        // Implementa la lógica para obtener el FCM Token
        // Por ejemplo, usando FCMUtils
        com.example.appnews.core.fcm.FCMUtils.getToken(context) { token ->
            token?.let {
                _fcmToken.postValue(it)
            }
        }
    }

    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
        validateInputs()
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
        validateInputs()
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        validateInputs()
    }

    private fun validateInputs() {
        _isButtonEnabled.value = !(_username.value.isNullOrBlank() ||
                _email.value.isNullOrBlank() ||
                _password.value.isNullOrBlank() ||
                _fcmToken.value.isNullOrBlank())
    }

    fun registerUser() {
        viewModelScope.launch {
            val token = _fcmToken.value
            if (token.isNullOrBlank()) {
                // Si el token no está disponible, solicitar uno de nuevo
                requestFCMToken()
                _registrationStatus.value = RegistrationStatus.Error("Token FCM no disponible")
                return@launch
            }

            val request = CreateUserRequest(
                name = _username.value ?: "",
                email = _email.value ?: "",
                password = _password.value ?: "",
                fcmToken = token
            )

            val result = registerRepository.createUser(request)

            if (result.isSuccess) {
                _registrationStatus.value = RegistrationStatus.Success
            } else {
                _registrationStatus.value = RegistrationStatus.Error(
                    result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    sealed class RegistrationStatus {
        object Success : RegistrationStatus()
        data class Error(val message: String) : RegistrationStatus()
    }
}