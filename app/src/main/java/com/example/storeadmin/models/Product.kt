package com.example.storeadmin.models

import android.net.Uri


data class Product(
    val id: String?,
    val code: String,
    val colors: String,
    val size: String,
    val material: String,
    val details: String,
    val price: Float,
    var image: String?
)