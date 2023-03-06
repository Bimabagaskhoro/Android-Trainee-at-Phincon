package com.bimabagaskhoro.phincon.features.trolley

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bimabagaskhoro.phincon.core.adapter.CartAdapter
import com.bimabagaskhoro.phincon.core.data.source.local.model.CartEntity
import com.bimabagaskhoro.phincon.core.data.source.remote.response.DataStockItem
import com.bimabagaskhoro.phincon.core.data.source.remote.response.ResponseError
import com.bimabagaskhoro.phincon.core.utils.Resource
import com.bimabagaskhoro.phincon.core.utils.formatterIdr
import com.bimabagaskhoro.phincon.core.vm.DataStoreViewModel
import com.bimabagaskhoro.phincon.core.vm.FGAViewModel
import com.bimabagaskhoro.phincon.core.vm.LocalViewModel
import com.bimabagaskhoro.phincon.core.vm.RemoteViewModel
import com.bimabagaskhoro.phincon.features.trolley.databinding.ActivityCartBinding
import com.bimabagaskhoro.phincon.router.ActivityRouter
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

@Suppress("UnusedEquals", "UNUSED_EXPRESSION")
@AndroidEntryPoint
class CartActivity : AppCompatActivity() {

    @Inject
    lateinit var router: ActivityRouter

    private lateinit var binding: ActivityCartBinding
    private var trolleyAdapter: CartAdapter? = null
    private var totalPrice = 0

