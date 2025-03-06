package com.example.appnews.login.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnews.core.network.RetrofitHelper
import com.example.appnews.login.data.model.LoginRequest
import com.example.appnews.login.data.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val context: Context,
    private val loginRepository: LoginRepository = LoginRepository()
) : ViewModel() {

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _isButtonEnabled = MutableLiveData(false)
    val isButtonEnabled: LiveData<Boolean> = _isButtonEnabled

    private val _loginStatus = MutableLiveData<LoginStatus>()
    val loginStatus: LiveData<LoginStatus> = _loginStatus

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
        _isButtonEnabled.value = !(_email.value.isNullOrBlank() || _password.value.isNullOrBlank())
    }

    fun login() {
        viewModelScope.launch {
            val request = LoginRequest(
                email = _email.value ?: "",
                password = _password.value ?: ""
            )

            val result = loginRepository.login(request)

            if (result.isSuccess) {
                val userDTO = result.getOrNull()
                userDTO?.let { user ->
                    // Guardar el token de autorización
                    RetrofitHelper.saveAuthToken(context, user.token)
                    _loginStatus.value = LoginStatus.Success
                } ?: run {
                    _loginStatus.value = LoginStatus.Error("No se pudo obtener la información del usuario")
                }
            } else {
                _loginStatus.value = LoginStatus.Error(
                    result.exceptionOrNull()?.message ?: "Error de inicio de sesión"
                )
            }
        }
    }

    sealed class LoginStatus {
        object Success : LoginStatus()
        data class Error(val message: String) : LoginStatus()
    }
}