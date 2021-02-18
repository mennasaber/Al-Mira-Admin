package com.example.storeadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.storeadmin.R
import com.example.storeadmin.adapters.ProductsAdapter
import com.example.storeadmin.databinding.FragmentMainBinding
import com.example.storeadmin.databinding.LayoutTopMainToolbarBinding
import com.example.storeadmin.models.Product
import com.example.storeadmin.viewmodels.MainViewModel

class MainFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {
    private val model: MainViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding
    private lateinit var toolbarBinding: LayoutTopMainToolbarBinding
    private lateinit var navController: NavController
    private var products: ArrayList<Product> = arrayListOf()
    private var spanCount = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        toolbarBinding = LayoutTopMainToolbarBinding.bind(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setupUI()
    }

    private fun setupUI() {
        binding.progressBar.visibility = View.VISIBLE
        val screenWidth = resources.configuration.screenWidthDp
        spanCount = screenWidth / 100
        binding.addProductF.setOnClickListener(this)
        val categories = arrayListOf<String>()
        categories.add("All")
        categories.add("Offers")
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        model.getProductsCodes().observe(requireActivity(), {
            products = it
            binding.progressBar.visibility = View.INVISIBLE
            toolbarBinding.spinner.adapter = arrayAdapter
            toolbarBinding.spinner.setSelection(model.spinnerPosition)
            toolbarBinding.spinner.onItemSelectedListener = this
        })

    }

    override fun onClick(v: View) {
        val codes = arrayListOf<String>()
        products.forEach { codes.add(it.code) }
        when (v.id) {
            binding.addProductF.id -> {
                val bundle = bundleOf("codes" to codes)
                navController.navigate(R.id.action_mainFragment_to_addProductFragment, bundle)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        model.spinnerPosition = position
        when (position) {
            0 -> {
                binding.productsRecyclerView.adapter =
                    ProductsAdapter(products, navController)
            }
            1 -> {
                binding.productsRecyclerView.adapter =
                    ProductsAdapter(products.filter { it.offer != 0f }, navController)
            }
        }
        binding.productsRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), spanCount)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}