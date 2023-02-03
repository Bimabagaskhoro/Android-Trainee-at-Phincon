package com.bimabagaskhoro.taskappphincon.ui.fav

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.favorite.ResponseFavorite
import com.bimabagaskhoro.taskappphincon.databinding.FragmentFavoriteBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.DetailActivity
import com.bimabagaskhoro.taskappphincon.ui.adapter.ProductFavAdapter
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.utils.hideKeyboard
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.bimabagaskhoro.taskappphincon.vm.DataStoreViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.json.JSONObject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private lateinit var adapter: ProductFavAdapter

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchJob: Job? = null
    private var queryString: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ProductFavAdapter()
        dataStoreViewModel.getUserId.observe(viewLifecycleOwner) {
            val idUser = it
            initSearchingKey(idUser)
            binding.swipeRefresh.setOnRefreshListener {
                initSearchingKey(idUser)
                setData(null, idUser, 0)
                binding.progressBar.visibility = View.VISIBLE
                binding.edtSearch.setQuery("", false)
                binding.edtSearch.clearFocus()
            }
        }
    }

    private fun initSearchingKey(idUser: Int?) {
        setData(queryString, idUser!!, 0)
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
                        if (q.isEmpty() || q.toString() == "") {
                            setData("", idUser, 0)
                        } else {
                            setData(q, idUser, 0)
                        }
                    }
                }
                return false
            }
        })
        binding.apply {
            fabShorting.setOnClickListener {
                showFilterDialog(idUser)
            }
        }
    }

    private fun setData(q: String?, idUser: Int, i: Int?) {
        if (q.toString().isNotEmpty()) {
            queryString = q.toString()
            viewModel.getFavProduct(idUser, q).observe(viewLifecycleOwner) { data ->
                when (data) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        if (data.data!!.success.data.isNotEmpty()) {
                            binding.rvProduct.visibility = View.VISIBLE
                            binding.fabShorting.visibility = View.VISIBLE
                            binding.swipeRefresh.isRefreshing = false
                            setProductRv(data.data, i)
                        } else {
                            binding.rvProduct.visibility = View.GONE
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        try {
                            val err =
                                data.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                            val gson = Gson()
                            val jsonObject = gson.fromJson(err, JsonObject::class.java)
                            val errorResponse = gson.fromJson(jsonObject, ResponseError::class.java)
                            val messageErr = errorResponse.error.message
                            AlertDialog.Builder(requireActivity()).setTitle("Failed")
                                .setMessage(messageErr).setPositiveButton("Ok") { _, _ ->
                                }.show()
                        } catch (e: java.lang.Exception) {
                            val err = data.errorCode
                            Log.d("ErrorCode", "$err")
                        }
                    }
                    is Resource.Empty -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            viewEmptyData.root.visibility = View.VISIBLE
                            binding.rvProduct.visibility = View.GONE
                        }
                    }
                }
            }
        } else {
            viewModel.getFavProduct(idUser).observe(viewLifecycleOwner) { data ->
                when (data) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        if (data.data!!.success.data.isNotEmpty()) {
                            binding.rvProduct.visibility = View.VISIBLE
                            binding.fabShorting.visibility = View.VISIBLE
                            binding.viewEmptyData.root.visibility = View.GONE
                            setProductRv(data.data, i)
                        } else {
                            binding.rvProduct.visibility = View.GONE
                        }

                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        try {
                            val err =
                                data.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                            val gson = Gson()
                            val jsonObject = gson.fromJson(err, JsonObject::class.java)
                            val errorResponse = gson.fromJson(jsonObject, ResponseError::class.java)
                            val messageErr = errorResponse.error.message
                            AlertDialog.Builder(requireActivity()).setTitle("Failed")
                                .setMessage(messageErr).setPositiveButton("Ok") { _, _ ->
                                }.show()
                        } catch (e: java.lang.Exception) {
                            val err = data.errorCode
                            Log.d("ErrorCode", "$err")
                        }
                    }
                    is Resource.Empty -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            viewEmptyData.root.visibility = View.VISIBLE
                            rvProduct.visibility = View.GONE
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
            }.setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                when (selectedOption) {
                    options[0] -> {
                        setData(queryString, idUser, 1)
                    }
                    options[1] -> {
                        setData(queryString, idUser, 2)
                    }
                    else -> {
                        setData(queryString, idUser, 0)
                    }
                }
            }.setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }.show()
    }

    private fun initRecyclerView() {
        binding.apply {
            progressBar.visibility = View.GONE
            rvProduct.adapter = adapter
            binding.viewEmptyData.root.visibility = View.GONE
            rvProduct.setHasFixedSize(true)

            adapter.onItemClick = {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_DATA_DETAIL, it.id)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}