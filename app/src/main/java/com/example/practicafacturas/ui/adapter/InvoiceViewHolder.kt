package com.example.practicafacturas.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.practicafacturas.R

import com.example.practicafacturas.databinding.BillItemBinding
import com.example.practicafacturas.model.Invoice
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class InvoiceViewHolder (view: View): ViewHolder(view) {
    val binding = BillItemBinding.bind(view)

    fun render(item: Invoice, onClickListener: (Invoice) -> Unit) {
        item.fecha?.let { binding.tvFecha.text = formatearFecha(it) }
        if (item.descEstado == "Pendiente de pago") {
            binding.tvEstado.setText(R.string.factura_estado)
        }else{
            binding.tvEstado.text = " "
        }
        binding.tvPrecio.text = "${item.importeOrdenacion} €"

        itemView.setOnClickListener {
            onClickListener(item)
        }
    }

    private fun formatearFecha(fecha: String): String {
        try {
            val entrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formateador = entrada.parse(fecha)
            val formatoSalida = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))

            // Verificar si formateador es nulo antes de intentar formatear
            return formateador?.let { formatoSalida.format(it) } ?: fecha

        } catch (e: ParseException) {
            e.printStackTrace()
            return fecha
        }
    }
}