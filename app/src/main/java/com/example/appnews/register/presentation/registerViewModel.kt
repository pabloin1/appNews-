package com.example.appnews.register.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnews.register.data.model.CreateUserRequest
import com.example.appnews.register.data.repository.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerRepository: RegisterRepository
) : ViewModel() {

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _fcmToken = MutableLiveData("")
    val fcmToken: LiveData<String> = _fcmToken

    private val _isButtonEnabled = MutableLiveData(false)
    val isButtonEnabled: LiveData<Boolean> = _isButtonEnabled

    private val _registrationStatus = MutableLiveData<RegistrationStatus>()
    val registrationStatus: LiveData<RegistrationStatus> = _registrationStatus

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

    fun onFcmTokenReceived(token: String) {
        _fcmToken.value = token
    }

    private fun validateInputs() {
        _isButtonEnabled.value = !(_username.value.isNullOrBlank() || _email.value.isNullOrBlank() || _password.value.isNullOrBlank())
    }

    fun registerUser() {
        viewModelScope.launch {
            val request = CreateUserRequest(
                name = _username.value ?: "",
                email = _email.value ?: "",
                password = _password.value ?: "",
                fcmToken = _fcmToken.value ?: ""
            )

            val result = registerRepository.createUser(request)
            if (result.isSuccess) {
                _registrationStatus.value = RegistrationStatus.Success
            } else {
                _registrationStatus.value = RegistrationStatus.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    sealed class RegistrationStatus {
        object Success : RegistrationStatus()
        data class Error(val message: String) : RegistrationStatus()
    }
}