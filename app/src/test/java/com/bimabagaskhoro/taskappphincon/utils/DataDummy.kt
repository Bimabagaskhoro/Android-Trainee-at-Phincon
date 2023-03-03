package com.bimabagaskhoro.taskappphincon.utils

import com.bimabagaskhoro.phincon.core.data.source.remote.response.auth.*
import com.bimabagaskhoro.phincon.core.data.source.remote.response.detail.DataDetail
import com.bimabagaskhoro.phincon.core.data.source.remote.response.detail.ImageProductItem
import com.bimabagaskhoro.phincon.core.data.source.remote.response.detail.ResponseDetail
import com.bimabagaskhoro.phincon.core.data.source.remote.response.detail.SuccessDetail
import com.bimabagaskhoro.phincon.core.data.source.remote.response.favorite.*
import com.bimabagaskhoro.phincon.core.data.source.remote.response.product.DataItemProduct
import com.bimabagaskhoro.phincon.core.data.source.remote.response.product.ResponseProduct
import com.bimabagaskhoro.phincon.core.data.source.remote.response.product.Success

object DataDummy {
    fun generateDummyPaging(): List<DataItemProduct> {
        val items: MutableList<DataItemProduct> = arrayListOf()
        for (i in 0..100) {
            val feed = DataItemProduct(
                id = i,
                date = "date",
                image = "image",
                name_product = "name_product",
                harga = "harga",
                size = "size",
                rate = 0,
                weight = "weight",
                stock = 0,
                type = "type",
                desc = "desc"
            )
            items.add(feed)
        }
        return items
    }

    fun generateDummyLogin(): ResponseLogin {
        val getDataUser = DataUser(
            image = "image",
            path = "path",
            gender = 1,
            phone = "phone",
            name = "name",
            id = 1,
            email = "email"
        )

        return ResponseLogin(
            SuccessLogin(
                access_token = "access_token",
                refresh_token = "refresh_token",
                data_user = getDataUser,
                message = "message",
                status = null,
            )
        )
    }

    fun generateDummyRegister(): ResponseRegister {
        val responseRegister = SuccessRegister(
            message = "message",
            status = null,
        )

        return ResponseRegister(
            success = responseRegister
        )
    }

    fun generateDummyChangePassword(): ResponseChangePassword {
        val responsePassword = SuccessPassword(
            message = "message",
            status = null,
        )

        return ResponseChangePassword(
            success = responsePassword
        )
    }

    fun generateDummyChangeImage(): ResponseChangeImage {
        val responseChangeImage = SuccessImage(
            message = "message",
            status = null,
        )

        return ResponseChangeImage(
            success = responseChangeImage
        )
    }

    fun generateDummyAddFavorite(): ResponseAddFavorite {
        val responseAddFavorite = SuccessAddFav(
            message = "message",
            status = null
        )

        return ResponseAddFavorite(
            success = responseAddFavorite
        )
    }

    fun generateDummyGetDetailProduct(): ResponseDetail {
        val imageProductItem = ImageProductItem(
            image_product = "image_product",
            title_product = "title_product",
        )

        val getDetail = DataDetail(
            date = "date",
            image = "image",
            name_product = "name_product",
            harga = "harga",
            size = "size",
            weight = "weight",
            image_product = listOf(imageProductItem),
            id = 0,
            stock = 0,
            type = "type",
            desc = "desc",
            isFavorite = true
        )

        return ResponseDetail(
            SuccessDetail(
                data = getDetail,
                message = "message",
                status = null,
            )
        )
    }

    fun generateDummyUpdateStock(): ResponseAddFavorite {
        val responseAddFavorite = SuccessAddFav(
            message = "message",
            status = null
        )

        return ResponseAddFavorite(
            success = responseAddFavorite
        )
    }

    fun generateDummyUnFavorite(): ResponseAddFavorite {
        val responseAddFavorite = SuccessAddFav(
            message = "message",
            status = null
        )

        return ResponseAddFavorite(
            success = responseAddFavorite
        )
    }

    fun generateDummyUpdateRate(): ResponseAddFavorite {
        val responseAddFavorite = SuccessAddFav(
            message = "message",
            status = null
        )

        return ResponseAddFavorite(
            success = responseAddFavorite
        )
    }

    fun generateDummyDataFavorite(): ResponseFavorite {
        val dataItemFavorite = DataItemFavorite(
            id = 0,
            date = "date",
            image = "image",
            name_product = "name_product",
            harga = "harga",
            size = "size",
            rate = 0,
            weight = "weight",
            stock = 0,
            type = "type",
            desc = "desc"
        )

        return ResponseFavorite(
            success = Success(
                data = listOf(dataItemFavorite),
                message = "message",
                status = null
            )
        )
    }

    fun generateDummyOtherProduct(): ResponseProduct {
        val dataItemProduct = DataItemProduct(
            id = 0,
            date = "date",
            image = "image",
            name_product = "name_product",
            harga = "harga",
            size = "size",
            rate = 0,
            weight = "weight",
            stock = 0,
            type = "type",
            desc = "desc"
        )
        return ResponseProduct(
            success = Success(
                data = listOf(dataItemProduct),
                message = "message",
                status = null
            )
        )
    }

    fun generateDummyHistoryProduct(): ResponseProduct {
        val dataItemProduct = DataItemProduct(
            id = 0,
            date = "date",
            image = "image",
            name_product = "name_product",
            harga = "harga",
            size = "size",
            rate = 0,
            weight = "weight",
            stock = 0,
            type = "type",
            desc = "desc"
        )
        return ResponseProduct(
            success = Success(
                data = listOf(dataItemProduct),
                message = "message",
                status = null
            )
        )
    }
}