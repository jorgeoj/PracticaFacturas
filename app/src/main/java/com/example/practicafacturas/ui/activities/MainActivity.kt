package com.example.practicafacturas.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practicafacturas.R
import com.example.practicafacturas.databinding.ActivityMainBinding
import com.example.practicafacturas.model.Invoice
import com.example.practicafacturas.ui.Filter
import com.example.practicafacturas.ui.adapter.InvoiceAdapter
import com.example.practicafacturas.viewmodel.InvoiceViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var invoiceAdapter: InvoiceAdapter
    private var maxImporte: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.mainActivity_titulo)

        invoiceAdapter = InvoiceAdapter() {
                invoice -> onItemSelected()
        }
        initViewModel()
        initMainViewModel()

        val layoutManager = LinearLayoutManager(this)
        binding.rvFacturas.layoutManager = layoutManager

        // Estetico: divisor para cada elemento del recyclerView
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvFacturas.context,
            layoutManager.orientation
        )
        binding.rvFacturas.addItemDecoration(dividerItemDecoration)
    }

    private fun initViewModel() {
        binding.rvFacturas.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            invoiceAdapter = InvoiceAdapter() {
                invoice -> onItemSelected()
            }
            adapter = invoiceAdapter
        }
    }

    private fun initMainViewModel() {
        val viewModel = ViewModelProvider(this).get(InvoiceViewModel::class.java)

        viewModel.getAllRepositoryList().observe(this, Observer<List<Invoice>>{
            invoiceAdapter.setListInvoices(it)
            invoiceAdapter.notifyDataSetChanged()

            if (it.isEmpty()) {
                viewModel.makeApiCall()
                Log.d("Datos", it.toString())
            }
            // Obtenemos los datos de la lista de filtros de la otra actividad
            var datosFiltro = intent.getStringExtra("datosFiltro")
            // En caso de que los filtros no esten vacios
            if( datosFiltro != null) {
                var filtro = Gson().fromJson(datosFiltro, Filter::class.java)
                var invoiceList = it

                // Hacer que se cumplan los filtros de fecha
                invoiceList = comprobarFechaFiltro(filtro.fechaMin, filtro.fechaMax, invoiceList)

                // Hacemos que se ponga la lista filtrada
                invoiceAdapter.setListInvoices(invoiceList)
            }

            // Lista para almacenar la lista de facturas original y no tocarla
            val listaOriginal = it
            // Calcular el máximo importe de la lista
            maxImporte = obtenerMayorImporte(listaOriginal)
        })
    }

    // Funcion alertDialog al pulsar en una factura
    private fun onItemSelected() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.aDialog_titulo)
        builder.setMessage(R.string.aDialog_mensaje)
        builder.setPositiveButton(R.string.aDialog_boton) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    // Crear el menu para ir a la actividad de los filtros
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_view, menu)
        return true
    }

    // Funcionalidad del menu para ir a los filtros
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filtros_menu -> {
                val intent = Intent(this, FilterActivity::class.java)
                intent.putExtra("maxImporte", maxImporte)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Funcion para obtener el mayor importe de la lista
    private fun obtenerMayorImporte(listaFacturas: List<Invoice>): Double {
        var importeMaximo = 0.0
        for (factura in listaFacturas) {
            val facturaActual = factura.importeOrdenacion
            if(importeMaximo < facturaActual!!) importeMaximo = facturaActual
        }
        return  importeMaximo
    }

    // Funcion para los filtros de la fecha
    private fun comprobarFechaFiltro(fechaMin: String, fechaMax: String, invoiceList: List<Invoice>): List<Invoice> {
        // Lista auxiliar para devolverla despues
        val listaAux = ArrayList<Invoice>()
        if (fechaMin != R.string.btn_fecha.toString() && fechaMax != R.string.btn_fecha.toString()) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            var fechaMinima: Date? = null
            var fechaMaxima: Date? = null

            // Parseamos las fechas para cambiarlas de tipo String a tipo Date
            try {
                fechaMinima = simpleDateFormat.parse(fechaMin)
                fechaMaxima = simpleDateFormat.parse(fechaMax)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            // Recorremos la lista de facturas y aquellas que coincidan las añadimos a la lista auxiliar
            for (invoice in invoiceList) {
                var invoiceDate = Date()
                try {
                    invoiceDate = simpleDateFormat.parse(invoice.fecha)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                if (invoiceDate.after(fechaMinima) && invoiceDate.before(fechaMaxima)) {
                    listaAux.add(invoice)
                }
            }
        }
        return listaAux
    }
}