package com.bimabagaskhoro.taskappphincon.feature.fav

import android.app.AlertDialog
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
import com.bimabagaskhoro.phincon.core.data.source.remote.response.ResponseError
import com.bimabagaskhoro.phincon.core.data.source.remote.response.favorite.ResponseFavorite
import com.bimabagaskhoro.phincon.core.vm.DataStoreViewModel
import com.bimabagaskhoro.phincon.core.vm.LocalViewModel
import com.bimabagaskhoro.phincon.core.vm.RemoteViewModel
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.FragmentFavoriteBinding
import com.bimabagaskhoro.taskappphincon.feature.activity.CartActivity
import com.bimabagaskhoro.taskappphincon.feature.activity.DetailActivity
import com.bimabagaskhoro.taskappphincon.feature.activity.NotificationActivity
import com.bimabagaskhoro.taskappphincon.feature.adapter.ProductFavAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.json.JSONObject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding

    private val viewModel: RemoteViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private val roomViewModel: LocalViewModel by viewModels()
    private lateinit var adapter: ProductFavAdapter

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ProductFavAdapter()
        dataStoreViewModel.getUserId.observe(viewLifecycleOwner) {
            val idUser = it
            initSearchingKey(idUser)
            binding?.apply {
                swipeRefresh.setOnRefreshListener {
                    initSearchingKey(idUser)
                    viewModel.onRefresh()
                    progressBar.visibility = View.VISIBLE
                    edtSearch.setQuery("", false)
                    edtSearch.clearFocus()
                }
            }
        }
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
                                startActivity(
                                    Intent(
                                        requireActivity(),
                                        NotificationActivity::class.java
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initSearchingKey(idUser: Int?) {
        idUser?.let { setData(null, it, 0) }
        binding?.edtSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                com.bimabagaskhoro.phincon.core.utils.hideKeyboard(requireActivity())
                return false
            }

            override fun onQueryTextChange(q: String?): Boolean {
                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    q?.let {
                        delay(2000)
                        if (q.isEmpty() || q.toString() == "") {
                            idUser?.let { it1 -> setData("", it1, 0) }
                        } else {
                            idUser?.let { it1 -> setData(q, it1, 0) }
                        }
                    }
                }
                return false
            }
        })
        binding?.apply {
            fabShorting.setOnClickListener {
                idUser?.let { it1 -> showFilterDialog(it1) }
            }
        }
    }

    private fun setData(q: String?, idUser: Int, i: Int?) {
        if (q.toString().isNotEmpty()) {
            viewModel.getFavProduct(idUser, q).observe(viewLifecycleOwner) { data ->
                when (data) {
                    is com.bimabagaskhoro.phincon.core.utils.Resource.Loading -> {
                        binding?.apply {
                            progressBar.visibility = View.VISIBLE
                            fabShorting.visibility = View.GONE
                            rvProduct.visibility = View.GONE
                            viewEmptyData.root.visibility = View.GONE
                        }
                    }
                    is com.bimabagaskhoro.phincon.core.utils.Resource.Success -> {
                        if (data.data?.success?.data?.isNotEmpty() == true) {
                            binding?.apply {
                                rvProduct.visibility = View.VISIBLE
                                fabShorting.visibility = View.VISIBLE
                                swipeRefresh.isRefreshing = false
                                progressBar.visibility = View.GONE
                                viewEmptyData.root.visibility = View.GONE
                                fabShorting.visibility = View.VISIBLE
                                data.data?.let { result ->
                                    setProductRv(result, i)
                                }
                            }
                        } else {
                            binding?.apply {
                                rvProduct.visibility = View.GONE
                                fabShorting.visibility = View.GONE
                            }
                        }
                    }
                    is com.bimabagaskhoro.phincon.core.utils.Resource.Error -> {
                        try {
                            binding?.apply {
                                swipeRefresh.isRefreshing = false
                                progressBar.visibility = View.GONE
                                fabShorting.visibility = View.GONE
                            }
                            val err =
                                data.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                            val gson = Gson()
                            val jsonObject = gson.fromJson(err, JsonObject::class.java)
                            val errorResponse = gson.fromJson(jsonObject, ResponseError::class.java)
                            val messageErr = errorResponse.error.message
                            AlertDialog.Builder(requireActivity()).setTitle("Failed")
                                .setMessage(messageErr).setPositiveButton("Ok") { _, _ ->
                                }.show()
                        } catch (t: Throwable) {
                            Toast.makeText(requireActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is com.bimabagaskhoro.phincon.core.utils.Resource.Empty -> {
                        binding?.apply {
                            swipeRefresh.isRefreshing = false
                            progressBar.visibility = View.GONE
                            viewEmptyData.root.visibility = View.VISIBLE
                            rvProduct.visibility = View.GONE
                            fabShorting.visibility = View.GONE
                            edtSearch.visibility = View.GONE

                        }
                    }
                }
            }
        } else {
            viewModel.getFavProduct(idUser, null).observe(viewLifecycleOwner) { data ->
                when (data) {
                    is com.bimabagaskhoro.phincon.core.utils.Resource.Loading -> {
                        binding?.apply {
                            progressBar.visibility = View.VISIBLE
                            fabShorting.visibility = View.GONE
                            rvProduct.visibility = View.GONE
                            viewEmptyData.root.visibility = View.GONE
                        }
                    }
                    is com.bimabagaskhoro.phincon.core.utils.Resource.Success -> {
                        if (data.data?.success?.data?.isNotEmpty() == true) {
                            binding?.apply {
                                rvProduct.visibility = View.VISIBLE
                                fabShorting.visibility = View.VISIBLE
                                viewEmptyData.root.visibility = View.GONE
                                progressBar.visibility = View.GONE
                                fabShorting.visibility = View.VISIBLE
                                data.data?.let { result ->
                                    setProductRv(result, i)
                                }
                            }
                        } else {
                            binding?.rvProduct?.visibility = View.GONE
                        }

                    }
                    is com.bimabagaskhoro.phincon.core.utils.Resource.Error -> {
                        try {
                            binding?.apply {
                                progressBar.visibility = View.GONE
                                fabShorting.visibility = View.GONE
                            }
                            val err =
                                data.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                            val gson = Gson()
                            val jsonObject = gson.fromJson(err, JsonObject::class.java)
                            val errorResponse = gson.fromJson(jsonObject, ResponseError::class.java)
                            val messageErr = errorResponse.error.message
                            AlertDialog.Builder(requireActivity()).setTitle("Failed")
                                .setMessage(messageErr).setPositiveButton("Ok") { _, _ ->
                                }.show()
                        } catch (t: Throwable) {
                            Toast.makeText(requireActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is com.bimabagaskhoro.phincon.core.utils.Resource.Empty -> {
                        binding?.apply {
                            progressBar.visibility = View.GONE
                            viewEmptyData.root.visibility = View.VISIBLE
                            rvProduct.visibility = View.GONE
                            fabShorting.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun setProductRv(data: ResponseFavorite, i: Int?) {
        when (i) {
            0 -> {
                adapter.setData(data.success.data)
                initRecyclerView()
            }
            1 -> {
                adapter.setData(data.success.data.sortedBy { it.name_product })
                initRecyclerView()
            }
            2 -> {
                adapter.setData(data.success.data.sortedByDescending { it.name_product })
                initRecyclerView()
            }
        }
    }

    private fun showFilterDialog(idUser: Int) {
        val options = arrayOf("From A to Z", "From Z to A")
        var selectedOption = ""
        MaterialAlertDialogBuilder(requireActivity()).setTitle(resources.getString(R.string.sort_by))
            .setSingleChoiceItems(options, -1) { _, which ->
                selectedOption = options[which]
            }.setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                when (selectedOption) {
                    options[0] -> {
                        setData(null, idUser, 1)
                    }
                    options[1] -> {
                        setData(null, idUser, 2)
                    }
                    else -> {
                        setData(null, idUser, 0)
                    }
                }
            }.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun initRecyclerView() {
        binding?.apply {
            progressBar.visibility = View.GONE
            rvProduct.adapter = adapter
            viewEmptyData.root.visibility = View.GONE
            rvProduct.setHasFixedSize(true)

            adapter.onItemClick = {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_DATA_DETAIL, it.id)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dataStoreViewModel.getUserId.observe(viewLifecycleOwner) {
            val idUser = it
            setData(null, idUser, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}