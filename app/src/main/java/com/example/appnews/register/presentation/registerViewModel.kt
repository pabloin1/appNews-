package com.example.appnews.register.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _isButtonEnabled = MutableLiveData(false)
    val isButtonEnabled: LiveData<Boolean> = _isButtonEnabled

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
        _isButtonEnabled.value = !(_username.value.isNullOrBlank() || _email.value.isNullOrBlank() || _password.value.isNullOrBlank())
    }
}
