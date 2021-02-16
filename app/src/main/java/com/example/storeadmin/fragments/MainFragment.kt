package com.example.storeadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.storeadmin.R
import com.example.storeadmin.adapters.ProductsAdapter
import com.example.storeadmin.databinding.FragmentMainBinding
import com.example.storeadmin.models.Product
import com.example.storeadmin.viewmodels.MainViewModel

class MainFragment : Fragment(), View.OnClickListener {
    private val model: MainViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    private fun setupUI() {
        var list: ArrayList<Product>
        binding.progressBar.visibility = View.VISIBLE
        val screenWidth = resources.configuration.screenWidthDp
        val spanCount = screenWidth / 100
        model.getProductsCodes().observe(requireActivity(), {
            list = it
            binding.productsRecyclerView.adapter = ProductsAdapter(list)
            binding.productsRecyclerView.layoutManager =
                GridLayoutManager(requireContext(), spanCount)
            binding.progressBar.visibility = View.INVISIBLE
        })
        binding.addProductF.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.addProductF.id -> {
                navController.navigate(R.id.action_mainFragment_to_addProductFragment)
            }
        }
    }
}