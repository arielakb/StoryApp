package com.dicoding.storyapp.data

import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.dicoding.storyapp.utils.CoroutinesTestRule
import com.dicoding.storyapp.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AppRepositoryTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var preferencesUser: PreferencesUser
    @Mock
    private lateinit var apiService: ApiService
    private lateinit var appRepository: AppRepository

    private val dummyName = "Name"
    private val dummyEmail = "mail@mail.com"
    private val dummyPassword = "password"
    private val dummyToken = "authentication_token"

    @Before
    fun setup() {
        appRepository = AppRepository(apiService, preferencesUser)
    }

    @Test
    fun `User login successfully`(): Unit = runTest {
        val expectedResponse = DataDummy.generateDummyLoginResponse()

        Mockito.`when`(apiService.userLogin(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        appRepository.userLogin(dummyEmail, dummyPassword).collect { result ->
            Assert.assertTrue(result.isSuccess)
            Assert.assertFalse(result.isFailure)

            result.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(expectedResponse, actualResponse)
            }

            result.onFailure {
                Assert.assertNull(it)
            }
        }

    }

    @Test
    fun `User login failed - throw exception`(): Unit = runTest {
        Mockito.`when`(apiService.userLogin(dummyEmail, dummyPassword)).then { throw Exception() }

        appRepository.userLogin(dummyEmail, dummyPassword).collect { result ->
            Assert.assertFalse(result.isSuccess)
            Assert.assertTrue(result.isFailure)

            result.onFailure {
                Assert.assertNotNull(it)
            }
        }
    }

    @Test
    fun `User register successfully`(): Unit = runTest {
        val expectedResponse = DataDummy.generateDummyRegisterResponse()

        Mockito.`when`(apiService.userRegister(dummyName, dummyEmail, dummyPassword)).thenReturn(
            expectedResponse
        )

        appRepository.userRegister(dummyName, dummyEmail, dummyPassword).collect { result ->
            Assert.assertTrue(result.isSuccess)
            Assert.assertFalse(result.isFailure)

            result.onSuccess { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(expectedResponse, actualResponse)
            }

            result.onFailure {
                Assert.assertNull(it)
            }
        }
    }

    @Test
    fun `User register failed - throw exception`(): Unit = runTest {
        Mockito.`when`(
            apiService.userRegister(
                dummyName,
                dummyEmail,
                dummyPassword
            )
        ).then { throw Exception() }

        appRepository.userRegister(dummyName, dummyEmail, dummyPassword).collect { result ->
            Assert.assertFalse(result.isSuccess)
            Assert.assertTrue(result.isFailure)

            result.onFailure {
                Assert.assertNotNull(it)
            }
        }
    }

    @Test
    fun `Save auth token successfully`() = runTest {
        appRepository.saveAuthToken(dummyToken)
        Mockito.verify(preferencesUser).saveAuthToken(dummyToken)
    }

    @Test
    fun `Get authentication token successfully`() = runTest {
        val expectedToken = flowOf(dummyToken)

        Mockito.`when`(preferencesUser.getAuthToken()).thenReturn(expectedToken)

        appRepository.getAuthToken().collect { actualToken ->
            Assert.assertNotNull(actualToken)
            Assert.assertEquals(dummyToken, actualToken)
        }

        Mockito.verify(preferencesUser).getAuthToken()
    }

}