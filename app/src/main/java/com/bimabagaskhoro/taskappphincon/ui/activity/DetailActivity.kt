package com.bimabagaskhoro.taskappphincon.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Html.fromHtml
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bimabagaskhoro.taskappphincon.R
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
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.view.*
import org.json.JSONObject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: AuthViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    private lateinit var adapter: ImageSliderAdapter

    //    private val list = ArrayList<ImageProductItem>()
    private lateinit var dots: ArrayList<TextView>

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
            btnBack.setOnClickListener { finish() }
        }

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

    private fun initDataDetail() {
        val id = intent.getIntExtra(EXTRA_DATA_DETAIL, 0)
        viewModel.getDetail(id, 214).observe(this@DetailActivity) { results ->
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

                    }
                    doAction(results.data.success.data.image_product)
                    setActionDialog(results.data.success.data)
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
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
                    binding.progressBar.visibility = View.GONE
                    Log.d("DetailActivity", "Empty Data")
                }
            }
        }
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