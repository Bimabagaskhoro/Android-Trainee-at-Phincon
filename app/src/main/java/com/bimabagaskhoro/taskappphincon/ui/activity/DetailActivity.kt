package com.bimabagaskhoro.taskappphincon.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Html.fromHtml
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.DataDetail
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.ImageProductItem
import com.bimabagaskhoro.taskappphincon.databinding.ActivityDetailBinding
import com.bimabagaskhoro.taskappphincon.ui.adapter.ImageSliderAdapter
import com.bimabagaskhoro.taskappphincon.ui.detail.BuyDialogFragment
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.bimabagaskhoro.taskappphincon.vm.DataStoreViewModel
import com.bimabagaskhoro.taskappphincon.vm.LocalViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: AuthViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private val roomViewModel: LocalViewModel by viewModels()
    private var idProduct : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDataDetail()
        binding.apply {
            btnBack.setOnClickListener { finish() }
        }
    }

    private fun initDataDetail() {
        val productId = intent.getIntExtra(EXTRA_DATA_DETAIL, 0)

        idProduct = productId
        if (idProduct == 0){
            val data: Uri? = intent?.data
            val id = data?.getQueryParameter("id")
            if (id != null) {
                idProduct = id.toInt()
            }
        }

        dataStoreViewModel.apply {
            getUserId.observe(this@DetailActivity) {
                val userId = it
                viewModel.getDetail(idProduct!!.toInt(), userId).observe(this@DetailActivity) { results ->
                    when (results) {
                        is Resource.Loading -> {
                            binding.apply {
                                progressBar.visibility = View.VISIBLE
                                tvDescHelpers.visibility = View.GONE
                                tvWeightHelpers.visibility = View.GONE
                                tvSizeHelpers.visibility = View.GONE
                                tvTypeHelpers.visibility = View.GONE
                                tvStockHelpers.visibility = View.GONE
                                textView12.visibility = View.GONE
                                textView13.visibility = View.GONE
                                textView14.visibility = View.GONE
                                textView15.visibility = View.GONE
                                textView16.visibility = View.GONE
                                view2.visibility = View.GONE
                                ratingBar.visibility = View.GONE
                                tvDetailProduct.visibility = View.GONE
                                imgFavorite.visibility =View.GONE
                                tvNameDetail.visibility = View.GONE

                            }
                        }
                        is Resource.Success -> {
                            val data = results.data!!.success.data
                            binding.apply {
                                progressBar.visibility = View.GONE
                                tvNameDetail.visibility = View.VISIBLE
                                tvDescHelpers.visibility = View.VISIBLE
                                tvWeightHelpers.visibility = View.VISIBLE
                                tvSizeHelpers.visibility = View.VISIBLE
                                tvTypeHelpers.visibility = View.VISIBLE
                                tvStockHelpers.visibility = View.VISIBLE
                                view2.visibility = View.VISIBLE
                                textView12.visibility = View.VISIBLE
                                textView13.visibility = View.VISIBLE
                                textView14.visibility = View.VISIBLE
                                textView15.visibility = View.VISIBLE
                                textView16.visibility = View.VISIBLE
                                ratingBar.visibility = View.VISIBLE
                                tvDetailProduct.visibility = View.VISIBLE
                                imgFavorite.visibility =View.VISIBLE

                                tvNameDetail.isSelected = true
                                tvNameDetail.text = data.name_product
                                tvTittle.isSelected = true
                                tvTittle.text = data.name_product
                                ratingBar.rating = data.rate.toFloat()
                                tvPrice.text = data.harga.formatterIdr()
                                tvStock.text = data.stock.toString()
                                tvSize.text = data.size
                                tvWeight.text = data.weight
                                tvType.text = data.type
                                tvDesc.text = data.desc
                                cardImage.adapter = ImageSliderAdapter(this@DetailActivity, data.image_product)
                                dotsIndicator.attachTo(cardImage)

                                imgFavorite.isChecked = data.isFavorite

                                if (data.stock == 1) {
                                    tvStock.text = getString(R.string.out_stock)
                                }
                                btnShare.setOnClickListener {
                                    val shareIntent = Intent(Intent.ACTION_SEND)
                                    shareIntent.type = "text/plain"
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, "https://bimabk.com/deeplink?id=${data.id}")
                                    startActivity(Intent.createChooser(shareIntent, "Share link using"))

                                }

                            }
                            binding.btnCart.setOnClickListener {
                                doActionCart(results.data.success.data)
                            }
                            initFavorite(userId, results.data.success.data, productId)
//                            doAction(results.data.success.data.image_product)
                            setActionDialog(results.data.success.data)
                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            try {
                                val err =
                                    results.errorBody?.string()
                                        ?.let { it1 -> JSONObject(it1).toString() }
                                val gson = Gson()
                                val jsonObject = gson.fromJson(err, JsonObject::class.java)
                                val errorResponse =
                                    gson.fromJson(jsonObject, ResponseError::class.java)
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
                            binding.progressBar.visibility = View.GONE
                            Log.d("DetailActivity", "Empty Data")
                        }
                    }
                }
            }
        }
    }

    private fun initFavorite(userId: Int, data: DataDetail, productId: Int) {
        binding.imgFavorite.setOnClickListener {
            if (data.isFavorite) {
                unFavorite(userId, productId)
            } else {
                addFavorite(userId, productId)
            }
        }
    }

    private fun addFavorite(userId: Int, productId: Int) {
        viewModel.addFavorite(userId, productId).observe(this@DetailActivity) { results ->
            when (results) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Log.d("addFavoriteLoading", "Loading")
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@DetailActivity,
                        R.string.succes_favorite,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    try {
                        val err =
                            results.errorBody?.string()
                                ?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse =
                            gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        Log.d("Error Body", messageErr)
                    } catch (e: java.lang.Exception) {
                        val err = results.errorCode
                        Log.d("ErrorCode", "$err")
                    }
                }
                is Resource.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("addFavorite", "Empty Data")
                }

            }
        }
    }

    private fun unFavorite(userId: Int, productId: Int) {
        viewModel.unFavorite(userId, productId).observe(this@DetailActivity) { results ->
            when (results) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Log.d("unFavoriteLoading", "Loading")
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@DetailActivity,
                        R.string.delete_favorite,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    try {
                        val err =
                            results.errorBody?.string()
                                ?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse =
                            gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        Log.d("Error Body", messageErr)
                    } catch (e: java.lang.Exception) {
                        val err = results.errorCode
                        Log.d("ErrorCode", "$err")
                    }
                }
                is Resource.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("unFavorite", "Empty Data")
                }

            }
        }
    }

    private fun doActionCart(data: DataDetail) {
        val idProduct = data.id
        val nameProduct = data.name_product
        val priceProduct = data.harga
        val imageProduct = data.image
        val quantityProduct = 1

        if (idProduct == idProduct) {

        }
        val cart = CartEntity(idProduct, nameProduct, priceProduct, imageProduct, quantityProduct)
        roomViewModel.insertCart(cart)
        Toast.makeText(this, R.string.succes_trolley, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@DetailActivity, MainActivity::class.java))
        finish()
    }

    private fun setActionDialog(data: DataDetail) {
        binding.btnBuy.setOnClickListener {
            val showData = BuyDialogFragment(data)
            showData.show(supportFragmentManager, DetailActivity::class.java.simpleName)
        }

    }

    companion object {
        const val EXTRA_DATA_DETAIL = "extra_data_detail"
    }
}