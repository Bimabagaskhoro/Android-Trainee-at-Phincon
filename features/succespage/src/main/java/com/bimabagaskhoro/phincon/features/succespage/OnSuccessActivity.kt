package com.bimabagaskhoro.phincon.features.succespage

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bimabagaskhoro.phincon.core.data.source.remote.response.RequestRating
import com.bimabagaskhoro.phincon.core.data.source.remote.response.ResponseError
import com.bimabagaskhoro.phincon.core.utils.Resource
import com.bimabagaskhoro.phincon.core.utils.formatterIdr
import com.bimabagaskhoro.phincon.core.vm.FGAViewModel
import com.bimabagaskhoro.phincon.core.vm.RemoteViewModel
import com.bimabagaskhoro.phincon.features.succespage.databinding.ActivityOnSuccessBinding
import com.bimabagaskhoro.phincon.router.ActivityRouter
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class OnSuccessActivity : AppCompatActivity() {

    @Inject
    lateinit var router: ActivityRouter

    private lateinit var binding: ActivityOnSuccessBinding
    private val viewModel: RemoteViewModel by viewModels()
    private val analyticViewModel: FGAViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDataPayment()
        initDataPaymentFromBottomCard()


        val productId = intent.getIntExtra(EXTRA_DATA_SUCCESS, 0)
        val listProductId = intent.getStringArrayListExtra(EXTRA_DATA_LIST_ID_FROM_TROLLEY)

        if (productId != 0) {
            val totalPriceDetail = intent.getStringExtra(EXTRA_DATA_PRICE)
            binding.tvTotalPriceIdr.text = totalPriceDetail?.formatterIdr()
        } else {
            val totalPriceTrolley = intent.getStringExtra(EXTRA_DATA_RESULT_PRICE_FROM_TROLLEY)
            binding.tvTotalPriceIdr.text = totalPriceTrolley?.formatterIdr()
        }

        binding.button.setOnClickListener {
            val rating = binding.ratingBar.rating
            if (productId != 0) {
                viewModel.updateRating(productId, RequestRating(rating.toString()))
                    .observe(this@OnSuccessActivity) { result ->
                        when (result) {
                            is Resource.Loading -> {
                                binding.progressbar.visibility = View.VISIBLE
                            }
                            is Resource.Success -> {
                                binding.progressbar.visibility = View.GONE
                                Toast.makeText(
                                    this@OnSuccessActivity,
                                    R.string.succes_rating,
                                    Toast.LENGTH_SHORT
                                ).show()

                                startActivity(router.toHomeActivity(this).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    finishAffinity()
                                })
                                val ratingAnly = binding.ratingBar.rating.toInt()
                                analyticViewModel.onClickBtnSuccessPage(ratingAnly)
                            }
                            is Resource.Error -> {
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
                                    Toast.makeText(
                                        this,
                                        "No Internet Connection",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is Resource.Empty -> {
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
                                is Resource.Loading -> {
                                    binding.progressbar.visibility = View.VISIBLE
                                }
                                is Resource.Success -> {
                                    binding.progressbar.visibility = View.GONE
                                    Toast.makeText(
                                        this@OnSuccessActivity,
                                        R.string.succes_rating,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(router.toHomeActivity(this).apply {
                                        flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        finishAffinity()
                                    })
                                    val ratingAnly = binding.ratingBar.rating.toInt()
                                    analyticViewModel.onClickBtnSuccessPage(ratingAnly)
                                    finishAffinity()
                                }
                                is Resource.Error -> {
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
                                        Toast.makeText(
                                            this,
                                            "No Internet Connection",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                is Resource.Empty -> {
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
        val dataPayment = intent.getStringExtra(EXTRA_DATA_NAME_FROM_TROLLEY)
        val dataName = intent.getStringExtra(EXTRA_DATA_NAME_FROM_TROLLEY)

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

    override fun onResume() {
        super.onResume()
        val nameScreen = this.javaClass.simpleName
        analyticViewModel.onLoadScreenSuccessPage(nameScreen)
    }

    companion object {
        const val EXTRA_DATA_SUCCESS = "extra_data_success"
        const val EXTRA_ID_SUCCESS = "extra_id_success"
        const val EXTRA_NAME_SUCCESS = "extra_name_success"
        const val EXTRA_DATA_PRICE = "extra_data_price"
        fun createIntentFromDetail(
            context: Context,
            idProduct: Int,
            dataName: String,
            dataPayment: String,
            resultPrice: Int
        ): Intent {
            return Intent(context, OnSuccessActivity::class.java).apply {
                putExtra(EXTRA_ID_SUCCESS, idProduct)
                putExtra(EXTRA_NAME_SUCCESS, dataName)
                putExtra(EXTRA_DATA_PRICE, dataPayment)
                putExtra(EXTRA_DATA_SUCCESS, resultPrice)
            }
        }

        private const val EXTRA_DATA_LIST_ID_FROM_TROLLEY = "extra_data_success_id"
        private const val EXTRA_DATA_NAME_FROM_TROLLEY = "extra_data_success_id_name"
        private const val EXTRA_DATA_PAYMENT_FROM_TROLLEY = "extra_data_success_id_payment"
        private const val EXTRA_DATA_RESULT_PRICE_FROM_TROLLEY = "extra_data_price_trolley"

        fun createIntentFromTrolley(
            context: Context,
            listOfProductId: ArrayList<String>,
            dataName: String,
            dataPayment: String,
            resultPrice: String
        ): Intent {
            return Intent(
                context, OnSuccessActivity::class.java
            ).apply {
                putExtra(EXTRA_DATA_LIST_ID_FROM_TROLLEY, listOfProductId)
                putExtra(EXTRA_DATA_NAME_FROM_TROLLEY, dataName)
                putExtra(EXTRA_DATA_PAYMENT_FROM_TROLLEY, dataPayment)
                putExtra(EXTRA_DATA_RESULT_PRICE_FROM_TROLLEY, resultPrice)
            }
        }

    }
}