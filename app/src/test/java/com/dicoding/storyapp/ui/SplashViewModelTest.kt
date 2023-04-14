package com.dicoding.storyapp.ui

import com.dicoding.storyapp.data.AppRepository
import com.dicoding.storyapp.viewmodel.SplashViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SplashViewModelTest {


    @Mock
    private lateinit var appRepository: AppRepository
    private lateinit var splashViewModel: SplashViewModel

    private val dummyToken = "authentication_token"

    @Before
    fun setup() {
        splashViewModel = SplashViewModel(appRepository)
    }

    @Test
    fun `Get authentication token successfully`() = runTest {
        val expectedToken = flowOf(dummyToken)

        Mockito.`when`(splashViewModel.getAuthToken()).thenReturn(expectedToken)

        splashViewModel.getAuthToken().collect { actualToken ->
            Assert.assertNotNull(actualToken)
            Assert.assertEquals(dummyToken, actualToken)
        }

        Mockito.verify(appRepository).getAuthToken()
    }

    @Test
    fun `Get authentication token empty`() = runTest {
        val expectedToken = flowOf(null)

        Mockito.`when`(splashViewModel.getAuthToken()).thenReturn(expectedToken)

        splashViewModel.getAuthToken().collect { actualToken ->
            Assert.assertNull(actualToken)
        }

        Mockito.verify(appRepository).getAuthToken()
    }

}