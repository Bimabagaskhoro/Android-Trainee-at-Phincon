package com.bimabagaskhoro.phincon.core.adapter.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.phincon.core.databinding.ItemLoadPagingBinding

class LoadPagingAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadPagingAdapter.LoadingViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingViewHolder {
        val binding =
            ItemLoadPagingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadingViewHolder(private val binding: ItemLoadPagingBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }
    }

}