package com.app.shoply.domain

data class ProductPage(
    val products: List<ProductItem>,
    val currentPage: Int,
    val pageCount: Int
)