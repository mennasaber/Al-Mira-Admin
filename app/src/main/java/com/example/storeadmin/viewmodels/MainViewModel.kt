package com.example.storeadmin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storeadmin.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MainViewModel : ViewModel() {
    var spinnerPosition: Int=0
    private val products: MutableLiveData<ArrayList<Product>> = MutableLiveData()
    fun getProductsCodes(): LiveData<ArrayList<Product>> {
        FirebaseFirestore.getInstance().collection("Products").addSnapshotListener { it, _ ->
            products.postValue(it?.let { it1 -> toProducts(it1) })
        }
        return products
    }

    private fun toProducts(it: QuerySnapshot): ArrayList<Product> {
        val list = arrayListOf<Product>()
        for (document in it) {
            val product = Product(
                id = document.id,
                code = document["code"] as String,
                material = document["material"] as String,
                colors = document["colors"] as String,
                image = document["image"] as String?,
                price = (document["price"] as Double).toFloat(),
                details = document["details"] as String,
                size = document["size"] as String,
                offer = (document["offer"] as Double).toFloat()
            )
            list.add(product)
        }
        return list
    }
}