package com.bimabagaskhoro.taskappphincon.ui.fav

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.databinding.FragmentFavoriteBinding
import com.bimabagaskhoro.taskappphincon.databinding.FragmentRegisterBinding
import com.bimabagaskhoro.taskappphincon.ui.adapter.ProductFavAdapter
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.utils.hideKeyboard
import com.bimabagaskhoro.taskappphincon.vm.DataStoreViewModel
import com.bimabagaskhoro.taskappphincon.vm.ProductViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.json.JSONObject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private lateinit var adapter: ProductFavAdapter

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductFavAdapter()
        iniDataFavorite(null)
        initSearchingKey()
        binding.apply {
            fabShorting.setOnClickListener {
//                initDialog()
            }
        }
    }

    private fun initSearchingKey() {
        binding.edtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard(requireActivity())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    newText?.let {
                        delay(3000)
                        if (it.isEmpty()) {
                            iniDataFavorite(null)
                        } else {
                            iniDataFavorite(newText)
                        }
                    }
                }
                return false
            }
        })
    }

    private fun iniDataFavorite(q: String?) {
        var userIds = 0
        dataStoreViewModel.getUserId.observe(viewLifecycleOwner) {
            userIds = it
            Log.d("UserPrefFav", "$userIds")
        }
        viewModel.getFavProduct(3,q).observe(viewLifecycleOwner) { results ->
            when(results) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    adapter.setData(results.data!!.success.data)
                    binding.apply {
                        progressBar.visibility = View.GONE
                        rvProduct.adapter = adapter
                        rvProduct.layoutManager = LinearLayoutManager(context)
                        rvProduct.setHasFixedSize(true)
                        adapter.onItemClick = {
//                            val bundle = Bundle().apply { putParcelable(DetailItemFragment.EXTRA_DATA, it) }
//                            findNavController().navigate(R.id.action_navigation_dashboard_to_detailItemFragment, bundle)
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    try {
                        val err = results.errorBody?.string()
                            ?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse =
                            gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        AlertDialog.Builder(requireActivity())
                            .setTitle("Failed")
                            .setMessage(messageErr)
                            .setPositiveButton("Ok") { _, _ ->
                            }
                            .show()
                    } catch (e: java.lang.Exception) {
                        val err = results.errorCode
                        Log.d("ErrorCode", "$err")
                    }
                }
                is Resource.Empty -> {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        binding.viewEmptyData.root.visibility = View.VISIBLE
                    }
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}