package com.bimabagaskhoro.taskappphincon.feature.home

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
import com.bimabagaskhoro.phincon.core.vm.FGAViewModel
import com.bimabagaskhoro.phincon.core.vm.LocalViewModel
import com.bimabagaskhoro.phincon.core.vm.RemoteViewModel
import com.bimabagaskhoro.taskappphincon.databinding.FragmentHomeBinding
import com.bimabagaskhoro.taskappphincon.feature.activity.CartActivity
import com.bimabagaskhoro.taskappphincon.feature.activity.DetailActivity
import com.bimabagaskhoro.taskappphincon.feature.activity.NotificationActivity
import com.bimabagaskhoro.taskappphincon.feature.adapter.paging.LoadPagingAdapter
import com.bimabagaskhoro.taskappphincon.feature.adapter.paging.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val viewModel: RemoteViewModel by viewModels()
    private val roomViewModel: LocalViewModel by viewModels()
    private val analyticViewModel: FGAViewModel by viewModels()
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
                                analyticViewModel.onClickTrolleyToolbar()
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
                                analyticViewModel.onClickNotificationToolbar()
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
                query?.let { analyticViewModel.onSearchHome(it) }
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

            val name = it.name_product
            val price = it.harga
            val rate = it.rate
            val id = it.id
            name?.let { nameLet ->
                price?.toDouble()?.let { priceLet ->
                    rate?.let { rateLet ->
                        id?.let { idLet ->
                            analyticViewModel.onClickProductHome(
                                nameLet, priceLet, rateLet, idLet
                            )
                        }
                    }
                }
            }
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
                                        val page = adapterProduct.itemCount
                                        analyticViewModel.onPagingScrollHome(page.toString())
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
                                    Toast.makeText(
                                        context,
                                        "No Internet Detect",
                                        Toast.LENGTH_SHORT
                                    ).show()
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

    override fun onResume() {
        super.onResume()
        val nameClass = this.javaClass.simpleName
        analyticViewModel.onLoadScreenHome(nameClass)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}