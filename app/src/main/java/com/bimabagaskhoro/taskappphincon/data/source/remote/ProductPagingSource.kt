package com.bimabagaskhoro.taskappphincon.data.source.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bimabagaskhoro.taskappphincon.data.source.remote.network.ApiService
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.DataItemProduct
import com.bimabagaskhoro.taskappphincon.utils.Constant
import retrofit2.HttpException
import javax.inject.Inject

class ProductPagingSource @Inject constructor(
    private val search: String?,
    private val apiService: ApiService
) : PagingSource<Int, DataItemProduct>() {

    override fun getRefreshKey(state: PagingState<Int, DataItemProduct>): Int? {
//        return state.anchorPosition
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataItemProduct> {
        return try {
            val position = params.key ?: Constant.INITIAL_INDEX
            val responseData = apiService.getListProduct(search, position)

            LoadResult.Page(
                data = responseData.success.data,
                prevKey = null,
                nextKey = if (responseData.success.data.isEmpty()) null else position + 5
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

    }
}