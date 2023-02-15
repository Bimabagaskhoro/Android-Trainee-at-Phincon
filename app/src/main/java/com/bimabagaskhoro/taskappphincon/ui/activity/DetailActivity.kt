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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ProductHistoryAdapter()

        initDataDetail()
        binding.apply {
            btnBack.setOnClickListener {
                val intent = Intent(this@DetailActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
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

        val dataPayment = intent.getStringExtra(EXTRA_DATA_PAYMENT_TO_BTN)
        val namePayment = intent.getStringExtra(EXTRA_NAME_PAYMENT_TO_BTN)
        Log.d("Detail_dataPayment", "$dataPayment")

        dataStoreViewModel.apply {
            getUserId.observe(this@DetailActivity) {
                val userId = it
                idProduct?.let { it1 ->
                    viewModel.getDetail(it1, userId)
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
                                    val data = results.data?.success?.data
                                    binding.apply {
                                        progressBar.visibility = View.GONE
                                        swipeRefresh.isRefreshing = false
                                        toolbar.visibility = View.VISIBLE
                                        layBtn.visibility = View.VISIBLE
                                        swipeRefresh.visibility = View.VISIBLE

                                        tvNameDetail.isSelected = true
                                        tvNameDetail.text = data?.name_product
                                        tvTittle.isSelected = true
                                        tvTittle.text = data?.name_product
                                        ratingBar.rating = data?.rate?.toFloat()!!
                                        tvPrice.text = data.harga?.formatterIdr()
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
                                            data.image?.let { it2 ->
                                                data.name_product?.let { it3 ->
                                                    data.weight?.let { it4 ->
                                                        data.size?.let { it5 ->
                                                            shareDeepLink(
                                                                it2,
                                                                it3,
                                                                data.stock.toString(),
                                                                it4,
                                                                it5,
                                                                "https://bimabk.com/deeplink?id=${data.id}"
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    binding.btnCart.setOnClickListener {
                                        results.data?.success?.data?.let { it2 -> doActionCart(it2) }
                                    }
                                    results.data?.success?.data?.let { it2 ->
                                        initFavorite(
                                            userId,
                                            it2, productId
                                        )
                                    }
                                    results.data?.success?.data?.let { it2 ->
                                        setActionDialog(it2, dataPayment, namePayment)
                                    }
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
                                            .setMessage(messageErr)
                                            .setPositiveButton("Ok") { _, _ ->
                                            }.show()
                                    } catch (e: Exception) {
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
                        messageErr?.let { Log.d("Error Body", it) }
                    } catch (e: Exception) {
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
                        messageErr?.let { Log.d("Error Body", it) }
                    } catch (e: Exception) {
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
        val totalPrice = data.harga?.toInt()
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

    private fun setActionDialog(data: DataDetail, dataPayment: String?, namePayment: String?) {
        if (dataPayment != null && namePayment !== null) {
            val showData = BuyDialogFragment(data, dataPayment, namePayment)
            showData.show(supportFragmentManager, DetailActivity::class.java.simpleName)
        } else {
            binding.btnBuy.setOnClickListener {
                val showData = BuyDialogFragment(data, dataPayment, namePayment)
                showData.show(supportFragmentManager, DetailActivity::class.java.simpleName)
            }
        }
    }

    private fun initViewModelHistory(idUser: Int?) {
        idUser?.let {
            viewModel.getHistoryProduct(it).observe(this) { data ->
                when (data) {
                    is Resource.Loading -> {
                        Log.d("getHistoryProduct", "isLoading")
                    }
                    is Resource.Success -> {
                        if (data.data?.success?.data?.isNotEmpty() == true) {
                            adapter.setData(data.data.success.data.sortedBy { it1 -> it1.name_product })
                            initRecyclerViewHistory()
                        }
                        /**
                         * do action
                         */
                        else if (data.data?.success?.data?.isEmpty() == true) {
                            binding.apply {
                                viewHelper2.visibility = View.GONE
                                tvTittleSticky2.visibility = View.GONE
                                rvHistoryProduct.visibility = View.GONE
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
                        } catch (e: Exception) {
                            val err = data.errorCode
                            Log.d("ErrorCode", "$err")
                        }
                    }
                    is Resource.Empty -> {
                        Log.d("getHistoryProduct", "isEmpty")
                        binding.apply {
                            viewHelper2.visibility = View.GONE
                            tvTittleSticky2.visibility = View.GONE
                            rvHistoryProduct.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun initViewModelOther(idUser: Int?) {
        idUser?.let {
            viewModel.getOtherProduct(it).observe(this) { data ->
                when (data) {
                    is Resource.Loading -> {
                        Log.d("getHistoryProduct", "isLoading")
                    }
                    is Resource.Success -> {
                        if (data.data?.success?.data?.isNotEmpty() == true) {
                            adapter.setData(data.data.success.data.sortedBy { it1 -> it1.name_product })
                            initRecyclerViewOther()
                        }
                        else if (data.data?.success?.data?.isEmpty() == true) {
                            binding.apply {
                                viewHelper1.visibility = View.GONE
                                rvOtherProduct.visibility = View.GONE
                                tvTittleSticky.visibility = View.GONE
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
                        } catch (e: Exception) {
                            val err = data.errorCode
                            Log.d("ErrorCode", "$err")
                        }
                    }
                    is Resource.Empty -> {
                        Log.d("getHistoryProduct", "isEmpty")
                        binding.apply {
                            viewHelper1.visibility = View.GONE
                            rvOtherProduct.visibility = View.GONE
                            tvTittleSticky.visibility = View.GONE
                        }
                    }
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
        const val EXTRA_DATA_PAYMENT_TO_BTN = "extra_data_payment_to_btn"
        const val EXTRA_NAME_PAYMENT_TO_BTN = "extra_name_payment_to_btn"
    }

    override fun onClick(image: String) {
        seePhoto.showPhoto(image)
    }
}