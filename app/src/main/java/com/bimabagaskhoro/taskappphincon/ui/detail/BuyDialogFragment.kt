package com.bimabagaskhoro.taskappphincon.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestRating
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.DataDetail
import com.bimabagaskhoro.taskappphincon.databinding.FragmentBuyDialogBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.DetailActivity
import com.bimabagaskhoro.taskappphincon.ui.activity.OnSuccessActivity
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuyDialogFragment(private val data: DataDetail) : BottomSheetDialogFragment() {
    private var _binding: FragmentBuyDialogBinding? = null
    private val binding get() = _binding
    private val viewModel: BuyDialogViewModel by viewModels()
    override fun getTheme(): Int {
        return R.style.NoBackgroundDialogTheme
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBuyDialogBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            Glide.with(requireContext())
                .load(data.image)
                .into(this!!.imgDialog)

            tvPriceFragmentDialog.text = data.harga.formatterIdr()
            tvStockFragmentDialog.text = data.stock.toString()


        }

        viewModel.quantity.observe(requireActivity()) { results ->
            binding?.tvTotalNumber?.text = results.toString()
            if (results == data.stock) {
                binding?.addFragmentDialog?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_lightgrey)
                binding?.minFragmentDialog?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_black)
            } else if (results == 1) {
                binding?.addFragmentDialog?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_black)
                binding?.minFragmentDialog?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded_lightgrey)
            }
        }

        viewModel.setPrice(data.harga.toInt())

        viewModel.price.observe(requireActivity()) {data ->
            binding?.tvTotalPrice?.text = data.toString().formatterIdr()
        }

        setActionData()
        setTotalPrice()

        binding?.apply {
            cardBuy.setOnClickListener {
                val intent = Intent(context, OnSuccessActivity::class.java)
                intent.putExtra(OnSuccessActivity.EXTRA_DATA_SUCCESS, data.id)
                startActivity(intent)

                doActionUpdate()
            }
        }
    }

    private fun doActionUpdate() {

    }

    private fun setActionData() {
        binding?.addFragmentDialog?.setOnClickListener {
            viewModel.addQuantity(data.stock)
        }
        binding?.minFragmentDialog?.setOnClickListener {
            viewModel.minQuantity()
        }
    }

    private fun setTotalPrice() {
//        val quantity = binding?.tvTotalNumber
//        val price = binding?.tvPriceFragmentDialog
//        val n1: Int = quantity?.text.toString().toInt()
//        val n2: Int = price?.text.toString().toInt()
//        val results = (n2*n1)

//        binding?.tvTotalPrice?.text = results.toString()
    }

}