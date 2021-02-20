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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.storeadmin.R
import com.example.storeadmin.databinding.FragmentEditBinding
import com.example.storeadmin.databinding.LayoutTopEditToolbarBinding
import com.example.storeadmin.models.Product
import com.example.storeadmin.viewmodels.EditViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class EditFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentEditBinding
    private lateinit var toolbarBinding: LayoutTopEditToolbarBinding
    private lateinit var navController: NavController
    private lateinit var product: Product
    private val PERMISSION_CODE = 0
    private val IMAGE_PICK_CODE = 1
    private val model: EditViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        toolbarBinding = LayoutTopEditToolbarBinding.bind(binding.root)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = requireArguments().getParcelable("product")!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setupUI()
    }

    private fun setupUI() {
        binding.priceETxt.setText(product.price.toString())
        binding.codeETxt.setText(product.code)
        binding.materialETxt.setText(product.material)
        binding.sizeETxt.setText(product.size)
        binding.colorsETxt.setText(product.colors)
        binding.detailsETxt.setText(product.details)
        binding.offerETxt.setText(product.offer.toString())
        binding.progressBar2.visibility = View.VISIBLE
        Picasso.get().load(product.image).into(binding.productImg, object : Callback {
            override fun onSuccess() {
                binding.progressBar2.visibility = View.INVISIBLE
            }

            override fun onError(e: Exception?) {
                binding.progressBar2.visibility = View.INVISIBLE
            }
        })
        toolbarBinding.backB.setOnClickListener(this)
        toolbarBinding.deleteB.setOnClickListener(this)
        binding.updateB.setOnClickListener(this)
        binding.selectImageB.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.updateB.id -> {
                updateProductOBJ()
                if (productIsValid(product)) {
                    if (model.imageName.isEmpty())
                        updateProduct(product)
                    else
                        updateProductWithImage(product, convertImageToByte())
                } else
                    produceError(product)
            }
            toolbarBinding.backB.id -> {
                navController.navigate(R.id.action_editFragment_to_mainFragment)
            }
            binding.selectImageB.id -> {
                pickImage()
            }
            toolbarBinding.deleteB.id -> {
                deleteProduct(product)
            }
        }
    }

    private fun deleteProduct(product: Product) {
        setupEditTexts(false)
        binding.progressBar1.visibility = View.VISIBLE
        FirebaseStorage.getInstance().getReferenceFromUrl(product.image!!).delete()
            .addOnSuccessListener {
                product.id?.let {
                    FirebaseFirestore.getInstance().collection("Products").document(it).delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
                            binding.progressBar1.visibility = View.INVISIBLE
                            navController.navigate(R.id.action_editFragment_to_mainFragment)
                        }
                }
            }
    }

    private fun updateProductOBJ() {
        product.details = binding.detailsETxt.text.toString()
        product.colors = binding.colorsETxt.text.toString()
        product.price = binding.priceETxt.text.toString().toFloat()
        binding.offerETxt.text.toString().isNotEmpty().let {
            product.offer = binding.offerETxt.text.toString().toFloat()
        }
        product.size = binding.sizeETxt.text.toString()
    }

    private fun produceError(product: Product) {
        if (product.colors.isEmpty()) {
            binding.colorsETxt.error = "Enter valid data"
        }
        if (product.material.isEmpty()) {
            binding.materialETxt.error = "Enter valid data"
        }
        if (product.size.isEmpty()) {
            binding.sizeETxt.error = "Enter valid data"
        }
    }

    private fun setupEditTexts(state: Boolean) {
        binding.colorsETxt.isEnabled = state
        binding.materialETxt.isEnabled = state
        binding.sizeETxt.isEnabled = state
        binding.priceETxt.isEnabled = state
        binding.selectImageB.isEnabled = state
        binding.detailsETxt.isEnabled = state
        binding.offerETxt.isEnabled = state
    }

    private fun productIsValid(product: Product): Boolean {
        return product.colors.isNotEmpty() &&
                product.material.isNotEmpty() &&
                product.size.isNotEmpty()
    }

    private fun pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) ==
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

    private fun convertImageToByte(): ByteArray {
        val bitmap = (binding.productImg.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        return baos.toByteArray()
    }

    private fun updateProduct(product: Product) {
        binding.progressBar1.visibility = View.VISIBLE
        setupEditTexts(false)
        product.id?.let {
            FirebaseFirestore.getInstance().collection("Products").document(it).set(product)
                .addOnSuccessListener {
                    binding.progressBar1.visibility = View.INVISIBLE
                    setupEditTexts(true)
                    Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateProductWithImage(product: Product, image: ByteArray) {
        binding.progressBar1.visibility = View.VISIBLE
        setupEditTexts(false)
        val storageRef =
            FirebaseStorage.getInstance().reference.child("Images/${model.imageName}")
        storageRef.putBytes(image).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                FirebaseStorage.getInstance().getReferenceFromUrl(product.image!!).delete()
                    .addOnSuccessListener {
                        model.imageName = ""
                        product.image = uri.toString()
                        product.id?.let { id ->
                            FirebaseFirestore.getInstance().collection("Products").document(id)
                                .set(product).addOnSuccessListener {
                                    binding.progressBar1.visibility = View.INVISIBLE
                                    setupEditTexts(true)
                                    Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        }
                    }
            }
        }
    }
}