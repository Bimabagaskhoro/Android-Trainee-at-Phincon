package com.bimabagaskhoro.taskappphincon.utils

import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.DataItemProduct

data class StickyModel(
    var toolbar: String,
    var itemSticky: DataItemProduct,
    var isSticky: Boolean
)
