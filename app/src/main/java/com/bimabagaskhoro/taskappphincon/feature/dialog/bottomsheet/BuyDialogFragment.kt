package com.bimabagaskhoro.taskappphincon.feature.dialog.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bimabagaskhoro.phincon.core.data.source.remote.response.DataStockItem
import com.bimabagaskhoro.phincon.core.data.source.remote.response.ResponseError
import com.bimabagaskhoro.phincon.core.data.source.remote.response.detail.DataDetail
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.FragmentBuyDialogBinding
import com.bimabagaskhoro.taskappphincon.feature.activity.PaymentActivity
import com.bimabagaskhoro.taskappphincon.feature.activity.PaymentActivity.Companion.EXTRA_DATA_PAYMENT
import com.bimabagaskhoro.taskappphincon.feature.activity.OnSuccessActivity
import com.bimabagaskhoro.phincon.core.utils.formatterIdr
import com.bimabagaskhoro.phincon.core.vm.DataStoreViewModel
import com.bimabagaskhoro.phincon.core.vm.RemoteViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class BuyDialogFragment(
    private val data: DataDetail,
    private val dataPayment: String?,
    private val namePayment: String?
) : BottomSheetDialogFragment() {
    private var _binding: FragmentBuyDialogBinding? = null
    private val binding get() = _binding
    private val viewModel: BuyDialogViewModel by viewModels()
    private val viewModelStock: RemoteViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

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

        getDataDetail()
        initDataPayment()
        observeToggle()
        postDataProduct()
    }

    private fun postDataProduct() {
        binding?.apply {
            val stock = tvStockFragmentDialog.text
            val buy = tvTotalNumber.text
            if (stock == buy) {
                cardBuy.isClickable = false
                cardBuy.setOnClickListener {
                    Toast.makeText(requireActivity(), R.string.out_stock, Toast.LENGTH_SHORT).show()
                }
            } else if (stock != buy) {
                cardBuy.isClickable = true

                if (dataPayment != null && namePayment != null) {
                    tvPaymentMethode.visibility = View.VISIBLE
                    tvPaymentMethode.setOnClickListener {
                        val idProduct = data.id.toString()
                        val intent2 = Intent(context, PaymentActivity::class.java)
                        intent2.putExtra(EXTRA_DATA_PAYMENT, idProduct.toInt())
                        startActivity(intent2)
                    }
                    imgPaymentMethode.visibility = View.VISIBLE
                    imgPaymentMethode.setOnClickListener {
                        val idProduct = data.id.toString()
                        val intentOsaas = Intent(context, PaymentActivity::class.java)
                        intentOsaas.putExtra(EXTRA_DATA_PAYMENT, idProduct.toInt())
                        startActivity(intentOsaas)

                    }
                    cardBuy.setOnClickListener {
                        val stockProduct = binding?.tvTotalNumber?.text.toString()

                        val idProduct = data.id.toString()
                        dataStoreViewModel.getUserId.observe(viewLifecycleOwner) {
                            val idUser = it
                            doActionUpdate(
                                idProduct,
                                stockProduct.toInt(),
                                idUser.toString(),
                                dataPayment,
                                namePayment
                            )
                        }
                    }
                    Log.d("cardBuy", "to on success")
                } else {
                    tvPaymentMethode.visibility = View.GONE
                    tvPaymentMethode.setOnClickListener {
                        val idProduct = data.id.toString()
                        val intent2 = Intent(context, PaymentActivity::class.java)
                        intent2.putExtra(EXTRA_DATA_PAYMENT, idProduct.toInt())
                        startActivity(intent2)

                    }
                    imgPaymentMethode.visibility = View.GONE
                    imgPaymentMethode.setOnClickListener {
                        val idProduct = data.id.toString()
                        val intent3 = Intent(context, PaymentActivity::class.java)
                        intent3.putExtra(EXTRA_DATA_PAYMENT, idProduct.toInt())
                        startActivity(intent3)
                    }
                    cardBuy.setOnClickListener {
                        val idProduct = data.id.toString()
                        val intent = Intent(requireActivity(), PaymentActivity::class.java)
                        intent.putExtra(EXTRA_DATA_PAYMENT, idProduct.toInt())
                        startActivity(intent)

                    }
                    Log.d("cardBuy", "toPayment")
                }
            }
        }
    }

    private fun observeToggle() {
        viewModel.quantity.observe(viewLifecycleOwner) { results ->
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
            } else if (results != 1) {
                binding?.addFragmentDialog?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_black)
                binding?.minFragmentDialog?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_black)
            }
        }

        data.harga?.toInt()?.let { viewModel.setPrice(it) }

        viewModel.price.observe(requireActivity()) { data ->
            binding?.tvTotalPrice?.text = data.toString().formatterIdr()
        }
        binding?.addFragmentDialog?.setOnClickListener {
            viewModel.addQuantity(data.stock)
        }
        binding?.minFragmentDialog?.setOnClickListener {
            viewModel.minQuantity()
        }
    }

    private fun getDataDetail() {
        binding.apply {
            Glide.with(requireContext())
                .load(data.image)
                .into(this!!.imgDialog)

            tvPriceFragmentDialog.text = data.harga?.formatterIdr()
            tvStockFragmentDialog.text = data.stock.toString()

            if (data.stock == 1) {
                tvStockFragmentDialog.text = getString(R.string.out_stock)
            }
        }
    }

    private fun doActionUpdate(
        idProduct: String,
        stockProduct: Int,
        idUser: String,
        dataPayment: String?,
        namePayment: String?
    ) {
        viewModelStock.updateStock(
            (listOf(DataStockItem(idProduct, stockProduct))), idUser
        ).observe(viewLifecycleOwner) { results ->
            when (results) {
                is com.bimabagaskhoro.phincon.core.utils.Resource.Loading -> {
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Success -> {
                    viewModel.price.observe(requireActivity()) {
                        val totalPrice = it.toString()
                        val intent6 = Intent(context, OnSuccessActivity::class.java)
                        intent6.putExtra(OnSuccessActivity.EXTRA_DATA_PRICE, totalPrice)
                        intent6.putExtra(OnSuccessActivity.EXTRA_DATA_SUCCESS, data.id)
                        intent6.putExtra(OnSuccessActivity.EXTRA_ID_SUCCESS, dataPayment)
                        intent6.putExtra(OnSuccessActivity.EXTRA_NAME_SUCCESS, namePayment)
                        startActivity(intent6)
                    }

                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Error -> {
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
                    } catch (t: Throwable) {
                        Toast.makeText(requireActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Empty -> {
                    Log.d("unFavorite", "Empty Data")
                }
            }
        }
    }

    private fun initDataPayment() {
        if (dataPayment == null && namePayment == null) {
            Log.d("initDataPayment", "Ada Error")
        } else {
            dataPayment?.let { initImagePayment(it) }
            binding?.tvPaymentMethode?.text = namePayment
        }
    }


    private fun initImagePayment(dataPayment: String) {
        binding?.apply {
            when (dataPayment) {
                "va_bca" -> {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .load(R.drawable.bca)
                        .into(imgPaymentMethode)
                }
                "va_mandiri" -> {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .load(R.drawable.mandiri)
                        .into(imgPaymentMethode)
                }
                "va_bri" -> {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .load(R.drawable.bri)
                        .into(imgPaymentMethode)
                }
                "va_bni" -> {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .load(R.drawable.bni)
                        .into(imgPaymentMethode)
                }
                "va_btn" -> {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .load(R.drawable.btn)
                        .into(imgPaymentMethode)
                }
                "va_danamon" -> {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .load(R.drawable.danamon)
                        .into(imgPaymentMethode)
                }
                "ewallet_gopay" -> {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .load(R.drawable.gopay)
                        .into(imgPaymentMethode)
                }
                "ewallet_ovo" -> {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .load(R.drawable.ovo)
                        .into(imgPaymentMethode)
                }
                "ewallet_dana" -> {
                    Glide.with(requireActivity())
                        .asBitmap()
                        .load(R.drawable.dana)
                        .into(imgPaymentMethode)
                }
            }
        }
    }
}