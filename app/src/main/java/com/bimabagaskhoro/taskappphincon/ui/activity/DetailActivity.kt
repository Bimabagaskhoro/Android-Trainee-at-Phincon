package com.bimabagaskhoro.taskappphincon.ui.activity

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.databinding.ActivityDetailBinding
import com.bimabagaskhoro.taskappphincon.databinding.ActivityMainBinding
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.utils.formatDate
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.bimabagaskhoro.taskappphincon.vm.DataStoreViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_detail.view.*
import org.json.JSONObject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: AuthViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDataDetail()
        binding.apply {
            imgUnFavorite.setOnClickListener {
                initFavorite()
            }
            imgFavorite.visibility = View.GONE
        }
    }

    private fun initDataDetail() {
        val id = intent.getIntExtra(EXTRA_DATA_DETAIL, 0)
        viewModel.getDetail(id).observe(this@DetailActivity) { results ->
            when (results) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val data = results.data!!.success.data
                    binding.apply {
                        tvTittle.isSelected = true
                        tvTittle.text = data.name_product
                        ratingBar.rating = data.rate.toFloat()
                        tvPrice.text = data.harga.formatterIdr()
                        tvStock.text = data.stock.toString()
                        tvSize.text = data.size
                        tvWeight.text = data.weight
                        tvType.text = data.type
                        tvDesc.text = data.desc

                    }
                }
                is Resource.Error -> {
                    try {
                        val err =
                            results.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse = gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        AlertDialog.Builder(this@DetailActivity).setTitle("Failed")
                            .setMessage(messageErr).setPositiveButton("Ok") { _, _ ->
                            }.show()
                    } catch (e: java.lang.Exception) {
                        val err = results.errorCode
                        Log.d("ErrorCode", "$err")
                    }
                }
                is Resource.Empty -> {
                    Log.d("DetailActivity", "Empty Data")
                }
            }
        }
    }


    private fun initFavorite() {
        val idProduct = intent.getIntExtra(EXTRA_DATA_DETAIL, 0)
//        var userId = 0
        dataStoreViewModel.apply {
            getUserId.observe(this@DetailActivity) {
                val userId = it
                viewModel.addFavorite(idProduct, userId).observe(this@DetailActivity) { result ->
                    when (result) {
                        is Resource.Loading -> {
                            binding.apply {
                                progressbarAddFav.visibility = View.VISIBLE
                                cardProgressbar.visibility = View.VISIBLE
                                tvWaiting.visibility = View.VISIBLE
                            }
                        }
                        is Resource.Success -> {
                            binding.apply {
                                progressbarAddFav.visibility = View.GONE
                                cardProgressbar.visibility = View.GONE
                                tvWaiting.visibility = View.GONE
                            }

                            val dataMessages = result.data!!.success.message
                            AlertDialog.Builder(this@DetailActivity)
                                .setTitle("Add Favorite Success")
                                .setMessage(dataMessages)
                                .setPositiveButton("Ok") { _, _ ->
                                }
                                .show()
                        }
                        is Resource.Error -> {
                            try {
                                val err =
                                    result.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                                val gson = Gson()
                                val jsonObject = gson.fromJson(err, JsonObject::class.java)
                                val errorResponse = gson.fromJson(jsonObject, ResponseError::class.java)
                                val messageErr = errorResponse.error.message
                                AlertDialog.Builder(this@DetailActivity).setTitle("Failed")
                                    .setMessage(messageErr).setPositiveButton("Ok") { _, _ ->
                                    }.show()
                            } catch (e: java.lang.Exception) {
                                val err = result.errorCode
                                Log.d("ErrorCode", "$err")
                            }
                        }
                        is Resource.Empty -> {
                            Log.d("DetailActivity", "Empty Data")
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_DATA_DETAIL = "extra_data_detail"
    }
}