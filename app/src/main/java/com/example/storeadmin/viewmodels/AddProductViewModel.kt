package com.example.storeadmin.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storeadmin.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddProductViewModel : ViewModel() {
    var imageName = ""
    var message = MutableLiveData<String>()
     fun addProduct(data: ByteArray, product: Product) {
        val storageRef =
            FirebaseStorage.getInstance().reference.child("Images/${imageName}")
        storageRef.putBytes(data).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                product.image = uri.toString()
                FirebaseFirestore.getInstance().collection("Products").add(product)
                    .addOnSuccessListener {
                        message.value = ""
                    }
                    .addOnFailureListener {
                        message.value = it.message
                    }
            }.addOnFailureListener {
                message.value = it.message
            }
        }
    }
}