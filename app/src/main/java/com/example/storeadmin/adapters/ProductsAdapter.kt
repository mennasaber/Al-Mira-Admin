package com.example.storeadmin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.storeadmin.R
import com.example.storeadmin.models.Product


class ProductsAdapter(var productsList: List<Product>, var navController: NavController) :
    RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {
    class ProductsViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val codeTxt = view.findViewById<TextView>(R.id.codeTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.codeTxt.text = productsList[position].code
        holder.itemView.setOnClickListener {
            navController.navigate(
                R.id.action_mainFragment_to_editFragment,
                bundleOf("product" to productsList[position])
            )
        }
    }

    override fun getItemCount(): Int {
        return productsList.count()
    }
}