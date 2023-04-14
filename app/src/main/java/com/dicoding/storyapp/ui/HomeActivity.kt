package com.dicoding.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.storyapp.adapter.LoadingStateAdapter
import com.dicoding.storyapp.adapter.StoryListAdapter
import com.dicoding.storyapp.data.local.entity.Story
import com.dicoding.storyapp.databinding.ActivityHomeBinding
import com.dicoding.storyapp.utils.animateVisibility
import com.dicoding.storyapp.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalPagingApi
class HomeActivity : Fragment() {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: StoryListAdapter

    private var token: String = ""
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ActivityHomeBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = requireActivity().intent.getStringExtra(MainActivity.EXTRA_TOKEN) ?: ""

        setSwipeRefreshLayout()
        setRecyclerView()
        getAllStories()

        binding?.fabCreateStory?.setOnClickListener {
            Intent(requireContext(), UploadActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAllStories() {
        homeViewModel.getAllStories(token).observe(viewLifecycleOwner) { result ->
            updateRecyclerViewData(result)
        }
    }

    private fun setSwipeRefreshLayout() {
        binding?.swipeRefresh?.setOnRefreshListener {
            getAllStories()
        }
    }

    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        listAdapter = StoryListAdapter()

        listAdapter.addLoadStateListener { loadState ->
            if ((loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && listAdapter.itemCount < 1) || loadState.source.refresh is LoadState.Error) {
                binding?.apply {
                    tvNotFoundError.animateVisibility(true)
                    ivNotFoundError.animateVisibility(true)
                    rvStories.animateVisibility(false)
                }
            } else {
                binding?.apply {
                    tvNotFoundError.animateVisibility(false)
                    ivNotFoundError.animateVisibility(false)
                    rvStories.animateVisibility(true)
                }
            }

            binding?.swipeRefresh?.isRefreshing = loadState.source.refresh is LoadState.Loading
        }

        try {
            recyclerView = binding?.rvStories!!
            recyclerView.apply {
                adapter = listAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        listAdapter.retry()
                    }
                )
                layoutManager = linearLayoutManager
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun updateRecyclerViewData(stories: PagingData<Story>) {
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()

        listAdapter.submitData(lifecycle, stories)

        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }
}