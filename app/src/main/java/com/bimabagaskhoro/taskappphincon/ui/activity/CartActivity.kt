package com.bimabagaskhoro.taskappphincon.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.databinding.ActivityCartBinding
import com.bimabagaskhoro.taskappphincon.ui.adapter.CartAdapter
import com.bimabagaskhoro.taskappphincon.ui.detail.BuyDialogViewModel
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.bimabagaskhoro.taskappphincon.vm.LocalViewModel
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_cart.*
import org.json.JSONObject

@Suppress("UnusedEquals")
@AndroidEntryPoint
class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private val roomViewModel: LocalViewModel by viewModels()
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doActionClicked()
        initData()
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.checkBox2.setOnCheckedChangeListener { _, isChecked ->
            adapter.selectAll(isChecked)
        }
    }

    private fun doActionClicked() {
        adapter = CartAdapter(
            { roomViewModel.deleteCart(it) },
            {
                val result = adapter.totalValue
                binding.tvAllPrice.text = result.toString().formatterIdr()
            },
            {
                val result = adapter.totalValue
                binding.tvAllPrice.text = result.toString().formatterIdr()
            },
            {
                val id = it.id
                val quantity = it.quantity
                roomViewModel.updateQuantity((quantity + 1), id)
            },
            {
                val id = it.id
                val quantity = it.quantity
                roomViewModel.updateQuantity((quantity - 1), id)
            },
            {
                val schema = "data_stock"
                val idProduct = it.id
                val buyProduct = it.quantity
                binding.btnBuy.setOnClickListener {
                    doActionBuy(schema, idProduct.toString(), buyProduct)
                }
            }
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
            } else {
                Log.d("getDatabase", "Errr00r awokokwokwokwokw")
            }
        }
    }


    private fun doActionBuy(schema: String, idProduct: String, buyProduct: Int) {
        viewModel.updateStock(schema, idProduct, buyProduct).observe(this@CartActivity) { results ->
            when (results) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    val intent = Intent(this@CartActivity, OnSuccessActivity::class.java)
                    intent.putExtra(OnSuccessActivity.EXTRA_DATA_SUCCESS, idProduct.toInt())
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
                        Toast.makeText(this@CartActivity, messageErr, Toast.LENGTH_SHORT).show()
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

}
