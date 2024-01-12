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
    private lateinit var invoice: Invoice

    fun render(item: Invoice) {
        // TODO: Ver si funciona la linea de abajo, sino esta es la linea de antes
        // binding.tvFecha.text = formatearFecha(item.fecha)
        item.fecha?.let { // Verificar si fecha no es nulo
            binding.tvFecha.text = formatearFecha(it)
        }
        if (item.descEstado == "Pendiente de pago") {
            binding.tvEstado.setText(R.string.factura_estado)
        }else{
            binding.tvEstado.text = " "
        }
        binding.tvPrecio.text = "${item.importeOrdenacion} €"
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