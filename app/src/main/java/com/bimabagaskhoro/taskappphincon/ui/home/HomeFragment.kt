package com.bimabagaskhoro.taskappphincon.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bimabagaskhoro.taskappphincon.databinding.FragmentHomeBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.DetailActivity
import com.bimabagaskhoro.taskappphincon.ui.adapter.paging.LoadPagingAdapter
import com.bimabagaskhoro.taskappphincon.ui.adapter.paging.ProductAdapter
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.INITIAL_INDEX
import com.bimabagaskhoro.taskappphincon.utils.hideKeyboard
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var adapterProduct: ProductAdapter
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchingKey()
        initRecyclerView()
        setViewModel(null)
        initSwipeRefresh()
    }

    private fun initSearchingKey() {
        binding.edtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard(requireActivity())
                return false
            }

            override fun onQueryTextChange(q: String?): Boolean {
                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    q?.let {
                        delay(2000)
                        if (q?.length == 0 || q.toString() == "") {
                            setViewModel(null)
                            clearFragmentResult(q)
                        } else {
                            setViewModel(q)
                            clearFragmentResult(q)
                        }
                    }
                }
                return false
            }
        })
    }

    private fun initRecyclerView() {
        adapterProduct = ProductAdapter()
        adapterProduct.onClick = {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_DATA_DETAIL, it.id)
            startActivity(intent)
        }

        binding.rvProduct.apply {
            adapterProduct.addLoadStateListener { loadState ->
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            }
            adapter = adapterProduct.withLoadStateFooter(
                footer = LoadPagingAdapter { adapterProduct.retry() }
            )
        }
    }

    private fun setViewModel(q: String?) {
        if (q.toString().isNotEmpty()) {
            lifecycleScope.launch {
                viewModel.getProduct(q).collectLatest { data ->
                    adapterProduct.submitData(data)
                }
            }
            lifecycleScope.launch{
                viewModel.getProduct(q).collectLatest {
                    adapterProduct.loadStateFlow.collectLatest { load ->
                        binding.apply {
                            when (load.refresh) {
                                is LoadState.Loading -> {
                                    progressBar.visibility = View.VISIBLE
                                    rvProduct.visibility = View.GONE
                                    viewEmptyDatas.root.visibility = View.GONE
                                }
                                is LoadState.NotLoading -> {
                                    if (adapterProduct.itemCount == 0) {
                                        viewEmptyDatas.root.visibility = View.VISIBLE
                                    } else {
                                        progressBar.visibility = View.GONE
                                        rvProduct.visibility = View.VISIBLE
                                        binding.swipeRefresh.isRefreshing = false
                                        viewEmptyDatas.root.visibility = View.GONE
                                    }
                                }
                                is LoadState.Error -> {
                                    if (adapterProduct.itemCount == INITIAL_INDEX) {
                                        progressBar.visibility = View.GONE
                                        viewEmptyDatas.root.visibility = View.VISIBLE
                                    } else {
                                        progressBar.visibility = View.GONE
                                        viewEmptyDatas.root.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            lifecycleScope.launch {
                viewModel.getProduct(null).collectLatest { data ->
                    adapterProduct.submitData(data)
                }
                viewModel.getProduct(null).collectLatest {
                    adapterProduct.loadStateFlow.collectLatest { load ->
                        binding.apply {
                            when (load.refresh) {
                                is LoadState.Loading -> {
                                    progressBar.visibility = View.VISIBLE
                                    rvProduct.visibility = View.GONE
                                    viewEmptyDatas.root.visibility = View.GONE
                                }
                                is LoadState.NotLoading -> {
                                    if (adapterProduct.itemCount == 0) {
                                        viewEmptyDatas.root.visibility = View.VISIBLE
                                    } else {
                                        progressBar.visibility = View.GONE
                                        rvProduct.visibility = View.VISIBLE
                                        binding.swipeRefresh.isRefreshing = false
                                        viewEmptyDatas.root.visibility = View.GONE
                                    }
                                }
                                is LoadState.Error -> {
                                    if (adapterProduct.itemCount == 0) {
                                        progressBar.visibility = View.GONE
                                        viewEmptyDatas.root.visibility = View.VISIBLE
                                    } else {
                                        progressBar.visibility = View.GONE
                                        viewEmptyDatas.root.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            initSearchingKey()
            setViewModel(null)
            binding.progressBar.visibility = View.VISIBLE
            binding.edtSearch.setQuery("", false)
            binding.edtSearch.clearFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}