package com.bimabagaskhoro.taskappphincon.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.local.model.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.ResponseError
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.DataDetail
import com.bimabagaskhoro.taskappphincon.databinding.ActivityDetailBinding
import com.bimabagaskhoro.taskappphincon.ui.adapter.ImageSliderAdapter
import com.bimabagaskhoro.taskappphincon.ui.adapter.ProductHistoryAdapter
import com.bimabagaskhoro.taskappphincon.ui.dialog.bottomsheet.BuyDialogFragment
import com.bimabagaskhoro.taskappphincon.ui.dialog.photoview.PhotoViewFragment
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.bimabagaskhoro.taskappphincon.vm.DataStoreViewModel
import com.bimabagaskhoro.taskappphincon.vm.LocalViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_detail.view.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

@AndroidEntryPoint
class DetailActivity : AppCompatActivity(), ImageSliderAdapter.OnPageClickListener {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: AuthViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private val roomViewModel: LocalViewModel by viewModels()
    private var idProduct: Int? = null
    private lateinit var adapter: ProductHistoryAdapter
    private lateinit var seePhoto: PhotoViewFragment
    private lateinit var imageSliderAdapter: ImageSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ProductHistoryAdapter()

        initDataDetail()
        binding.apply {
            btnBack.setOnClickListener { finish() }
        }
        binding.swipeRefresh.setOnRefreshListener {
            initDataDetail()
            binding.progressBar.visibility = View.VISIBLE
        }
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        dataStoreViewModel.getUserId.observe(this@DetailActivity) {
            val idUser = it
            initViewModelHistory(idUser)
            initViewModelOther(idUser)
        }

