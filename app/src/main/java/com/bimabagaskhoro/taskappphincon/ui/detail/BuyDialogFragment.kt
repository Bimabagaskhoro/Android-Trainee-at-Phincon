package com.bimabagaskhoro.taskappphincon.ui.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.DataStockItem
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestRating
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestStock
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.DataDetail
import com.bimabagaskhoro.taskappphincon.databinding.FragmentBuyDialogBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.DetailActivity
import com.bimabagaskhoro.taskappphincon.ui.activity.OnSuccessActivity
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.math.log

@AndroidEntryPoint
class BuyDialogFragment(private val data: DataDetail) : BottomSheetDialogFragment() {
    private var _binding: FragmentBuyDialogBinding? = null
    private val binding get() = _binding
    private val viewModel: BuyDialogViewModel by viewModels()
    private val viewModelStock: AuthViewModel by viewModels()
    override fun getTheme(): Int {
        return R.style.NoBackgroundDialogTheme
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBuyDialogBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            Glide.with(requireContext())
                .load(data.image)
                .into(this!!.imgDialog)

            tvPriceFragmentDialog.text = data.harga.formatterIdr()
            tvStockFragmentDialog.text = data.stock.toString()

            if (data.stock == 1) {
                tvStockFragmentDialog.text = getString(R.string.out_stock)
            }
        }

        viewModel.quantity.observe(requireActivity()) { results ->
            binding?.tvTotalNumber?.text = results.toString()
            if (results == data.stock) {
                binding?.addFragmentDialog?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_lightgrey)
                binding?.minFragmentDialog?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_black)
            } else if (results == 1) {
                binding?.addFragmentDialog?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_black)
                binding?.minFragmentDialog?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_lightgrey)
            }
        }

        viewModel.setPrice(data.harga.toInt())

        viewModel.price.observe(requireActivity()) {data ->
            binding?.tvTotalPrice?.text = data.toString().formatterIdr()
        }

        binding?.apply {
            val stock = tvStockFragmentDialog.text
            val buy = tvTotalNumber.text
            if (stock == buy) {
                cardBuy.isClickable = false
                cardBuy.setOnClickListener {
                    Toast.makeText(requireActivity(), R.string.out_stock, Toast.LENGTH_SHORT).show()
//                    Log.d("checkingCar", "onViewCreated: ")
                }
            } else if (stock != buy){
                cardBuy.isClickable = true
                cardBuy.setOnClickListener {
                    val reqParams = "data_stock"
                    val idProduct = data.id.toString()
                    val stockProduct = binding?.tvTotalNumber?.text.toString()
                    doActionUpdate(reqParams, idProduct, stockProduct.toInt())
                }
            }
        }

        setActionData()
    }

    private fun doActionUpdate(reqParams: String, idProduct: String, stockProduct: Int)  {
        viewModelStock.updateStock(
            reqParams,
            idProduct,
            stockProduct,
        ).observe(viewLifecycleOwner) { results ->
            when (results) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    val intent = Intent(context, OnSuccessActivity::class.java)
                    intent.putExtra(OnSuccessActivity.EXTRA_DATA_SUCCESS, data.id)
                    startActivity(intent)
                    Log.d("Teststockkkkkkk", "doActionUpdate: ")
                }
                is Resource.Error -> {
                    try {
                        val err =
                            results.errorBody?.string()
                                ?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse =
                            gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        Toast.makeText(requireActivity(), messageErr, Toast.LENGTH_SHORT).show()
                    } catch (e: java.lang.Exception) {
                        val err = results.errorCode
                        Log.d("ErrorCode", "$err")
                    }
                }
                is Resource.Empty -> {
                    Log.d("unFavorite", "Empty Data")
                }

            }
        }
    }

    private fun setActionData() {
        binding?.addFragmentDialog?.setOnClickListener {
            viewModel.addQuantity(data.stock)
        }
        binding?.minFragmentDialog?.setOnClickListener {
            viewModel.minQuantity()
        }
    }

}