package com.bimabagaskhoro.phincon.core.remote

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.bimabagaskhoro.phincon.core.data.pref.AuthPreferences
import com.bimabagaskhoro.phincon.core.data.source.repository.remote.RemoteRepositoryImpl
import com.bimabagaskhoro.phincon.core.utils.CoroutinesTestRule
import com.bimabagaskhoro.phincon.core.utils.DataDummy
import com.bimabagaskhoro.phincon.core.utils.Resource
import com.bimabagaskhoro.phincon.core.vm.RemoteViewModel
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
class RemoteViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var remoteRepository: RemoteRepositoryImpl

    @Mock
    private lateinit var dataPreference: AuthPreferences

    private lateinit var remoteViewModel: RemoteViewModel

    @Before
    fun setUp() {
        remoteViewModel = RemoteViewModel(remoteRepository, dataPreference)
    }

    @Test
    fun `Login Success`() = runTest {
        val result = flowOf(Resource.Success(DataDummy.generateDummyLogin()))
        Mockito.`when`(
            remoteRepository.login(
                "email", "password", "token"
            )
        ).thenReturn(result)

        remoteViewModel.login("email", "password", "token").observeForever { actualData ->
            Assert.assertNotNull(actualData)
            Assert.assertTrue(actualData is Resource.Success)
            Assert.assertEquals(
                DataDummy.generateDummyLogin().success.data_user,
                (actualData as Resource.Success).data?.success?.data_user
            )
        }

        Mockito.verify(remoteRepository).login("email", "password", "token")
    }

    @Test
    fun `Login Loading`() = runTest {
        val result = flowOf(Resource.Loading(null))
        Mockito.`when`(
            remoteRepository.login(
                "email", "password", "token"
            )
        ).thenReturn(result)

        remoteViewModel.login("email", "password", "token").observeForever { actualData ->
            Assert.assertTrue(actualData is Resource.Loading)
            Assert.assertNotNull(actualData)
        }

        Mockito.verify(remoteRepository).login("email", "password", "token")
    }

    @Test
    fun `Login Error`() = runTest {
        val result = flowOf(Resource.Error("", null, null))
        Mockito.`when`(
            remoteRepository.login(
                "email", "password", "token"
            )
        ).thenReturn(result)

        remoteViewModel.login("email", "password", "token").observeForever { actualData ->
            Assert.assertTrue(actualData is Resource.Error)
            Assert.assertNotNull(actualData)
        }

        Mockito.verify(remoteRepository).login("email", "password", "token")
    }

    @Test
    fun `Login Empty`() = runTest {
        val result = flowOf(Resource.Empty())
        Mockito.`when`(
            remoteRepository.login(
                "email", "password", "token"
            )
        ).thenReturn(result)

        remoteViewModel.login("email", "password", "token").observeForever { actualData ->
            Assert.assertTrue(actualData is Resource.Empty)
            Assert.assertNotNull(actualData)
        }

        Mockito.verify(remoteRepository).login("email", "password", "token")
    }

}