package com.bimabagaskhoro.taskappphincon.ui.activity

import android.app.AlertDialog
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

    private lateinit var adapter: ImageSliderAdapter
    private lateinit var dots: ArrayList<TextView>

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
        dataStoreViewModel.apply {
            getUserId.observe(this@DetailActivity) {
                val userId = it
                viewModel.getDetail(productId, userId).observe(this@DetailActivity) { results ->
                    when (results) {
                        is Resource.Loading -> {
                            binding.apply {
                                progressBar.visibility = View.VISIBLE
                            }
                        }
                        is Resource.Success -> {
                            val data = results.data!!.success.data
                            binding.apply {
                                progressBar.visibility = View.GONE
                                tvTittle.isSelected = true
                                tvTittle.text = data.name_product
                                ratingBar.rating = data.rate.toFloat()
                                tvPrice.text = data.harga.formatterIdr()
                                tvStock.text = data.stock.toString()
                                tvSize.text = data.size
                                tvWeight.text = data.weight
                                tvType.text = data.type
                                tvDesc.text = data.desc
                                val isFav = data.isFavorite
                                if (!isFav) {
                                    imgUnFavorite.visibility = View.VISIBLE
                                } else if (isFav) {
                                    imgFavorite.visibility = View.VISIBLE
                                }

                            }
                            binding.btnCart.setOnClickListener {
                                doActionCart(results.data.success.data)
                            }
                            binding.imgUnFavorite.setOnClickListener {
                                initFavorite(userId, results.data.success.data, productId)
                            }
                            doAction(results.data.success.data.image_product)
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
        if (data.isFavorite) {
            binding.imgFavorite.isClickable = true
            viewModel.addFavorite(productId, userId).observe(this@DetailActivity) { result ->
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
                            imgFavorite.visibility = View.VISIBLE
                            imgUnFavorite.visibility = View.INVISIBLE
                            imgUnFavorite.isClickable = false
                        }

                        val dataMessages = result.data!!.success.message
                        AlertDialog.Builder(this@DetailActivity)
                            .setTitle("Delete From Favorite Success")
                            .setMessage(dataMessages)
                            .setPositiveButton("Ok") { _, _ ->
                            }
                            .show()
                    }
                    is Resource.Error -> {
                        binding.apply {
                            progressbarAddFav.visibility = View.GONE
                            cardProgressbar.visibility = View.GONE
                            tvWaiting.visibility = View.GONE
                        }
                        try {
                            val err =
                                result.errorBody?.string()
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
                            val err = result.errorCode
                            Log.d("ErrorCode", "$err")
                        }
                    }
                    is Resource.Empty -> {
                        Log.d("DetailActivity", "Empty Data")
                    }
                }
            }
        } else if (!data.isFavorite) {
            binding.imgUnFavorite.isClickable = true
            viewModel.unFavorite(productId, userId).observe(this@DetailActivity) { result ->
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
                            imgUnFavorite.visibility = View.VISIBLE
                            imgFavorite.visibility = View.INVISIBLE
                            imgFavorite.isClickable = false
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
                        binding.apply {
                            progressbarAddFav.visibility = View.GONE
                            cardProgressbar.visibility = View.GONE
                            tvWaiting.visibility = View.GONE
                        }
                        try {
                            val err =
                                result.errorBody?.string()
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

    private fun doActionCart(data: DataDetail) {
        val idProduct = data.id
        val nameProduct = data.name_product
        val priceProduct = data.harga
        val imageProduct = data.image
        val quantityProduct = 1

        val cart = CartEntity(idProduct, nameProduct, priceProduct, imageProduct, quantityProduct)
        roomViewModel.insertCart(cart)
        Toast.makeText(this, R.string.succes_trolley, Toast.LENGTH_SHORT).show()
    }

    private fun doAction(list: List<ImageProductItem>) {
        adapter = ImageSliderAdapter(list)
        binding.cardImage.adapter = adapter
        dots = ArrayList()
        setIndicator(list)
        binding.cardImage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectedDot(position, list)
                super.onPageSelected(position)
            }
        })
    }

    private fun selectedDot(position: Int, list: List<ImageProductItem>) {
        for (i in list.indices) {
            if (i == position) {
                dots[i].setTextColor(ContextCompat.getColor(this, R.color.pinkz))
            } else {
                dots[i].setTextColor(ContextCompat.getColor(this, R.color.lighter_grey))
            }
        }
    }


    private fun setIndicator(list: List<ImageProductItem>) {
        for (i in list.indices) {
            dots.add(TextView(this))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dots[i].text = fromHtml("&#9679", Html.FROM_HTML_MODE_LEGACY).toString()
            } else {
                dots[i].text = fromHtml("&#9679")
            }
            dots[i].textSize = 18f
            binding.dotsIndicator.addView(dots[i])
        }
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