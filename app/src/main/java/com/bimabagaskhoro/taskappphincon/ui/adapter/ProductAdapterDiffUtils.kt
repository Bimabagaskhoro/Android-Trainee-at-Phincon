package com.bimabagaskhoro.taskappphincon.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.DataItemProduct
import com.bimabagaskhoro.taskappphincon.databinding.ItemProductBinding
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bumptech.glide.Glide

@Suppress("DEPRECATION")
@SuppressLint("NotifyDataSetChanged")
class ProductAdapterDiffUtils : RecyclerView.Adapter<ProductAdapterDiffUtils.ViewHolder>(), Filterable {

    private var listData: ArrayList<DataItemProduct> = ArrayList()
    private var listDataFiltered: ArrayList<DataItemProduct> = ArrayList()

    var onItemClick: ((DataItemProduct) -> Unit)? = null

    fun setData(list: List<DataItemProduct>) {
        val diffResult = DiffUtil.calculateDiff(ProductDiffCallback(listData, list))
        listData.clear()
        listData.addAll(list)
        listDataFiltered.clear()
        listDataFiltered.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listDataFiltered[position])
    }

    override fun getItemCount() = listDataFiltered.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemProductBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bind(data: DataItemProduct) {
            binding.apply {
                Glide.with(itemView.context)
                    .asBitmap()
                    .load(data.image)
                    .into(imgProduct)
                tvTittleProduct.text = data.name_product
                tvPriceProduct.text = data.harga.formatterIdr()
                tvDateProduct.text = data.date
                imgBtnFavorite.visibility = View.INVISIBLE

            }
        }
        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(listData[adapterPosition])
            }
        }
    }


    inner class ProductDiffCallback(
        private val  oldList: List<DataItemProduct>,
        private val  newList: List<DataItemProduct>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].date == newList[newItemPosition].date

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                listDataFiltered = if (charString.isEmpty()) {
                    listData
                } else {
                    val filteredList = ArrayList<DataItemProduct>()
                    listData.filter {
                        (it.name_product.lowercase().contains(charString.lowercase()))
                    }.forEach { filteredList.add(it) }
                    filteredList
                }
                return FilterResults().apply {
                    values = listDataFiltered
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listDataFiltered = if (results?.values == null) {
                    ArrayList()
                } else {
                    results.values as ArrayList<DataItemProduct>
                }
                notifyDataSetChanged()
            }

        }
    }
}