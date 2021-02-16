package com.example.storeadmin.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.storeadmin.R
import com.example.storeadmin.databinding.FragmentAddProductBinding
import com.example.storeadmin.databinding.LayoutTopBackToolbarBinding
import com.example.storeadmin.models.Product
import com.example.storeadmin.viewmodels.AddProductViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class AddProductFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentAddProductBinding
    private lateinit var toolbarBinding: LayoutTopBackToolbarBinding
    private lateinit var navController: NavController
    private lateinit var firestore: FirebaseFirestore
    private val PERMISSION_CODE = 0
    private val IMAGE_PICK_CODE = 1
    private val model: AddProductViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        toolbarBinding = LayoutTopBackToolbarBinding.bind(binding.root)
        firestore = FirebaseFirestore.getInstance()
        binding.addProductB.setOnClickListener(this)
        binding.selectImageB.setOnClickListener(this)
        toolbarBinding.backB.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.addProductB.id -> {
                binding.progressBar.visibility = View.VISIBLE
                saveProduct()
            }
            binding.selectImageB.id -> {
                pickImage()
            }
            toolbarBinding.backB.id -> {
                navController.navigate(R.id.action_addProductFragment_to_mainFragment)
            }
        }
    }

    private fun saveProduct() {
        if (binding.priceETxt.text.isNotEmpty() && binding.priceETxt.text.isDigitsOnly()) {
            val product = initializeProduct()
            if (productIsValid(product)) {
                disableEditTexts()
                val data: ByteArray = convertImageToByte()
                pushProduct(data, product)
            } else {
                produceError(product)
                binding.progressBar.visibility = View.INVISIBLE
            }
        } else {
            binding.priceETxt.error = "Enter valid data"
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun pushProduct(data: ByteArray, product: Product) {
        val storageRef =
            FirebaseStorage.getInstance().reference.child("Images/${model.imageName}")
        storageRef.putBytes(data).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                product.image = it.toString()
                firestore.collection("Products").add(product)
                    .addOnSuccessListener {
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                    .addOnFailureListener {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener {
                binding.progressBar.visibility = View.INVISIBLE
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun convertImageToByte(): ByteArray {
        val bitmap = (binding.productImg.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        return baos.toByteArray()
    }

    private fun initializeProduct(): Product {
        return Product(
            id = null,
            code = binding.codeETxt.text.toString(),
            material = binding.materialETxt.text.toString(),
            size = binding.sizeETxt.text.toString(),
            colors = binding.colorsETxt.text.toString(),
            details = binding.detailsETxt.text.toString(),
            price = binding.priceETxt.text.toString().toFloat(),
            image = null
        )
    }

    private fun produceError(product: Product) {
        if (product.code.isEmpty()) {
            binding.codeETxt.error = "Enter valid data"
        }
        if (product.colors.isEmpty()) {
            binding.colorsETxt.error = "Enter valid data"
        }
        if (product.material.isEmpty()) {
            binding.materialETxt.error = "Enter valid data"
        }
        if (product.size.isEmpty()) {
            binding.sizeETxt.error = "Enter valid data"
        }
        if (model.imageName.isEmpty()) {
            Toast.makeText(requireContext(), "Image not selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun disableEditTexts() {
        binding.codeETxt.isEnabled = false
        binding.colorsETxt.isEnabled = false
        binding.materialETxt.isEnabled = false
        binding.sizeETxt.isEnabled = false
        binding.priceETxt.isEnabled = false
        binding.selectImageB.isEnabled = false
        binding.detailsETxt.isEnabled = false
        binding.addProductB.isEnabled = false
    }

    private fun productIsValid(product: Product): Boolean {
        return product.code.isNotEmpty() &&
                product.colors.isNotEmpty() &&
                model.imageName.isNotEmpty() &&
                product.material.isNotEmpty() &&
                product.size.isNotEmpty()
    }

    private fun pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                pickImageFromGallery();
            }
        } else {
            //system OS is < Marshmallow
            pickImageFromGallery();
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            binding.productImg.setImageURI(data?.data)
            model.imageName = data?.data?.lastPathSegment!!
        }
    }
}