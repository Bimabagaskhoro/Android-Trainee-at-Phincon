package com.bimabagaskhoro.taskappphincon.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.DataStockItem
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.databinding.ActivityCartBinding
import com.bimabagaskhoro.taskappphincon.firebase.payment.PaymentActivity
import com.bimabagaskhoro.taskappphincon.ui.activity.OnSuccessActivity.Companion.EXTRA_DATA_SUCCESS_ID
import com.bimabagaskhoro.taskappphincon.ui.activity.OnSuccessActivity.Companion.EXTRA_DATA_SUCCESS_ID_PAYMENT
import com.bimabagaskhoro.taskappphincon.ui.activity.OnSuccessActivity.Companion.EXTRA_DATA_SUCCESS_NAME
import com.bimabagaskhoro.taskappphincon.ui.adapter.CartAdapter
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.bimabagaskhoro.taskappphincon.vm.DataStoreViewModel
import com.bimabagaskhoro.taskappphincon.vm.LocalViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@Suppress("UnusedEquals", "UNUSED_EXPRESSION")
@AndroidEntryPoint
class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private val roomViewModel: LocalViewModel by viewModels()
    private val viewModel: AuthViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doActionAdapter()
        initData()
        doActionTextClick()

        binding.apply {
            btnBack.setOnClickListener {
                val intent = Intent(this@CartActivity, MainActivity::class.java)
                startActivity(intent)
            }
            checkBox2.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    btnBuy.isClickable = false
                } else if (isChecked) {
                    initCheckBox(isChecked)
                    val dataPayment = intent.getStringExtra(EXTRA_DATA_CART)
                    val dataName = intent.getStringExtra(EXTRA_DATA_CART_NAME)
                    if (dataPayment == null && dataName == null) {
                        btnBuy.isClickable = false
                    } else {
                        binding.btnBuy.setOnClickListener {
                            setActionPost(dataPayment, dataName)
                        }
                    }
                }
            }
        }
    }

    private fun doActionTextClick() {
        val dataPayment = intent.getStringExtra(EXTRA_DATA_CART)
        val dataName = intent.getStringExtra(EXTRA_DATA_CART_NAME)

        if (dataPayment == null && dataName == null) {
            binding.apply {
                imgPaymentMethode.visibility = View.INVISIBLE
                tvPaymentMethode.visibility = View.INVISIBLE
                tvHoldingPayment.visibility = View.VISIBLE
                btnBuy.isClickable = false
                btnBuy.setOnClickListener {
                    Toast.makeText(
                        this@CartActivity,
                        "Please Choose payment Method first",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                tvHoldingPayment.setOnClickListener {
                    val intent = Intent(this@CartActivity, PaymentActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            binding.apply {
                tvHoldingPayment.visibility = View.INVISIBLE
                imgPaymentMethode.visibility = View.VISIBLE
                tvPaymentMethode.visibility = View.VISIBLE

                dataPayment?.let { initImagePayment(it) }
                tvPaymentMethode.text = dataName
                tvHoldingPayment.visibility = View.GONE
                btnBuy.isClickable = true
                btnBuy.setOnClickListener {
                    setActionPost(dataPayment, dataName)
                }
                tvPaymentMethode.setOnClickListener {
                    val intent = Intent(this@CartActivity, PaymentActivity::class.java)
                    startActivity(intent)
                }
                imgPaymentMethode.setOnClickListener {
                    val intent = Intent(this@CartActivity, PaymentActivity::class.java)
                    startActivity(intent)
                }

                val countBadges = roomViewModel.countAllCart
                if (countBadges == 0) {
                    binding.apply {
                        checkBox2.visibility = View.INVISIBLE
                        tvCheckAll.visibility = View.INVISIBLE
                        tvAllPrice.visibility = View.INVISIBLE
                        btnBuy.visibility = View.INVISIBLE
                    }
                }

            }
        }
    }

    private fun initCheckBox(checked: Boolean) {
        if (checked) {
            dataStoreViewModel.getUserChecked.observe(this@CartActivity) { true }
            roomViewModel.checkAll(1)
            val resultPrice = roomViewModel.getTotalPrice()
            binding.tvAllPrice.text = resultPrice.toString().formatterIdr()
        } else {
            dataStoreViewModel.getUserChecked.observe(this@CartActivity) { false }
            roomViewModel.checkAll(0)
            val resultPrice = roomViewModel.getTotalPrice()
            binding.tvAllPrice.text = resultPrice.toString().formatterIdr()
        }
    }

    private fun doActionAdapter() {
        adapter = CartAdapter(
            { roomViewModel.deleteCart(it) },
            { data ->
                val totalQty = (data.quantity?.plus(1))
                val totalPrice = (data.firstPrice?.toInt()?.let { totalQty?.times(it) })
                val id = data.id
                val newPrice = (data.firstPrice?.toInt()?.let { totalQty?.times(it) })

                totalQty?.let {
                    id?.let { it1 ->
                        newPrice?.let { it2 ->
                            roomViewModel.updateQuantity(
                                it, it1,
                                it2
                            )
                        }
                    }
                }
                totalPrice?.let { id?.let { it1 -> roomViewModel.updatePriceCard(it, it1) } }

                val resultPrice = roomViewModel.getTotalPrice()
                binding.tvAllPrice.text = resultPrice.toString().formatterIdr()
            },
            { data ->
                val totalQty = (data.quantity?.minus(1))
                val totalPrice = (data.firstPrice?.toInt()?.let { totalQty?.times(it) })
                val id = data.id
                val newPrice = (data.firstPrice?.toInt()?.let { totalQty?.times(it) })

                totalQty?.let {
                    id?.let { it1 ->
                        newPrice?.let { it2 ->
                            roomViewModel.updateQuantity(
                                it, it1,
                                it2
                            )
                        }
                    }
                }
                totalPrice?.let { id?.let { it1 -> roomViewModel.updatePriceCard(it, it1) } }

                val resultPrice = roomViewModel.getTotalPrice()
                binding.tvAllPrice.text = resultPrice.toString().formatterIdr()
            },
            { data ->
                val id = data.id
                id?.let { roomViewModel.updateCheck(it, 1) }
                val result = roomViewModel.getTotalPrice()
                binding.tvAllPrice.text = result.toString().formatterIdr()

                val dataPayment = intent.getStringExtra(EXTRA_DATA_CART)
                val dataName = intent.getStringExtra(EXTRA_DATA_CART_NAME)
                binding.btnBuy.setOnClickListener {
                    setActionPost(dataPayment, dataName)
                }
            },
            { data ->
                val id = data.id
                id?.let { roomViewModel.updateCheck(it, 0) }
                val result = roomViewModel.getTotalPrice()
                binding.tvAllPrice.text = result.toString().formatterIdr()
            },
        )
    }

    private fun initData() {
        roomViewModel.getAllCart.observe(this@CartActivity) {
            if (it.isNotEmpty()) {
                adapter.setData(it)
                binding.apply {
                    rvCart.adapter = adapter
                    rvCart.layoutManager = LinearLayoutManager(this@CartActivity)
                    rvCart.setHasFixedSize(true)
                    adapter.onItemClick = {
                    }
                }
            }
        }
    }

    private fun setActionPost(dataPayment: String?, dataName: String?) {
        roomViewModel.getTrolleyChecked.observe(this@CartActivity) { result ->
            val dataStockItems = arrayListOf<DataStockItem>()
            val listOfProductId = arrayListOf<String>()
            dataStoreViewModel.getUserId.observe(this@CartActivity) {
                val idUser = it
                for (i in result.indices) {
                    dataStockItems.add(DataStockItem(result[i].id.toString(), result[i].quantity))
                    listOfProductId.add(result[i].id.toString())
                }
                buyProduct(
                    idUser.toString(),
                    dataStockItems,
                    listOfProductId,
                    dataPayment,
                    dataName
                )
            }
        }
    }

    private fun buyProduct(
        idUser: String,
        requestBody: List<DataStockItem>,
        listOfProductId: ArrayList<String>,
        dataPayment: String?,
        dataName: String?
    ) {
        viewModel.updateStock(requestBody, idUser).observe(this@CartActivity) { todo ->
            when (todo) {
                is Resource.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                    binding.cardProgressbar.visibility = View.VISIBLE
                    binding.tvWaiting.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressbar.visibility = View.GONE
                    binding.cardProgressbar.visibility = View.GONE
                    binding.tvWaiting.visibility = View.GONE
                    roomViewModel.deleteTrolleyChecked()

                    val intent = Intent(this, OnSuccessActivity::class.java)
                    intent.putExtra(EXTRA_DATA_SUCCESS_ID, listOfProductId)
                    intent.putExtra(EXTRA_DATA_SUCCESS_NAME, dataName)
                    intent.putExtra(EXTRA_DATA_SUCCESS_ID_PAYMENT, dataPayment)
                    startActivity(intent)
                }
                is Resource.Error -> {
                    binding.progressbar.visibility = View.GONE
                    binding.cardProgressbar.visibility = View.GONE
                    binding.tvWaiting.visibility = View.GONE
                    try {
                        val err =
                            todo.errorBody?.string()
                                ?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse =
                            gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        messageErr?.let { Log.d("Error Body", it) }
                    } catch (e: java.lang.Exception) {
                        val err = todo.errorCode
                        Log.d("ErrorCode", "$err")
                    }
                }
                is Resource.Empty -> {
                    Log.d("Empty Data", "Empty")
                }
            }
        }
    }

    private fun initImagePayment(dataPayment: String) {
        binding.apply {
            when (dataPayment) {
                "va_bca" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.bca)
                        .into(imgPaymentMethode)
                }
                "va_mandiri" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.mandiri)
                        .into(imgPaymentMethode)
                }
                "va_bri" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.bri)
                        .into(imgPaymentMethode)
                }
                "va_bni" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.bni)
                        .into(imgPaymentMethode)
                }
                "va_btn" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.btn)
                        .into(imgPaymentMethode)
                }
                "va_danamon" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.danamon)
                        .into(imgPaymentMethode)
                }
                "ewallet_gopay" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.gopay)
                        .into(imgPaymentMethode)
                }
                "ewallet_ovo" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.ovo)
                        .into(imgPaymentMethode)
                }
                "ewallet_dana" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.dana)
                        .into(imgPaymentMethode)
                }
            }
        }
    }

    companion object {
        const val EXTRA_DATA_CART = "extra_data_cart"
        const val EXTRA_DATA_CART_NAME = "extra_data_cart_name"
    }
}
