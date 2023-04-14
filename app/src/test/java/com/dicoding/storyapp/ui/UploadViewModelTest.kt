package com.dicoding.storyapp.ui

import androidx.paging.ExperimentalPagingApi
import com.dicoding.storyapp.data.AppRepository
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.remote.response.StoryUploadResponse
import com.dicoding.storyapp.utils.DataDummy
import com.dicoding.storyapp.viewmodel.UploadViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UploadViewModelTest {

    @Mock
    private lateinit var appRepository: AppRepository

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var uploadViewModel: UploadViewModel

    private val dummyToken = "authentication_token"
    private val dummyUploadResponse = DataDummy.generateDummyFileUploadResponse()
    private val dummyMultipart = DataDummy.generateDummyMultipartFile()
    private val dummyDescription = DataDummy.generateDummyRequestBody()

    @Before
    fun setup() {
        uploadViewModel = UploadViewModel(appRepository, storyRepository)
    }

    @Test
    fun `Get authentication token successfully`() = runTest {
        val expectedToken = flowOf(dummyToken)

        Mockito.`when`(uploadViewModel.getAuthToken()).thenReturn(expectedToken)

        uploadViewModel.getAuthToken().collect { actualToken ->
            Assert.assertNotNull(actualToken)
            Assert.assertEquals(dummyToken, actualToken)
        }

        Mockito.verify(appRepository).getAuthToken()
        Mockito.verifyNoInteractions(storyRepository)
    }

    @Test
    fun `Get authentication token successfully but null`() = runTest {
        val expectedToken = flowOf(null)

        Mockito.`when`(uploadViewModel.getAuthToken()).thenReturn(expectedToken)

        uploadViewModel.getAuthToken().collect { actualToken ->
            Assert.assertNull(actualToken)
        }

        Mockito.verify(appRepository).getAuthToken()
        Mockito.verifyNoInteractions(storyRepository)
    }

    @Test
    fun `Upload file successfully`() = runTest {
        val expectedResponse = flowOf(Result.success(dummyUploadResponse))

        Mockito.`when`(
            uploadViewModel.uploadImage(
                dummyToken,
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        ).thenReturn(expectedResponse)

        uploadViewModel.uploadImage(dummyToken, dummyMultipart, dummyDescription, null, null)
            .collect { result ->

                Assert.assertTrue(result.isSuccess)
                Assert.assertFalse(result.isFailure)

                result.onSuccess { actualResponse ->
                    Assert.assertNotNull(actualResponse)
                    Assert.assertSame(dummyUploadResponse, actualResponse)
                }
            }

        Mockito.verify(storyRepository)
            .uploadImage(dummyToken, dummyMultipart, dummyDescription, null, null)
        Mockito.verifyNoInteractions(appRepository)
    }

    @Test
    fun `Upload file failed`(): Unit = runTest {
        val expectedResponse: Flow<Result<StoryUploadResponse>> =
            flowOf(Result.failure(Exception("failed")))

        Mockito.`when`(
            uploadViewModel.uploadImage(
                dummyToken,
                dummyMultipart,
                dummyDescription,
                null,
                null
            )
        ).thenReturn(expectedResponse)

        uploadViewModel.uploadImage(dummyToken, dummyMultipart, dummyDescription, null, null)
            .collect { result ->
                Assert.assertFalse(result.isSuccess)
                Assert.assertTrue(result.isFailure)

                result.onFailure { actualResponse ->
                    Assert.assertNotNull(actualResponse)
                }
            }

    }
}