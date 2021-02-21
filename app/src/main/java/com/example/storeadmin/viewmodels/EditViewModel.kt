package com.example.storeadmin.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storeadmin.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditViewModel : ViewModel() {
    val deletedMessage= MutableLiveData<String>()
    var imageName: String = ""
    var message = MutableLiveData<String>()
    fun updateProduct(product: Product) {
        product.id?.let { id ->
            FirebaseFirestore.getInstance().collection("Products").document(id).set(product)
                .addOnSuccessListener { message.value = "" }
                .addOnFailureListener { message.value = it.message }
        }
    }

    fun updateProductWithImage(product: Product, image: ByteArray) {
        val storageRef =
            FirebaseStorage.getInstance().reference.child("Images/${imageName}")
        storageRef.putBytes(image).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                FirebaseStorage.getInstance().getReferenceFromUrl(product.image!!).delete()
                    .addOnSuccessListener {
                        imageName = ""
                        product.image = uri.toString()
                        product.id?.let { id ->
                            FirebaseFirestore.getInstance().collection("Products").document(id)
                                .set(product).addOnSuccessListener { message.value = "" }
                                .addOnFailureListener { message.value = it.message }
                        }
                    }.addOnFailureListener { message.value = it.message }
            }.addOnFailureListener { message.value = it.message }
        }.addOnFailureListener { message.value = it.message }
    }

    fun deleteProduct(product: Product) {
        FirebaseStorage.getInstance().getReferenceFromUrl(product.image!!).delete()
            .addOnSuccessListener {
                product.id?.let { id ->
                    FirebaseFirestore.getInstance().collection("Products").document(id).delete()
                        .addOnSuccessListener { deletedMessage.value = "" }
                        .addOnFailureListener { deletedMessage.value = it.message }
                }
            }.addOnFailureListener { deletedMessage.value = it.message }
    }
}