package com.bimabagaskhoro.taskappphincon.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.DataStockItem
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestStock
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.databinding.ActivityCartBinding
import com.bimabagaskhoro.taskappphincon.ui.adapter.CartAdapter
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
import kotlin.math.log

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
            if (isChecked) {
                adapter.selectAll(true)
                val dataArray = adapter.getCheckedAll()
//                dataArray
//                    .map {
//                        it.harga
//                    }

                /**
                 *
                 * bener nich
                 */
                Log.d("CheckedDataRoom", "CheckData = ${
                    dataArray.map { it.harga }
                }")
//                Log.d("CheckedDataRoom", "CheckData = $dataArray")
//                dataArray.forEach { temp ->
////                    val schema = "data_stock"
//                    val idProduct = temp.id
//                    val quantity = temp.quantity
//                    val postData = ArrayList<DataStockItem>()
////                    Log.d("CheckedDataRoom", "CheckData = $idProduct")
//                    postData.apply {
//                        add(DataStockItem(idProduct.toString(), quantity))
//                        add(DataStockItem(idProduct.toString(), quantity))
//                        add(DataStockItem(idProduct.toString(), quantity))
//                        add(DataStockItem(idProduct.toString(), quantity))
//                        add(DataStockItem(idProduct.toString(), quantity))
//                    }
//
//                    Log.d("valueOnArray", "$postData")
////                    Log.d("valueOnArray", "($schema + $idProduct + $quantity)")
////
////                    val arrayList = idProduct.split(",").map { it }.toMutableList()
//                    binding.btnBuy.setOnClickListener {
//                        doActionBusy(postData)
//                        roomViewModel.deleteCart(idProduct)
//                    }
//                }
            } else if (!isChecked) {
                recreate()
            }
        }
    }

    private fun doActionBusy(postData: ArrayList<DataStockItem>) {
        viewModel.updateStock(RequestStock(postData)).observe(this@CartActivity) { results ->
            when (results) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    postData.forEach { temp ->
                        val productId = temp.id_product
                        val intent = Intent(this@CartActivity, OnSuccessActivity::class.java)

                        intent.putExtra(OnSuccessActivity.EXTRA_DATA_SUCCESS, productId.toInt())
                        startActivity(intent)
                        Log.d("Teststockkkkkkk", "doActionUpdate: ")
                    }

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

    private fun doActionClicked() {
        adapter = CartAdapter(
            { roomViewModel.deleteCart(it) },
            { _, idProduct, quantity ->
                val schema = "data_stock"
                val result = adapter.totalValue
                binding.tvAllPrice.text = result.toString().formatterIdr()
                binding.btnBuy.setOnClickListener {
//                    doActionBuy(idProduct, quantity)
//                    roomViewModel.deleteCart(idProduct.toInt())
                    val postData = ArrayList<DataStockItem>()
                    postData.apply {
                        add(DataStockItem(idProduct, quantity))
                        add(DataStockItem(idProduct, quantity))
                        add(DataStockItem(idProduct, quantity))
                        add(DataStockItem(idProduct, quantity))
                        add(DataStockItem(idProduct, quantity))
                    }
                    Log.d("valueOnArray", "$postData")
                    doActionBusy(postData)
                }
            },
            { _, idProduct, quantity ->
                val schema = "data_stock"
                val result = adapter.totalValue
                binding.tvAllPrice.text = result.toString().formatterIdr()
                binding.btnBuy.setOnClickListener {
//                    doActionBuy(idProduct, quantity)
//                    roomViewModel.deleteCart(idProduct.toInt())

                    val postData = ArrayList<DataStockItem>()
                    postData.apply {
                        add(DataStockItem(idProduct, quantity))
                        add(DataStockItem(idProduct, quantity))
                        add(DataStockItem(idProduct, quantity))
                        add(DataStockItem(idProduct, quantity))
                        add(DataStockItem(idProduct, quantity))
                    }
                    Log.d("valueOnArray", "$postData")
                    doActionBusy(postData)
                }
            },
            { id, quantity ->
                roomViewModel.updateQuantity((quantity + 1), id)
            },
            { id, quantity ->
                roomViewModel.updateQuantity((quantity - 1), id)
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


    private fun doActionBuy(productId: String, stock: Int) {
        viewModel.updateStock(
//            "data_stock",
//            (listOf(DataStockItem(productId, stock)))
            RequestStock(listOf(DataStockItem(productId, stock)))
        ).observe(this@CartActivity) { results ->
            when (results) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    val intent = Intent(this@CartActivity, OnSuccessActivity::class.java)
                    intent.putExtra(OnSuccessActivity.EXTRA_DATA_SUCCESS, productId.toInt())
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
