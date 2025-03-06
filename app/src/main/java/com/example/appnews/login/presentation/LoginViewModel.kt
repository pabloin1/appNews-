package com.example.appnews.login.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

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
        validateForm()
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
        validateForm()
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        validateForm()
    }

    private fun validateForm() {
        _isButtonEnabled.value = !(_username.value.isNullOrBlank() || _email.value.isNullOrBlank())
    }


}
