package com.example.practicafacturas.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.practicafacturas.Bill
import com.example.practicafacturas.R
import com.example.practicafacturas.databinding.BillItemBinding

class BillViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = BillItemBinding.bind(view)
    private lateinit var factura: Bill

    fun render(item: Bill, onClickListener: (Bill) -> Unit) {
        binding.tvFecha.text = item.fecha
        if (item.descEstado == "Pendiente de pago") {
            binding.tvEstado.setText(R.string.factura_estado)
        }else{
            binding.tvEstado.text = " "
        }
        binding.tvPrecio.text = "${item.importeOrdenacion} €"

        itemView.setOnClickListener {
            onClickListener(item)
        }
        factura = item
    }
}