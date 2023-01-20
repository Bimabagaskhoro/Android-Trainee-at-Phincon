package com.bimabagaskhoro.taskappphincon.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.ResponseProduct
import com.bimabagaskhoro.taskappphincon.databinding.FragmentHomeBinding
import com.bimabagaskhoro.taskappphincon.ui.adapter.ProductAdapter
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.vm.ProductViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.json.JSONObject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var adapter: ProductAdapter
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchJob: Job? = null
    private var queryString: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        adapter = ProductAdapter()
        initSearchingKey()
        setData(queryString, 0)
        return root
    }

    private fun initSearchingKey() {
        binding.edtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
//                hideKeyboard(requireActivity())
//                return false
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(q: String?): Boolean {
                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    q?.let {
                        delay(2000)
                        if (q?.length == 0 || q.toString() == "") {
                            setData("", 0)
                            clearFragmentResult(q)
                        } else {
                            setData(q, 0)
                            clearFragmentResult(q)
                        }
                    }
                }
                return false
            }
        })
        binding.apply {
            fabShorting.setOnClickListener {
                showFilterDialog()
            }
        }
    }

    private fun setData(q: String?, i: Int?) {
        if (q.toString().isNotEmpty()) {
            queryString = q.toString()
            viewModel.getProduct(q).observe(viewLifecycleOwner) { data ->
                when (data) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        if (data.data!!.success.data.isNotEmpty()) {
                            binding.rvProduct.visibility = View.VISIBLE
                            binding.fabShorting.visibility = View.VISIBLE
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
            viewModel.getProduct().observe(viewLifecycleOwner) { data ->
                when (data) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        if (data.data!!.success.data.isNotEmpty()) {
                            binding.rvProduct.visibility = View.VISIBLE
                            binding.fabShorting.visibility = View.VISIBLE
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
        }
    }

    private fun setProductRv(data: ResponseProduct, i: Int?) {
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

    private fun showFilterDialog() {
        val options = arrayOf("From A to Z", "From Z to A")
        var selectedOption = ""
        MaterialAlertDialogBuilder(requireActivity()).setTitle(resources.getString(R.string.sort_by))
            .setSingleChoiceItems(options, -1) { _, which ->
                selectedOption = options[which]
            }.setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                when (selectedOption) {
                    options[0] -> {
                        setData(queryString, 1)
                    }
                    options[1] -> {
                        setData(queryString, 2)
                    }
                    else -> {
                        setData(queryString, 0)
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
            rvProduct.layoutManager = LinearLayoutManager(context)
            rvProduct.setHasFixedSize(true)
            adapter.onItemClick = {
//                val bundle = Bundle().apply { putParcelable(DetailItemFragment.EXTRA_DATA, it) }
//                findNavController().navigate(
//                    R.id.action_navigation_dashboard_to_detailItemFragment,
//                    bundle
//                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}