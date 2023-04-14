package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

    suspend fun userLogin(email: String, password: String) = repository.userLogin(email, password)

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            repository.saveAuthToken(token)
        }
    }
}