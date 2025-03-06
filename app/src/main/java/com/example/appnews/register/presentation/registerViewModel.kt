package com.example.appnews.register.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnews.core.fcm.FCMUtils
import com.example.appnews.register.data.model.CreateUserRequest
import com.example.appnews.register.data.repository.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerRepository: RegisterRepository,
    private val context: Context // Add context to retrieve FCM token
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
        // Retrieve FCM token when ViewModel is created
        retrieveFCMToken()
    }

    private fun retrieveFCMToken() {
        FCMUtils.getToken(context) { token ->
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
                // If token is not available, try to retrieve it again
                retrieveFCMToken()
                _registrationStatus.value = RegistrationStatus.Error("FCM Token not available")
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
                    result.exceptionOrNull()?.message ?: "Unknown error"
                )
            }
        }
    }

    sealed class RegistrationStatus {
        object Success : RegistrationStatus()
        data class Error(val message: String) : RegistrationStatus()
    }
}