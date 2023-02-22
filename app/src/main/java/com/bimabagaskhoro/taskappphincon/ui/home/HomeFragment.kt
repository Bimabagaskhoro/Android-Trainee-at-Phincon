package com.bimabagaskhoro.taskappphincon.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.INITIAL_INDEX
import com.bimabagaskhoro.taskappphincon.databinding.FragmentHomeBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.CartActivity
import com.bimabagaskhoro.taskappphincon.ui.activity.DetailActivity
import com.bimabagaskhoro.taskappphincon.ui.activity.NotificationActivity
import com.bimabagaskhoro.taskappphincon.ui.adapter.paging.LoadPagingAdapter
import com.bimabagaskhoro.taskappphincon.ui.adapter.paging.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val viewModel: com.bimabagaskhoro.phincon.core.vm.RemoteViewModel by viewModels()
    private val roomViewModel: com.bimabagaskhoro.phincon.core.vm.LocalViewModel by viewModels()
    private lateinit var adapterProduct: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setViewModel(null)
        initSwipeRefresh()
        initSearchingKey()
        setUpToolbar()
    }


    private fun setUpToolbar() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    roomViewModel.getAllProduct().collect { result ->
                        binding?.apply {
                            if (result.isNotEmpty()) {
                                imgBadges.isVisible = true
                                tvBadgesMenu.text = result.size.toString()
                            } else {
                                imgBadges.isVisible = false
                                tvBadgesMenu.isVisible = false
                                tvBadgesMenu.text = result.size.toString()
                            }
                            icCart.setOnClickListener {
                                startActivity(Intent(requireActivity(), CartActivity::class.java))
                            }
                        }
                    }
                }
                launch {
                    roomViewModel.getAllNotification().collect { result ->
                        binding?.apply {
                            if (result.isNotEmpty()) {
                                imgBadgesNotification.isVisible = true
                                tvBadgesNotification.text = result.size.toString()
                            } else {
                                imgBadgesNotification.isVisible = false
                                tvBadgesNotification.isVisible = false
                                tvBadgesNotification.text = result.size.toString()
                            }
                            icNotification.setOnClickListener {
                                startActivity(Intent(requireActivity(), NotificationActivity::class.java))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initSearchingKey() {
        binding?.edtSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                com.bimabagaskhoro.phincon.core.utils.hideKeyboard(requireActivity())
                return false
            }

            override fun onQueryTextChange(q: String): Boolean {
                viewModel.onSearch(q)
                setViewModel(q)
                return true
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

        binding?.rvProduct?.apply {
            adapterProduct.addLoadStateListener { loadState ->
                binding?.progressBar?.isVisible = loadState.source.refresh is LoadState.Loading
            }
            adapter = adapterProduct.withLoadStateFooter(
                footer = LoadPagingAdapter { adapterProduct.retry() }
            )
        }
    }

    private fun setViewModel(q: String?) {
        if (q.toString().isNotEmpty()) {
            lifecycleScope.launch {
                viewModel.productList.collectLatest { data ->
                    adapterProduct.submitData(data)
                }
            }
            lifecycleScope.launch {
                viewModel.productList.collectLatest {
                    adapterProduct.loadStateFlow.collectLatest { load ->
                        binding?.apply {
                            when (load.refresh) {
                                is LoadState.Loading -> {
                                    progressBar?.visibility = View.VISIBLE
                                    rvProduct.visibility = View.GONE
                                    viewEmptyDatas?.root?.visibility = View.GONE
                                }
                                is LoadState.NotLoading -> {
                                    if (adapterProduct.itemCount == 0) {
                                        viewEmptyDatas?.root?.visibility = View.VISIBLE
                                    } else {
                                        progressBar?.visibility = View.GONE
                                        rvProduct.visibility = View.VISIBLE
                                        binding?.swipeRefresh?.isRefreshing = false
                                        viewEmptyDatas?.root?.visibility = View.GONE
                                    }
                                }
                                is LoadState.Error -> {
                                    if (adapterProduct.itemCount == INITIAL_INDEX) {
                                        progressBar?.visibility = View.GONE
                                        viewEmptyDatas?.root?.visibility = View.VISIBLE
                                    } else {
                                        progressBar?.visibility = View.GONE
                                        viewEmptyDatas?.root?.visibility = View.GONE
                                    }
                                }

                                else -> {
                                    Toast.makeText(context, "No Internet Detect", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initSwipeRefresh() {
        binding?.apply {
            swipeRefresh.setOnRefreshListener {
                initSearchingKey()
                setViewModel(null)
                progressBar?.visibility = View.VISIBLE
                edtSearch.setQuery("", false)
                edtSearch.clearFocus()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}