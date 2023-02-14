package com.bimabagaskhoro.taskappphincon.firebase.payment

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.DataDetail
import com.bimabagaskhoro.taskappphincon.databinding.ActivityPaymentBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.CartActivity
import com.bimabagaskhoro.taskappphincon.ui.activity.CartActivity.Companion.EXTRA_DATA_CART
import com.bimabagaskhoro.taskappphincon.ui.activity.CartActivity.Companion.EXTRA_DATA_CART_NAME
import com.bimabagaskhoro.taskappphincon.ui.activity.DetailActivity
import com.bimabagaskhoro.taskappphincon.ui.activity.DetailActivity.Companion.EXTRA_DATA_DETAIL
import com.bimabagaskhoro.taskappphincon.ui.activity.DetailActivity.Companion.EXTRA_DATA_PAYMENT_TO_BTN
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var adapter: PaymentHeaderAdapter
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private var idProduct: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getIntExtra(EXTRA_DATA_PAYMENT, 0)
        idProduct = productId

        initRemoteConfig()
        initAdapter(productId)
        toDoActivity(productId)
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
                startActivity(intent)
            }
        } else {
            PaymentHeaderAdapter { dataItem ->
                val intent = Intent(this@PaymentActivity, DetailActivity::class.java)
                intent.putExtra(EXTRA_DATA_DETAIL, productId)
                intent.putExtra(EXTRA_DATA_PAYMENT_TO_BTN, dataItem.id)
                startActivity(intent)
            }
        }
    }

    private fun initRemoteConfig() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                getData()
                Log.d("dataRemoteConfig", "harusnya sukses")
            } else {
                Log.d("dataRemoteConfig", "Config params fetch failed")
            }
        }
    }

    private fun getData() {
        try {
            val dataRemoteConfig = remoteConfig.getString("payment_json")
            val dataList = Gson().fromJson<ArrayList<PaymentModel>>(
                dataRemoteConfig,
                object : TypeToken<ArrayList<PaymentModel>>() {}.type
            )
            initRecyclerview(dataList)
        } catch (e: Exception) {
            Toast.makeText(this, "dataRemoteConfig null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRecyclerview(dataList: List<PaymentModel>?) {
        binding.apply {
            rvHeaderPayment.setHasFixedSize(true)
            rvHeaderPayment.adapter = adapter
            dataList?.let { adapter.setData(it) }
        }
    }

    private fun doAction(fromDetail: Boolean, productId: Int?) {
        if (fromDetail) {
            binding.btnBack.setOnClickListener {
                val intent = Intent(this@PaymentActivity, DetailActivity::class.java)
                intent.putExtra(EXTRA_DATA_DETAIL, productId)
                startActivity(intent)

            }
        } else if (!fromDetail) {
            binding.btnBack.setOnClickListener {
                val intent = Intent(this@PaymentActivity, CartActivity::class.java)
                startActivity(intent)
            }
        }
    }

    companion object {
        const val EXTRA_DATA_PAYMENT = "extra_data_payment"
    }

}