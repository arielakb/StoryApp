package com.dicoding.storyapp.ui

import com.dicoding.storyapp.data.AppRepository
import com.dicoding.storyapp.data.remote.response.RegisterResponse
import com.dicoding.storyapp.utils.DataDummy
import com.dicoding.storyapp.viewmodel.RegisterViewModel
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

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @Mock
    private lateinit var appRepository: AppRepository
    private lateinit var registerViewModel: RegisterViewModel

    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyName = "Full Name"
    private val dummyEmail = "email@mail.com"
    private val dummyPassword = "password"

    @Before
    fun setup() {
        registerViewModel = RegisterViewModel(appRepository)
    }

    @Test
    fun `Registration successfully - result success`(): Unit = runTest {
        val expectedResponse = flowOf(Result.success(dummyRegisterResponse))

        Mockito.`when`(registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )

        registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword).collect { response ->

            Assert.assertTrue(response.isSuccess)
            Assert.assertFalse(response.isFailure)

            response.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertSame(dummyRegisterResponse, actualResponse)
            }
        }

        Mockito.verify(appRepository).userRegister(dummyName, dummyEmail, dummyPassword)
    }

    @Test
    fun `Registration failed - result with exception`(): Unit = runTest {
        val expectedResponse: Flow<Result<RegisterResponse>> =
            flowOf(Result.failure(Exception("failed")))

        Mockito.`when`(registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )

        registerViewModel.userRegister(dummyName, dummyEmail, dummyPassword).collect { response ->

            Assert.assertFalse(response.isSuccess)
            Assert.assertTrue(response.isFailure)

            response.onFailure {
                Assert.assertNotNull(it)
            }
        }
    }

}