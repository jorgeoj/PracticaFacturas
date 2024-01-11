package com.example.practicafacturas.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.practicafacturas.Bill
import com.example.practicafacturas.R
import com.example.practicafacturas.databinding.BillItemBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class BillViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = BillItemBinding.bind(view)
    private lateinit var factura: Bill

    fun render(item: Bill, onClickListener: (Bill) -> Unit) {
        binding.tvFecha.text = formatearFecha(item.fecha)
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

    private fun formatearFecha(fecha: String): String {
        try {
            val entrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formateador = entrada.parse(fecha)
            val formatoSalida = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
            return formatoSalida.format(formateador!!)
        }catch (e: ParseException) {
            e.printStackTrace()
            return fecha
        }
    }
}