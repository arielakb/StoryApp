package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.AppRepository
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.local.entity.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val appRepository: AppRepository
) : ViewModel() {

    fun getAllStories(token: String): LiveData<PagingData<Story>> =
        storyRepository.getAllStories(token).cachedIn(viewModelScope).asLiveData()

    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            appRepository.saveAuthToken(token)
        }
    }
}