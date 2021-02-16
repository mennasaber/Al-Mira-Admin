package com.example.storeadmin.viewmodels

import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.storeadmin.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AddProductViewModel : ViewModel() {
    var imageName = ""
}