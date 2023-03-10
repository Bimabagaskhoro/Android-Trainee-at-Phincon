package com.bimabagaskhoro.phincon.features.detail

import android.app.AlertDialog
import android.content.Context
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
import com.bimabagaskhoro.phincon.core.adapter.ImageSliderAdapter
import com.bimabagaskhoro.phincon.core.adapter.ProductHistoryAdapter
import com.bimabagaskhoro.phincon.core.data.source.local.model.CartEntity
import com.bimabagaskhoro.phincon.core.data.source.remote.response.ResponseError
import com.bimabagaskhoro.phincon.core.data.source.remote.response.detail.DataDetail
import com.bimabagaskhoro.phincon.core.utils.Resource
import com.bimabagaskhoro.phincon.core.utils.formatterIdr
import com.bimabagaskhoro.phincon.core.vm.DataStoreViewModel
import com.bimabagaskhoro.phincon.core.vm.FGAViewModel
import com.bimabagaskhoro.phincon.core.vm.LocalViewModel
import com.bimabagaskhoro.phincon.core.vm.RemoteViewModel
import com.bimabagaskhoro.phincon.features.detail.databinding.ActivityDetailBinding
import com.bimabagaskhoro.phincon.features.detail.dialog.bottomsheet.BuyDialogFragment
import com.bimabagaskhoro.phincon.features.detail.dialog.photoview.PhotoViewFragment
import com.bimabagaskhoro.phincon.router.ActivityRouter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailActivity : AppCompatActivity(), ImageSliderAdapter.OnPageClickListener {


    @Inject
    lateinit var router: ActivityRouter

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: RemoteViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private val roomViewModel: LocalViewModel by viewModels()
    private val analyticViewModel: FGAViewModel by viewModels()
    private var idProduct: Int? = null
    private lateinit var adapter: ProductHistoryAdapter
    private lateinit var seePhoto: PhotoViewFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ProductHistoryAdapter()

//            ini di log
        Log.d("onCreate", "mana dulu nich")
        initDataDetail()
        binding.apply {
            btnBack.setOnClickListener {
                analyticViewModel.onClickBackDetail()
                onSupportNavigateUp()
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
//            ini di log
            Log.d("dataStoreViewModel", "mana dulu nich")
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

                                        if (data.stock == 0) {
                                            tvStock.text = getString(R.string.out_stock)
                                        }

                                        btnShare.setOnClickListener {
                                            data.image?.let { dataImage ->
                                                data.name_product?.let { dataName ->
                                                    data.weight?.let { dataWeight ->
                                                        data.size?.let { dataSize ->
                                                            data.harga?.let { dataHarga ->
                                                                data.id?.let { dataId ->
                                                                    shareDeepLink(
                                                                        dataImage,
                                                                        dataName,
                                                                        data.stock.toString(),
                                                                        dataWeight,
                                                                        dataSize,
                                                                        dataHarga,
                                                                        dataId.toString(),
                                                                        "https://bimabk.com/deeplink?id=${data.id}"
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    binding.btnCart.setOnClickListener {
                                        results.data?.success?.data?.let { btnCartData ->
                                            doActionCart(
                                                btnCartData
                                            )
                                        }
                                    }
                                    results.data?.success?.data?.let { dataToFav ->
                                        initFavorite(
                                            userId,
                                            dataToFav, productId
                                        )
                                    }
                                    results.data?.success?.data?.let { dataToBottom ->
                                        setActionDialog(dataToBottom, dataPayment, namePayment)
                                    }
                                }
                                is Resource.Error -> {
                                    try {
                                        binding.progressBar.visibility = View.GONE
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
                                    } catch (t: Throwable) {
                                        Toast.makeText(
                                            this@DetailActivity,
                                            "No Internet Connection",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
        dataHargas: String,
        dataIdToAnly: String,
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
                analyticViewModel.onClickShareOnDetail(
                    name,
                    dataHargas.toDouble(),
                    dataIdToAnly.toInt()
                )
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Log.d("IMG Downloader", "Bitmap Failed...")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Log.d("IMG Downloader", "Bitmap Preparing Load...")
            }
        })
    }

    /**
     *
     * herrrrre
     */
    private fun initFavorite(userId: Int, data: DataDetail, productId: Int) {
        val nameToAnly = data.name_product
        binding.imgFavorite.setOnClickListener {
            if (!data.isFavorite) {
                nameToAnly?.let { dataName -> addFavorite(userId, productId, dataName) }
                binding.swipeRefresh.isRefreshing = true
            } else if (data.isFavorite) {
                nameToAnly?.let { dataName -> unFavorite(userId, productId, dataName) }
                binding.swipeRefresh.isRefreshing = true
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

    private fun addFavorite(userId: Int, productId: Int, nameToAnly: String) {
        viewModel.addFavorite(userId, productId).observe(this@DetailActivity) { results ->
            when (results) {
                is Resource.Loading -> {
                    binding.apply {
                        progressBar.visibility = View.VISIBLE
                        toolbar.visibility = View.GONE
                        stickyNested.visibility = View.GONE
                        layBtn.visibility = View.GONE
                        Log.d("addFavoriteLoading", "Loading")
                    }
                }
                is Resource.Success -> {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        toolbar.visibility = View.VISIBLE
                        stickyNested.visibility = View.VISIBLE
                        swipeRefresh.isRefreshing = false
                        layBtn.visibility = View.VISIBLE
                        Toast.makeText(
                            this@DetailActivity,
                            R.string.succes_favorite,
                            Toast.LENGTH_SHORT
                        ).show()
                        analyticViewModel.onClickLoveDetail(productId, nameToAnly, "true")
                        initDataDetail()
                    }
                }
                is Resource.Error -> {
                    try {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            toolbar.visibility = View.VISIBLE
                            stickyNested.visibility = View.GONE
                            swipeRefresh.isRefreshing = false
                            layBtn.visibility = View.VISIBLE
                        }
                        val err =
                            results.errorBody?.string()
                                ?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse =
                            gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        messageErr?.let { Log.d("Error Body", it) }
                    } catch (t: Throwable) {
                        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("addFavorite", "Empty Data")
                }
            }
        }
    }

    private fun unFavorite(userId: Int, productId: Int, dataName: String) {
        viewModel.unFavorite(userId, productId).observe(this@DetailActivity) { results ->
            when (results) {
                is Resource.Loading -> {
                    binding.apply {
                        progressBar.visibility = View.VISIBLE
                        toolbar.visibility = View.GONE
                        stickyNested.visibility = View.GONE
                        layBtn.visibility = View.GONE
                        Log.d("addFavoriteLoading", "Loading")
                    }
                }
                is Resource.Success -> {
                    binding.apply {
                        progressBar.visibility = View.GONE
                        toolbar.visibility = View.VISIBLE
                        stickyNested.visibility = View.VISIBLE
                        swipeRefresh.isRefreshing = false
                        layBtn.visibility = View.VISIBLE
                        Toast.makeText(
                            this@DetailActivity,
                            R.string.delete_favorite,
                            Toast.LENGTH_SHORT
                        ).show()
                        analyticViewModel.onClickLoveDetail(productId, dataName, "false")
                        initDataDetail()
                    }
                }
                is Resource.Error -> {
                    try {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            toolbar.visibility = View.VISIBLE
                            stickyNested.visibility = View.VISIBLE
                            swipeRefresh.isRefreshing = false
                            layBtn.visibility = View.VISIBLE
                        }
                        val err =
                            results.errorBody?.string()
                                ?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse =
                            gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        messageErr?.let { Log.d("Error Body", it) }
                    } catch (t: Throwable) {
                        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
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
        val cart = CartEntity(
            id = data.id,
            image = data.image,
            nameProduct = data.name_product,
            quantity = 1,
            price = data.harga,
            itemTotalPrice = data.harga?.toInt(),
            stock = data.stock,
            isChecked = false
        )

        if (data.stock != 0) {
            roomViewModel.insertCart(cart)
            analyticViewModel.onClickBtnTrolleyDetail()
            Toast.makeText(this, R.string.succes_trolley, Toast.LENGTH_SHORT).show()
            startActivity(router.toHomeActivity(this).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        } else {
            Toast.makeText(this, R.string.failed_trolley, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setActionDialog(data: DataDetail, dataPayment: String?, namePayment: String?) {
        if (dataPayment != null && namePayment !== null) {
            val showData = BuyDialogFragment(data, dataPayment, namePayment)
            showData.show(supportFragmentManager, DetailActivity::class.java.simpleName)
            analyticViewModel.onClickBtnBuyDetail()

            val idAnly = data.id
            idAnly?.let { analyticViewModel.onShowPopupBottom(it) }
        } else {
            binding.btnBuy.setOnClickListener {
                if (data.stock == 0) {
                    Toast.makeText(this, R.string.failed_trolley, Toast.LENGTH_SHORT).show()
                } else {
                    analyticViewModel.onClickBtnBuyDetail()
                    val showData = BuyDialogFragment(data, dataPayment, namePayment)
                    showData.show(supportFragmentManager, DetailActivity::class.java.simpleName)
                }
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
                            data.data?.let { result ->
                                adapter.setData(result.success.data.sortedBy { it1 -> it1.name_product })
                            }
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
                        } catch (t: Throwable) {
                            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT)
                                .show()
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
                            data.data?.let { result ->
                                adapter.setData(result.success.data.sortedBy { it1 -> it1.name_product })
                            }
                            initRecyclerViewOther()
                        } else if (data.data?.success?.data?.isEmpty() == true) {
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
                        } catch (t: Throwable) {
                            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT)
                                .show()
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
                    else -> {
                        Toast.makeText(this, "No Internet Detect", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val nameScreen = this.javaClass.simpleName
        analyticViewModel.onLoadScreenDetail(nameScreen)
    }

    private fun initRecyclerViewHistory() {
        binding.apply {
            rvHistoryProduct.adapter = adapter
            rvHistoryProduct.setHasFixedSize(true)

            adapter.onItemClick = {
                val intent = Intent(this@DetailActivity, DetailActivity::class.java)
                intent.putExtra(EXTRA_DATA_DETAIL, it.id)
                startActivity(intent)
                finish()
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
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }

    companion object {
        const val EXTRA_DATA_DETAIL = "extra_data_detail"
        fun createIntentById(context: Context, id: Int): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_DATA_DETAIL, id)
            }
        }

        const val EXTRA_DATA_PAYMENT_TO_BTN = "extra_data_payment_to_btn"
        const val EXTRA_NAME_PAYMENT_TO_BTN = "extra_name_payment_to_btn"

        fun createIntentFromPayment(
            context: Context,
            id: Int,
            idPayment: String,
            namePayment: String
        ): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_DATA_DETAIL, id)
                putExtra(EXTRA_DATA_PAYMENT_TO_BTN, idPayment)
                putExtra(EXTRA_NAME_PAYMENT_TO_BTN, namePayment)

            }
        }
    }

    override fun onClick(image: String) {
        seePhoto.showPhoto(image)
    }
}