        seePhoto = PhotoViewFragment(this)
    }

    private fun initDataDetail() {
        val productId = intent.getIntExtra(EXTRA_DATA_DETAIL, 0)

        idProduct = productId
        if (idProduct == 0) {
            val data: Uri? = intent?.data
            val id = data?.getQueryParameter("id")
            if (id != null) {
                idProduct = id.toInt()
            }
        }

        dataStoreViewModel.apply {
            getUserId.observe(this@DetailActivity) {
                val userId = it
                viewModel.getDetail(idProduct!!.toInt(), userId)
                    .observe(this@DetailActivity) { results ->
                        when (results) {
                            is Resource.Loading -> {
                                binding.apply {
                                    progressBar.visibility = View.VISIBLE
                                    toolbar.visibility = View.GONE
                                    layBtn.visibility = View.GONE
                                    swipeRefresh.visibility = View.GONE
                                }
                            }
                            is Resource.Success -> {
                                val data = results.data!!.success.data
                                binding.apply {
                                    progressBar.visibility = View.GONE
                                    swipeRefresh.isRefreshing = false
                                    toolbar.visibility = View.VISIBLE
                                    layBtn.visibility = View.VISIBLE
                                    swipeRefresh.visibility = View.VISIBLE

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

                                    cardImage.adapter = ImageSliderAdapter(
                                        this@DetailActivity,
                                        data.image_product,
                                        this@DetailActivity
                                    )
                                    dotsIndicator.attachTo(cardImage)

                                    imgFavorite.isChecked = data.isFavorite

                                    if (data.stock == 1) {
                                        tvStock.text = getString(R.string.out_stock)
                                    }

                                    btnShare.setOnClickListener {
                                        shareDeepLink(
                                            data.image,
                                            data.name_product,
                                            data.stock.toString(),
                                            data.weight,
                                            data.size,
                                            "https://bimabk.com/deeplink?id=${data.id}"
                                        )
                                    }
                                }
                                binding.btnCart.setOnClickListener {
                                    doActionCart(results.data.success.data)
                                }
                                initFavorite(userId, results.data.success.data, productId)
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

    private fun shareDeepLink(
        images: String,
        name: String,
        stock: String,
        weight: String,
        size: String,
        link: String
    ) {

        Picasso.get().load(images).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "${getString(R.string.name)} : $name\n${getString(R.string.stock)} : $stock\n${
                        getString(
                            R.string.weight
                        )
                    } : $weight\n${getString(R.string.size)} : $size\n${getString(R.string.url)} : $link"
                )
                intent.putExtra(Intent.EXTRA_STREAM, getBitmapFromView(bitmap))
                startActivity(Intent.createChooser(intent, "Share To"))

            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.d("IMG Downloader", "Bitmap Failed...")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.d("IMG Downloader", "Bitmap Preparing Load...")
            }
        })
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

    private fun getBitmapFromView(bmp: Bitmap?): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(this.externalCacheDir, System.currentTimeMillis().toString() + ".jpg")

            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.close()
            bmpUri = Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
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
        val stockProduct = data.stock
        val isCheck = 0
        val totalPrice = data.harga.toInt()
        val firstPrice = data.harga
        val cart = CartEntity(
            idProduct,
            nameProduct,
            priceProduct,
            imageProduct,
            quantityProduct,
            isCheck,
            stockProduct,
            totalPrice,
            firstPrice
        )

        if (stockProduct != 1) {
            roomViewModel.insertCart(cart)
            Toast.makeText(this, R.string.succes_trolley, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@DetailActivity, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, R.string.failed_trolley, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setActionDialog(data: DataDetail) {
        binding.btnBuy.setOnClickListener {
            val showData = BuyDialogFragment(data)
            showData.show(supportFragmentManager, DetailActivity::class.java.simpleName)
        }
    }

    private fun initViewModelHistory(idUser: Int?) {
        viewModel.getHistoryProduct(idUser!!).observe(this) { data ->
            when (data) {
                is Resource.Loading -> {
                    Log.d("getHistoryProduct", "isLoading")
                }
                is Resource.Success -> {
                    if (data.data!!.success.data.isNotEmpty()) {
                        adapter.setData(data.data.success.data.sortedBy { it.name_product })
                        initRecyclerViewHistory()
                    } else if (data.data.success.data.isEmpty()) {
                        binding.apply {
                            viewHelper1.visibility = View.INVISIBLE
                            viewHelper2.visibility = View.INVISIBLE
                            rvOtherProduct.visibility = View.INVISIBLE
                            rvHistoryProduct.visibility = View.INVISIBLE
                            tvTittleSticky.visibility = View.INVISIBLE
                            tvTittleSticky2.visibility = View.INVISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    try {
                        val err =
                            data.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse = gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        AlertDialog.Builder(this).setTitle("Failed")
                            .setMessage(messageErr).setPositiveButton("Ok") { _, _ ->
                            }.show()
                    } catch (e: java.lang.Exception) {
                        val err = data.errorCode
                        Log.d("ErrorCode", "$err")
                    }
                }
                is Resource.Empty -> {
                    Log.d("getHistoryProduct", "isEmpty")
                }
            }
        }
    }

    private fun initViewModelOther(idUser: Int?) {
        viewModel.getOtherProduct(idUser!!).observe(this) { data ->
            when (data) {
                is Resource.Loading -> {
                    Log.d("getHistoryProduct", "isLoading")
                }
                is Resource.Success -> {
                    if (data.data!!.success.data.isNotEmpty()) {
                        adapter.setData(data.data.success.data.sortedBy { it.name_product })
                        initRecyclerViewOther()
                    } else if (data.data.success.data.isEmpty()) {
                        binding.apply {
                            viewHelper1.visibility = View.INVISIBLE
                            viewHelper2.visibility = View.INVISIBLE
                            rvOtherProduct.visibility = View.INVISIBLE
                            rvHistoryProduct.visibility = View.INVISIBLE
                            tvTittleSticky.visibility = View.INVISIBLE
                            tvTittleSticky2.visibility = View.INVISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    try {
                        val err =
                            data.errorBody?.string()?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse = gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        AlertDialog.Builder(this).setTitle("Failed")
                            .setMessage(messageErr).setPositiveButton("Ok") { _, _ ->
                            }.show()
                    } catch (e: java.lang.Exception) {
                        val err = data.errorCode
                        Log.d("ErrorCode", "$err")
                    }
                }
                is Resource.Empty -> {
                    Log.d("getHistoryProduct", "isEmpty")
                }
            }
        }
    }

    private fun initRecyclerViewHistory() {
        binding.apply {
            rvHistoryProduct.adapter = adapter
            rvHistoryProduct.setHasFixedSize(true)

            adapter.onItemClick = {
                val intent = Intent(this@DetailActivity, DetailActivity::class.java)
                intent.putExtra(EXTRA_DATA_DETAIL, it.id)
                startActivity(intent)
            }
        }
    }

    private fun initRecyclerViewOther() {
        binding.apply {
            rvOtherProduct.adapter = adapter
            rvOtherProduct.setHasFixedSize(true)

            adapter.onItemClick = {
                val intent = Intent(this@DetailActivity, DetailActivity::class.java)
                intent.putExtra(EXTRA_DATA_DETAIL, it.id)
                startActivity(intent)
            }
        }
    }

    companion object {
        const val EXTRA_DATA_DETAIL = "extra_data_detail"
    }

    override fun onClick(image: String) {
        seePhoto.showPhoto(image)
    }
}