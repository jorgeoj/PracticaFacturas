package com.example.practicafacturas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practicafacturas.Bill
import com.example.practicafacturas.R

class BillAdapter(private val facturasList: List<Bill>, private val onCLickListener: (Bill) -> Unit) : RecyclerView.Adapter<BillViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BillViewHolder(layoutInflater.inflate(R.layout.bill_item, parent, false))
    }

    override fun getItemCount(): Int = facturasList.size

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val item = facturasList[position]
        holder.render(item, onCLickListener)
    }
}