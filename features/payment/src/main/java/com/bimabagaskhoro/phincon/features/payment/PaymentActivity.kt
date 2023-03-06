package com.bimabagaskhoro.phincon.features.payment

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import com.bimabagaskhoro.phincon.core.adapter.payment.PaymentHeaderAdapter
import com.bimabagaskhoro.phincon.core.data.source.remote.response.PaymentModel
import com.bimabagaskhoro.phincon.core.vm.FGAViewModel
import com.bimabagaskhoro.phincon.core.vm.FRCViewModel
import com.bimabagaskhoro.phincon.features.payment.databinding.ActivityPaymentBinding
import com.bimabagaskhoro.phincon.router.ActivityRouter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {

    @Inject
    lateinit var router: ActivityRouter

    private lateinit var binding: ActivityPaymentBinding
    private lateinit var adapter: PaymentHeaderAdapter
    private var idProduct: Int? = 0

    private val viewModel by viewModels<FRCViewModel>()
    private val analyticViewModel: FGAViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getIntExtra(EXTRA_DATA_PAYMENT, 0)
        idProduct = productId

        initAdapter(productId)
        toDoActivity(productId)
        observeData()
    }

    private fun observeData() {
        viewModel.state.observe(this@PaymentActivity) { response ->
            when (response) {
                is com.bimabagaskhoro.phincon.core.utils.Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvHeaderPayment.visibility = View.INVISIBLE
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvHeaderPayment.visibility = View.VISIBLE
                    try {
                        val dataList = Gson().fromJson<ArrayList<PaymentModel>>(
                            response.data,
                            object : TypeToken<ArrayList<PaymentModel>>() {}.type
                        )
                        initRecyclerview(dataList)
                    } catch (e: Exception) {
                        Toast.makeText(this, "dataRemoteConfig null", Toast.LENGTH_SHORT).show()
                    }
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.rvHeaderPayment.visibility = View.INVISIBLE
                    Toast.makeText(this@PaymentActivity, "Error Data", Toast.LENGTH_SHORT).show()
                }
                is com.bimabagaskhoro.phincon.core.utils.Resource.Empty -> {
                    Log.d("observeData", "observeData: Empty Data")
                }
            }
        }
    }

    private fun toDoActivity(productId: Int?) {
        if (idProduct != 0) {
            val data: Uri? = intent?.data
            val id = data?.getQueryParameter("id")
            idProduct = id?.toInt()
            binding.btnBack.setOnClickListener { doAction(true, productId) }
        } else {
            binding.btnBack.setOnClickListener { doAction(false, null) }
        }
    }

    private fun initAdapter(productId: Int) {
        adapter = if (productId == 0) {
            PaymentHeaderAdapter { dataItem, paymentModel ->
                startActivity(dataItem.id?.let { dataItemsId ->
                    dataItem.name?.let { dataItemsName ->
                        router.toTrolleyFromPaymentActivity(
                            this, dataItemsId, dataItemsName
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    }
                })

                Log.d("initAdapter", "harusnya ke trolley")
                val paymentMethod = paymentModel.type
                dataItem.name?.let { dataAnalytic ->
                    paymentMethod?.let { paymentAnalytic ->
                        analyticViewModel.onClickBankPayment(
                            paymentAnalytic, dataAnalytic
                        )
                    }
                }
            }
        } else {
            PaymentHeaderAdapter { dataItem, paymentModel ->
                startActivity(productId?.let { productId ->
                    dataItem.id?.let { idPayment ->
                        dataItem.name?.let { namePayment ->
                            router.toDetailFromPaymentActivity(
                                this, productId, idPayment, namePayment
                            )
                        }
                    }})
                Log.d("initAdapter", "harusnya ke detail")
                val paymentMethod = paymentModel.type
                dataItem.name?.let { dataAnalytic ->
                    paymentMethod?.let { paymentAnalytic ->
                        analyticViewModel.onClickBankPayment(
                            paymentAnalytic, dataAnalytic
                        )
                    }
                }
            }
        }
    }

    private fun initRecyclerview(dataList: List<PaymentModel>?) {
        binding.apply {
            rvHeaderPayment.setHasFixedSize(true)
            rvHeaderPayment.adapter = adapter
            dataList?.sortedBy { it.order }?.let { adapter.setData(it) }
        }
    }

    private fun doAction(fromDetail: Boolean, productId: Int?) {
        if (fromDetail) {
            binding.btnBack.setOnClickListener {
                startActivity(productId?.let { productIds ->
                    router.toDetailActivity(this, productIds).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                })

                analyticViewModel.onClickBackPayment()
            }
        } else if (!fromDetail) {
            binding.btnBack.setOnClickListener {
                analyticViewModel.onClickBackPayment()
                startActivity(router.toTrolleyActivity(this).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        }
    }

    companion object {
        const val EXTRA_DATA_PAYMENT = "extra_data_payment"
        fun createIntentFromDetail(context: Context, id: Int): Intent {
            return Intent(context, PaymentActivity::class.java).apply {
                putExtra(EXTRA_DATA_PAYMENT, id)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val nameSpace = this.javaClass.simpleName
        analyticViewModel.onLoadScreenPayment(nameSpace)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}