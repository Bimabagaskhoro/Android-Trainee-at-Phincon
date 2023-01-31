package com.bimabagaskhoro.taskappphincon.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.DataStockItem
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.databinding.ActivityCartBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.OnSuccessActivity.Companion.EXTRA_DATA_SUCCESS
import com.bimabagaskhoro.taskappphincon.ui.activity.OnSuccessActivity.Companion.EXTRA_DATA_SUCCESS_ID
import com.bimabagaskhoro.taskappphincon.ui.adapter.CartAdapter
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.bimabagaskhoro.taskappphincon.vm.DataStoreViewModel
import com.bimabagaskhoro.taskappphincon.vm.LocalViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_cart.*
import org.json.JSONObject

@Suppress("UnusedEquals", "UNUSED_EXPRESSION")
@AndroidEntryPoint
class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private val roomViewModel: LocalViewModel by viewModels()
    private val viewModel: AuthViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
//    private lateinit var mutableListItem: MutableList<DataStockItem>
//    private lateinit var id: MutableList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doActionAdapter()
        initData()
//        mutableListItem = mutableListOf()
//        id = mutableListOf<Int>()

        binding.apply {
            btnBack.setOnClickListener {
                val intent = Intent(this@CartActivity, MainActivity::class.java)
                startActivity(intent)
            }
            checkBox2.setOnCheckedChangeListener { _, isChecked ->
                initCheckBox(isChecked)
            }
            btnBuy.setOnClickListener {
                setActionPost()
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
                val totalQty = (data.quantity + 1)
                val totalPrice = (totalQty * data.firstPrice.toInt())
                val id = data.id
                val newPrice = (totalQty * data.firstPrice.toInt())

                roomViewModel.updateQuantity(totalQty, id, newPrice)
                roomViewModel.updatePriceCard(totalPrice, id)

                val resultPrice = roomViewModel.getTotalPrice()
                binding.tvAllPrice.text = resultPrice.toString().formatterIdr()
            },
            { data ->
                val totalQty = (data.quantity - 1)
                val totalPrice = (totalQty * data.firstPrice.toInt())
                val id = data.id
                val newPrice = (totalQty * data.firstPrice.toInt())

                roomViewModel.updateQuantity(totalQty, id, newPrice)
                roomViewModel.updatePriceCard(totalPrice, id)

                val resultPrice = roomViewModel.getTotalPrice()
                binding.tvAllPrice.text = resultPrice.toString().formatterIdr()
            },
            { data ->
                val id = data.id
                roomViewModel.updateCheck(id, 1)
                val result = roomViewModel.getTotalPrice()
                binding.tvAllPrice.text = result.toString().formatterIdr()
            },
            { data ->
                val id = data.id
                roomViewModel.updateCheck(id, 0)
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

    private fun setActionPost() {
        roomViewModel.getTrolleyChecked.observe(this@CartActivity) { result ->
            Log.d("testid", "$result")
            val dataStockItems = arrayListOf<DataStockItem>()
            val listOfProductId = arrayListOf<String>()
            for (i in result.indices) {
                dataStockItems.add(DataStockItem(result[i].id.toString(), result[i].quantity))
                listOfProductId.add(result[i].id.toString())
            }
            buyProduct(dataStockItems, listOfProductId)
        }
    }

    private fun buyProduct(requestBody: List<DataStockItem>, listOfProductId: ArrayList<String>) {
        viewModel.updateStock(requestBody).observe(this@CartActivity) { todo ->
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
                    Log.d("testidtestt", "$listOfProductId")
                    roomViewModel.deleteTrolleyChecked()

                    val intent = Intent(this, OnSuccessActivity::class.java)
                    intent.putExtra(EXTRA_DATA_SUCCESS_ID, listOfProductId)
                    Log.d("listProductId", "$listOfProductId")
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
                        Log.d("Error Body", messageErr)
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
}
