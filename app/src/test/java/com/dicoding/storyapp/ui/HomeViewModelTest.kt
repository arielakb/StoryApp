package com.dicoding.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storyapp.adapter.StoryListAdapter
import com.dicoding.storyapp.data.local.entity.Story
import com.dicoding.storyapp.utils.CoroutinesTestRule
import com.dicoding.storyapp.utils.DataDummy
import com.dicoding.storyapp.utils.PagedTestDataSource
import com.dicoding.storyapp.utils.getOrAwaitValue
import com.dicoding.storyapp.viewmodel.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var homeViewModel: HomeViewModel

    private val dummyToken = "authentication_token"

    @Test
    fun `Get all stories successfully`() = runTest {
        val dummyStories = DataDummy.generateDummyListStory()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        Mockito.`when`(homeViewModel.getAllStories(dummyToken)).thenReturn(stories)

        val actualStories = homeViewModel.getAllStories(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )
        differ.submitData(actualStories)

        advanceUntilIdle()

        Mockito.verify(homeViewModel).getAllStories(dummyToken)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
    }


    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}