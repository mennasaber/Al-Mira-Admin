package com.example.storeadmin.models

import android.os.Parcel
import android.os.Parcelable


data class Product(
    val id: String?,
    val code: String,
    var colors: String,
    var size: String,
    val material: String,
    var details: String,
    var price: Float,
    var image: String?,
    var offer: Float = 0f
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readString(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(code)
        parcel.writeString(colors)
        parcel.writeString(size)
        parcel.writeString(material)
        parcel.writeString(details)
        parcel.writeFloat(price)
        parcel.writeString(image)
        parcel.writeFloat(offer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}