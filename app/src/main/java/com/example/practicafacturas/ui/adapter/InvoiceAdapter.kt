package com.example.practicafacturas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practicafacturas.R

import com.example.practicafacturas.model.Invoice

class InvoiceAdapter(private val onCLickListener: (Invoice) -> Unit): RecyclerView.Adapter<InvoiceViewHolder>() {

    private var listInvoices: List<Invoice>? = null
    fun setListInvoices(listInvoices: List<Invoice>?) {
        this.listInvoices = listInvoices
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return InvoiceViewHolder(layoutInflater.inflate(R.layout.bill_item, parent, false))
    }

    override fun getItemCount(): Int {
        if (listInvoices == null) return 0
        return listInvoices?.size!!
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        holder.render(listInvoices?.get(position)!!, onCLickListener)
    }
}