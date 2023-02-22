package com.bimabagaskhoro.taskappphincon.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bimabagaskhoro.phincon.core.data.source.remote.response.RequestRating
import com.bimabagaskhoro.phincon.core.data.source.remote.response.ResponseError
import com.bimabagaskhoro.phincon.core.utils.formatterIdr
import com.bimabagaskhoro.phincon.core.vm.RemoteViewModel
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.ActivityOnSuccessBinding
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class OnSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnSuccessBinding
    private val viewModel: RemoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDataPayment()
        initDataPaymentFromBottomCard()


        val productId = intent.getIntExtra(EXTRA_DATA_SUCCESS, 0)
        val listProductId = intent.getStringArrayListExtra(EXTRA_DATA_SUCCESS_ID)

        if (productId != 0) {
            val totalPriceDetail = intent.getStringExtra(EXTRA_DATA_PRICE)
            binding.tvTotalPriceIdr.text = totalPriceDetail?.formatterIdr()
        } else {
            val totalPriceTrolley = intent.getStringExtra(EXTRA_DATA_PRICE_TROLLEY)
            binding.tvTotalPriceIdr.text = totalPriceTrolley?.formatterIdr()
        }

        binding.button.setOnClickListener {
            val rating = binding.ratingBar.rating
            if (productId != 0) {
                viewModel.updateRating(productId, RequestRating(rating.toString()))
                    .observe(this@OnSuccessActivity) { result ->
                        when (result) {
                            is com.bimabagaskhoro.phincon.core.utils.Resource.Loading -> {
                                binding.progressbar.visibility = View.VISIBLE
                            }
                            is com.bimabagaskhoro.phincon.core.utils.Resource.Success -> {
                                binding.progressbar.visibility = View.GONE
                                Toast.makeText(
                                    this@OnSuccessActivity,
                                    R.string.succes_rating,
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }
                            is com.bimabagaskhoro.phincon.core.utils.Resource.Error -> {
                                try {
                                    binding.progressbar.visibility = View.GONE
                                    val err =
                                        result.errorBody?.string()
                                            ?.let { it1 -> JSONObject(it1).toString() }
                                    val gson = Gson()
                                    val jsonObject = gson.fromJson(err, JsonObject::class.java)
                                    val errorResponse =
                                        gson.fromJson(jsonObject, ResponseError::class.java)
                                    val messageErr = errorResponse.error.message
                                    AlertDialog.Builder(this@OnSuccessActivity).setTitle("Failed")
                                        .setMessage(messageErr).setPositiveButton("Ok") { _, _ ->
                                        }.show()
                                } catch (t: Throwable) {
                                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                                }
                            }
                            is com.bimabagaskhoro.phincon.core.utils.Resource.Empty -> {
                                binding.progressbar.visibility = View.GONE
                                Log.d("DetailActivity", "Empty Data")
                            }
                        }
                    }
            } else {
                for (i in listProductId!!.indices) {
                    viewModel.updateRating(
                        listProductId[i].toInt(),
                        RequestRating(rating.toString())
                    )
                        .observe(this@OnSuccessActivity) { result ->
                            when (result) {
                                is com.bimabagaskhoro.phincon.core.utils.Resource.Loading -> {
                                    binding.progressbar.visibility = View.VISIBLE
                                }
                                is com.bimabagaskhoro.phincon.core.utils.Resource.Success -> {
                                    binding.progressbar.visibility = View.GONE
                                    Toast.makeText(
                                        this@OnSuccessActivity,
                                        R.string.succes_rating,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finishAffinity()
                                }
                                is com.bimabagaskhoro.phincon.core.utils.Resource.Error -> {
                                    binding.progressbar.visibility = View.GONE
                                    try {
                                        val err =
                                            result.errorBody?.string()
                                                ?.let { it1 -> JSONObject(it1).toString() }
                                        val gson = Gson()
                                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                                        val errorResponse =
                                            gson.fromJson(jsonObject, ResponseError::class.java)
                                        val messageErr = errorResponse.error.message
                                        AlertDialog.Builder(this@OnSuccessActivity)
                                            .setTitle("Failed")
                                            .setMessage(messageErr)
                                            .setPositiveButton("Ok") { _, _ ->
                                            }.show()
                                    } catch (t: Throwable) {
                                        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                is com.bimabagaskhoro.phincon.core.utils.Resource.Empty -> {
                                    binding.progressbar.visibility = View.GONE
                                    Log.d("DetailActivity", "Empty Data")
                                }
                            }
                        }
                }
            }
        }


    }

    private fun initDataPaymentFromBottomCard() {
        val dataPayment = intent.getStringExtra(EXTRA_DATA_SUCCESS_ID_PAYMENT)
        val dataName = intent.getStringExtra(EXTRA_DATA_SUCCESS_NAME)

        if (dataPayment == null && dataName == null) {
            Log.d("initDataPayment", "Ada Error")
        } else {
            dataPayment?.let { initImagePayment(it) }
            binding.tvPaymentMethode.text = dataName
        }
    }

    private fun initDataPayment() {
        val dataPayment = intent.getStringExtra(EXTRA_ID_SUCCESS)
        val dataName = intent.getStringExtra(EXTRA_NAME_SUCCESS)

        if (dataPayment == null && dataName == null) {
            Log.d("initDataPayment", "Ada Error")
        } else {
            dataPayment?.let { initImagePayment(it) }
            binding.tvPaymentMethode.text = dataName
        }
    }

    private fun initImagePayment(dataPayment: String) {
        binding.apply {
            when (dataPayment) {
                "va_bca" -> {
                    Glide.with(this@OnSuccessActivity)
                        .asBitmap()
                        .load(R.drawable.bca)
                        .into(imgPaymentMethode)
                }
                "va_mandiri" -> {
                    Glide.with(this@OnSuccessActivity)
                        .asBitmap()
                        .load(R.drawable.mandiri)
                        .into(imgPaymentMethode)
                }
                "va_bri" -> {
                    Glide.with(this@OnSuccessActivity)
                        .asBitmap()
                        .load(R.drawable.bri)
                        .into(imgPaymentMethode)
                }
                "va_bni" -> {
                    Glide.with(this@OnSuccessActivity)
                        .asBitmap()
                        .load(R.drawable.bni)
                        .into(imgPaymentMethode)
                }
                "va_btn" -> {
                    Glide.with(this@OnSuccessActivity)
                        .asBitmap()
                        .load(R.drawable.btn)
                        .into(imgPaymentMethode)
                }
                "va_danamon" -> {
                    Glide.with(this@OnSuccessActivity)
                        .asBitmap()
                        .load(R.drawable.danamon)
                        .into(imgPaymentMethode)
                }
                "ewallet_gopay" -> {
                    Glide.with(this@OnSuccessActivity)
                        .asBitmap()
                        .load(R.drawable.gopay)
                        .into(imgPaymentMethode)
                }
                "ewallet_ovo" -> {
                    Glide.with(this@OnSuccessActivity)
                        .asBitmap()
                        .load(R.drawable.ovo)
                        .into(imgPaymentMethode)
                }
                "ewallet_dana" -> {
                    Glide.with(this@OnSuccessActivity)
                        .asBitmap()
                        .load(R.drawable.dana)
                        .into(imgPaymentMethode)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_DATA_SUCCESS = "extra_data_success"
        const val EXTRA_ID_SUCCESS = "extra_id_success"
        const val EXTRA_NAME_SUCCESS = "extra_name_success"
        const val EXTRA_DATA_PRICE = "extra_data_price"

        const val EXTRA_DATA_SUCCESS_NAME = "extra_data_success_id_name"
        const val EXTRA_DATA_SUCCESS_ID_PAYMENT = "extra_data_success_id_payment"
        const val EXTRA_DATA_SUCCESS_ID = "extra_data_success_id"
        const val EXTRA_DATA_PRICE_TROLLEY = "extra_data_price_trolley"
    }
}