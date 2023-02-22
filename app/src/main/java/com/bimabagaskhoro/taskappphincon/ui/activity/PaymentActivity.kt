package com.bimabagaskhoro.taskappphincon.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bimabagaskhoro.phincon.core.data.source.remote.response.PaymentModel
import com.bimabagaskhoro.phincon.core.vm.FRCViewModel
import com.bimabagaskhoro.taskappphincon.databinding.ActivityPaymentBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.CartActivity.Companion.EXTRA_DATA_CART
import com.bimabagaskhoro.taskappphincon.ui.activity.CartActivity.Companion.EXTRA_DATA_CART_NAME
import com.bimabagaskhoro.taskappphincon.ui.activity.DetailActivity.Companion.EXTRA_DATA_DETAIL
import com.bimabagaskhoro.taskappphincon.ui.activity.DetailActivity.Companion.EXTRA_DATA_PAYMENT_TO_BTN
import com.bimabagaskhoro.taskappphincon.ui.activity.DetailActivity.Companion.EXTRA_NAME_PAYMENT_TO_BTN
import com.bimabagaskhoro.taskappphincon.ui.adapter.payment.PaymentHeaderAdapter
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var adapter: PaymentHeaderAdapter
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private var idProduct: Int? = 0

    private val viewModel by viewModels<FRCViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getIntExtra(EXTRA_DATA_PAYMENT, 0)
        idProduct = productId

        initAdapter(productId)
        toDoActivity(productId)
        observeData()
    }

    private fun observeData() {
        viewModel.state.observe(this@PaymentActivity) { response ->
            when (response) {
                is com.bimabagaskhoro.phincon.core.utils.Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvHeaderPayment.visibility = View.INVISIBLE
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvHeaderPayment.visibility = View.VISIBLE
                    try {
                        val dataList = Gson().fromJson<ArrayList<PaymentModel>>(
                            response.data,
                            object : TypeToken<ArrayList<PaymentModel>>() {}.type
                        )
                        initRecyclerview(dataList)
                    } catch (e: Exception) {
                        Toast.makeText(this, "dataRemoteConfig null", Toast.LENGTH_SHORT).show()
                    }
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.rvHeaderPayment.visibility = View.INVISIBLE
                    Toast.makeText(this@PaymentActivity, "Error Data", Toast.LENGTH_SHORT).show()
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Empty -> {
                    Log.d("observeData", "observeData: Empty Data")
                }
            }
        }
    }

    private fun toDoActivity(productId: Int?) {
        if (idProduct != 0) {
            val data: Uri? = intent?.data
            val id = data?.getQueryParameter("id")
            idProduct = id?.toInt()
            binding.btnBack.setOnClickListener { doAction(true, productId) }
        } else {
            binding.btnBack.setOnClickListener { doAction(false, null) }
        }
    }

    private fun initAdapter(productId: Int) {
        adapter = if (productId == 0) {
            PaymentHeaderAdapter { dataItem ->
                val intent = Intent(this@PaymentActivity, CartActivity::class.java)
                intent.putExtra(EXTRA_DATA_CART, dataItem.id)
                intent.putExtra(EXTRA_DATA_CART_NAME, dataItem.name)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        } else {
            PaymentHeaderAdapter { dataItem ->
                val intent = Intent(this@PaymentActivity, DetailActivity::class.java)
                intent.putExtra(EXTRA_DATA_DETAIL, productId)
                intent.putExtra(EXTRA_DATA_PAYMENT_TO_BTN, dataItem.id)
                intent.putExtra(EXTRA_NAME_PAYMENT_TO_BTN, dataItem.name)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun initRecyclerview(dataList: List<PaymentModel>?) {
        binding.apply {
            rvHeaderPayment.setHasFixedSize(true)
            rvHeaderPayment.adapter = adapter
            dataList?.sortedBy { it.order }?.let { adapter.setData(it) }
        }
    }

    private fun doAction(fromDetail: Boolean, productId: Int?) {
        if (fromDetail) {
            binding.btnBack.setOnClickListener {
                val intent = Intent(this@PaymentActivity, DetailActivity::class.java)
                intent.putExtra(EXTRA_DATA_DETAIL, productId)
                startActivity(intent)
                finish()
            }
        } else if (!fromDetail) {
            binding.btnBack.setOnClickListener {
                val intent = Intent(this@PaymentActivity, CartActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_DATA_PAYMENT = "extra_data_payment"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}