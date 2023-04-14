package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.dicoding.storyapp.data.AppRepository
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.remote.response.StoryUploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class UploadViewModel @Inject constructor(
    private val repository: AppRepository,
    private val storyRepository: StoryRepository

    ) : ViewModel() {

    fun getAuthToken(): Flow<String?> = repository.getAuthToken()

    suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Result<StoryUploadResponse>> =
        storyRepository.uploadImage(token, file, description, lat, lon)
}