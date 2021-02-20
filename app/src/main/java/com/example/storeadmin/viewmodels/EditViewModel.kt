package com.example.storeadmin.viewmodels

import androidx.lifecycle.ViewModel
import com.example.storeadmin.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditViewModel : ViewModel() {
    var imageName: String = ""
    fun updateProduct(product: Product) {
        product.id?.let {
            FirebaseFirestore.getInstance().collection("Products").document(it).set(product)
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
                                .set(product)
                        }
                    }
            }
        }
    }

    fun deleteProduct(product: Product) {
        FirebaseStorage.getInstance().getReferenceFromUrl(product.image!!).delete()
            .addOnSuccessListener {
                product.id?.let {
                    FirebaseFirestore.getInstance().collection("Products").document(it).delete()
                }
            }
    }
}