    private val roomViewModel: LocalViewModel by viewModels()
    private val viewModel: RemoteViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()
    private val analyticViewModel: FGAViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataPayment = intent.getStringExtra(EXTRA_DATA_CART)
        val dataName = intent.getStringExtra(EXTRA_DATA_CART_NAME)
        setupToolbar()
        initObserver()
        initRecyclerView()
        setupListener()
        postProductTrolley(dataPayment, dataName)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarTrolley)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                roomViewModel.getAllProduct().collect { result ->
                    if (result.isNotEmpty()) {
                        var priceTotal = 0
                        val filterResult = result.filter { it.isChecked }

                        filterResult.forEach {
                            priceTotal = priceTotal.plus(it.itemTotalPrice!!)
                        }
                        binding.apply {
                            tvAllPrice.text = priceTotal.toString().formatterIdr()
                            checkBox2.isChecked = result.size == filterResult.size
                            trolleyAdapter?.setData(result)
                            totalPrice = priceTotal
                            checkBox2.visibility = View.VISIBLE
                            rvCart.visibility = View.VISIBLE
                            cardView.visibility = View.VISIBLE

                            emptyData.visibility = View.GONE
                            tvEmpty.visibility = View.GONE

                        }
                    } else {
                        binding.apply {
                            cardView.visibility = View.GONE
                            viewLayout.visibility = View.GONE
                            emptyData.visibility = View.VISIBLE
                            tvEmpty.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.apply {
            trolleyAdapter = CartAdapter(
                onDeleteItem = {
                    setDialogDeleteItem(it)
                    it.id?.let { dataId ->
                        it.nameProduct?.let { dataName ->
                            analyticViewModel.onClickDeleteTrolley(dataId, dataName)
                        }
                    }
                },
                onAddQuantity = {
                    val productId = it.id
                    val quantity = it.quantity
                    val price = it.price
                    roomViewModel.updateProductData(
                        quantity = quantity?.plus(1),
                        itemTotalPrice = price?.toInt()?.times(quantity.toString().toInt().plus(1)),
                        id = productId
                    )

                    it.id?.let { dataId ->
                        it.nameProduct?.let { dataName ->
                            quantity?.let { dataQty ->
                                analyticViewModel.onClickAddQtyTrolley(
                                    "-",
                                    dataQty, dataId, dataName
                                )
                            }
                        }
                    }
                },
                onMinQuantity = {
                    val productId = it.id
                    val quantity = it.quantity
                    val price = it.price
                    roomViewModel.updateProductData(
                        quantity = quantity?.minus(1),
                        itemTotalPrice = price?.toInt()
                            ?.times(quantity.toString().toInt().minus(1)),
                        id = productId
                    )
                    it.id?.let { dataId ->
                        it.nameProduct?.let { dataName ->
                            quantity?.let { dataQty ->
                                analyticViewModel.onClickAddQtyTrolley(
                                    "-",
                                    dataQty, dataId, dataName
                                )
                            }
                        }
                    }
                },
                onCheckedItem = {
                    val productId = it.id
                    val isChecked = !it.isChecked
                    binding.btnBuy.isClickable = true
                    roomViewModel.updateProductIsCheckedById(isChecked, productId)

                    it.id?.let { dataId ->
                        it.nameProduct?.let { dataName ->
                            analyticViewModel.onClickSelectTrolley(dataId, dataName)
                        }
                    }
                },
            )
            rvCart.adapter = trolleyAdapter
            rvCart.setHasFixedSize(true)
        }
    }

    private fun setupListener() {
        binding.checkBox2.setOnClickListener {
            if (binding.checkBox2.isChecked) {
                roomViewModel.updateProductIsCheckedAll(true)
            } else {
                roomViewModel.updateProductIsCheckedAll(false)
            }
        }
    }

    private fun postProductTrolley(dataPayment: String?, dataName: String?) {
        if (dataPayment == null && dataName == null) {
            binding.layBtnPayment.visibility = View.GONE
            binding.btnBuy.setOnClickListener {
                startActivity(router.toPaymentFromTrolleyActivity(this).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
                analyticViewModel.onClickBuyOnTrolley()
            }
        } else {
            binding.layBtnPayment.setOnClickListener {
                startActivity(router.toPaymentFromTrolleyActivity(this).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
                dataName?.let { dataNameAnly ->
                    analyticViewModel.onClickIconBankTrolley(
                        dataNameAnly
                    )
                }
            }
            binding.tvPaymentMethode.text = dataName
            dataPayment?.let { initImagePayment(it) }
            binding.btnBuy.setOnClickListener {
                setActionPost(dataPayment, dataName)
            }
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    roomViewModel.getAllCheckedProduct().collect { result ->
                        val dataStockItems = arrayListOf<DataStockItem>()
                        val listOfProductId = arrayListOf<String>()
                        for (i in result.indices) {
                            dataStockItems.add(
                                DataStockItem(
                                    result[i].id.toString(),
                                    result[i].quantity!!
                                )
                            )
                            listOfProductId.add(result[i].id.toString())
                        }
                    }
                }
            }
        }
    }

    private fun setActionPost(dataPayment: String?, dataName: String?) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                roomViewModel.getAllCheckedProduct().collect { result ->
                    val dataStockItems = arrayListOf<DataStockItem>()
                    val listOfProductId = arrayListOf<String>()
                    dataStoreViewModel.getUserId.observe(this@CartActivity) {
                        val idUser = it
                        for (i in result.indices) {
                            dataStockItems.add(
                                DataStockItem(
                                    result[i].id.toString(),
                                    result[i].quantity
                                )
                            )
                            listOfProductId.add(result[i].id.toString())
                        }
                        buyProduct(
                            idUser.toString(),
                            dataStockItems,
                            listOfProductId,
                            dataPayment,
                            dataName,
                        )
                        val filterResult = result.filter { it.isChecked }
                        var priceTotalForAnly = 0
                        filterResult.forEach {
                            priceTotalForAnly = priceTotalForAnly.plus(it.itemTotalPrice!!)
                        }

                        dataName?.let { paymentMethodeAnly ->
                            analyticViewModel.onClickBuyScsTrolley(
                                priceTotalForAnly.toDouble(),
                                paymentMethodeAnly
                            )
                        }
                    }
                }
            }
        }
    }

    private fun buyProduct(
        idUser: String,
        requestBody: ArrayList<DataStockItem>,
        listOfProductId: ArrayList<String>,
        dataPayment: String?,
        dataName: String?
    ) {
        viewModel.updateStock(requestBody, idUser).observe(this@CartActivity) { todo ->
            when (todo) {
                is Resource.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                    binding.cardProgressbar.visibility = View.VISIBLE
                    binding.tvWaiting.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressbar.visibility = View.GONE
                    binding.cardProgressbar.visibility = View.GONE
                    binding.tvWaiting.visibility = View.GONE
                    requestBody.forEach { roomViewModel.deleteProductByIdFromTrolley(it.id_product?.toInt()) }

                    /**
                     * do something
                     */
                    val resultPrice = binding.tvAllPrice.text.toString().trim()
                    startActivity(dataName?.let { dataName ->
                        dataPayment?.let { dataPayment ->
                            router.toSuccessPageFromTrolleyActivity(
                                this, listOfProductId, dataName, dataPayment, resultPrice
                            )}})
                }
                is Resource.Error -> {
                    try {
                        binding.progressbar.visibility = View.GONE
                        binding.cardProgressbar.visibility = View.GONE
                        binding.tvWaiting.visibility = View.GONE
                        val err =
                            todo.errorBody?.string()
                                ?.let { it1 -> JSONObject(it1).toString() }
                        val gson = Gson()
                        val jsonObject = gson.fromJson(err, JsonObject::class.java)
                        val errorResponse =
                            gson.fromJson(jsonObject, ResponseError::class.java)
                        val messageErr = errorResponse.error.message
                        messageErr?.let { Log.d("Error Body", it) }
                    } catch (t: Throwable) {
//                        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    } catch (io: IOException) {
                        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Empty -> {
                    Log.d("Empty Data", "Empty")
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        analyticViewModel.onClickBackTrolley()
        return true
    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        analyticViewModel.onClickBackTrolley()
    }

    private fun initImagePayment(dataPayment: String) {
        binding.apply {
            when (dataPayment) {
                "va_bca" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.bca)
                        .into(imgPaymentMethode)
                }
                "va_mandiri" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.mandiri)
                        .into(imgPaymentMethode)
                }
                "va_bri" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.bri)
                        .into(imgPaymentMethode)
                }
                "va_bni" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.bni)
                        .into(imgPaymentMethode)
                }
                "va_btn" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.btn)
                        .into(imgPaymentMethode)
                }
                "va_danamon" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.danamon)
                        .into(imgPaymentMethode)
                }
                "ewallet_gopay" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.gopay)
                        .into(imgPaymentMethode)
                }
                "ewallet_ovo" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.ovo)
                        .into(imgPaymentMethode)
                }
                "ewallet_dana" -> {
                    Glide.with(this@CartActivity)
                        .asBitmap()
                        .load(R.drawable.dana)
                        .into(imgPaymentMethode)
                }
            }
        }
    }

    private fun setDialogDeleteItem(data: CartEntity) {
        AlertDialog.Builder(this@CartActivity)
            .setTitle("Remove item from trolley")
            .setMessage("Are you sure you want to remove this item?")
            .setPositiveButton("Ok") { _, _ ->
                roomViewModel.deleteProductByIdFromTrolley(data.id)
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    override fun onResume() {
        super.onResume()
        val nameScreen = this.javaClass.simpleName
        analyticViewModel.onLoadScreenTrolley(nameScreen)
    }

    companion object {
        const val EXTRA_DATA_CART = "extra_data_cart"
        const val EXTRA_DATA_CART_NAME = "extra_data_cart_name"

        fun createIntentFromPayment(
            context: Context,
            dataId: String,
            dataName: String
        ): Intent {
            return Intent(context, CartActivity::class.java).apply {
                putExtra(EXTRA_DATA_CART, dataId)
                putExtra(EXTRA_DATA_CART_NAME, dataName)
            }
        }
    }
}