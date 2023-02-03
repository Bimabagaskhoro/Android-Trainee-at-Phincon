package com.bimabagaskhoro.taskappphincon.ui.other

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.databinding.FragmentHistoryBinding
import com.bimabagaskhoro.taskappphincon.databinding.FragmentOtherProductBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.DetailActivity
import com.bimabagaskhoro.taskappphincon.ui.adapter.ProductOtherAdapter
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.bimabagaskhoro.taskappphincon.vm.DataStoreViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import kotlin.math.log

class OtherProductFragment : Fragment() {
    private var _binding: FragmentOtherProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private lateinit var adapter: ProductOtherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOtherProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ProductOtherAdapter()
        dataStoreViewModel.getUserId.observe(viewLifecycleOwner) {
            val idUser = it
            initViewMode(idUser)
        }
    }

    private fun initViewMode(idUser: Int?) {
        viewModel.getOtherProduct(idUser!!).observe(viewLifecycleOwner) { data ->
            when (data) {
                is Resource.Loading -> {
                    Log.d("getOtherProduct", "isLoading")
                }
                is Resource.Success -> {
                    adapter.setData(data.data!!.success.data.sortedBy { it.name_product })
                    initRecyclerView()
                }
                is Resource.Error -> {
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
                    Log.d("getOtherProduct", "isEmpty")
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.apply {
            rvOtherProduct.adapter = adapter
            rvOtherProduct.setHasFixedSize(true)

            adapter.onItemClick = {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_DATA_DETAIL, it.id)
                startActivity(intent)
            }
        }
    }
}