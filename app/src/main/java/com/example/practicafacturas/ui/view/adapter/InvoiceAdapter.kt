package com.example.practicafacturas.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practicafacturas.R
import com.example.practicafacturas.data.database.Invoice

class InvoiceAdapter(private val onCLickListener: (Invoice) -> Unit) : RecyclerView.Adapter<InvoiceViewHolder>() {

    // Lista de facturas que se mostrará en el RecyclerView
    private var listInvoices: List<Invoice>? = null

    // Método para establecer la lista de facturas en el adaptador
    fun setListInvoices(listInvoices: List<Invoice>?) {
        this.listInvoices = listInvoices
    }

    // Crea y devuelve una instancia de InvoiceViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return InvoiceViewHolder(layoutInflater.inflate(R.layout.bill_item, parent, false))
    }

    // Devuelve la cantidad de elementos en la lista de facturas (o 0 si la lista es nula)
    override fun getItemCount(): Int {
        if (listInvoices == null) return 0
        return listInvoices?.size!!
    }

    // Vincula los datos de una factura específica a la vista correspondiente en el RecyclerView
    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        // Llama al método render de InvoiceViewHolder para mostrar la información de la factura
        holder.render(listInvoices?.get(position)!!, onCLickListener)
    }
}
