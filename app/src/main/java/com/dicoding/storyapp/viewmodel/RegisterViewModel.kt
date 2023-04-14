package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

    suspend fun userRegister(name: String, email: String, password: String) = repository.userRegister(name, email, password)
}