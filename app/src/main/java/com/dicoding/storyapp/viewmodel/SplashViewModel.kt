package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {

    fun getAuthToken(): Flow<String?> = repository.getAuthToken()
}