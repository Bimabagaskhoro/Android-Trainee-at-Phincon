package com.bimabagaskhoro.taskappphincon.ui.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestRating
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.databinding.ActivityOnSuccessBinding
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class OnSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnSuccessBinding
    private val viewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getIntExtra(EXTRA_DATA_SUCCESS, 0)
//        binding.tvSuccess.text = productId.toString()
        binding.button.setOnClickListener {
            doUpdateRate(productId)
        }
    }

    private fun doUpdateRate(productId: Int) {
        val rating = binding.ratingBar.rating
        viewModel.updateRating(productId, RequestRating(rating.toString())).observe(this@OnSuccessActivity) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(this@OnSuccessActivity, R.string.succes_rating, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
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
                        AlertDialog.Builder(this@OnSuccessActivity).setTitle("Failed")
                            .setMessage(messageErr).setPositiveButton("Ok") { _, _ ->
                            }.show()
                    } catch (e: java.lang.Exception) {
                        val err = result.errorCode
                        Log.d("ErrorCode", "$err")
                    }
                }
                is Resource.Empty -> {
                    binding.progressbar.visibility = View.GONE
                    Log.d("DetailActivity", "Empty Data")
                }
            }
        }
    }

    companion object {
        const val EXTRA_DATA_SUCCESS = "extra_data_success"
    }
}