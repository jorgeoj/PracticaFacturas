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
import com.example.practicafacturas.ui.Constants.Companion.ANULADAS
import com.example.practicafacturas.ui.Constants.Companion.CUOTA_FIJA
import com.example.practicafacturas.ui.Constants.Companion.PAGADAS
import com.example.practicafacturas.ui.Constants.Companion.PENDIENTES_PAGO
import com.example.practicafacturas.ui.Constants.Companion.PLAN_PAGO
import com.example.practicafacturas.ui.Filter
import com.example.practicafacturas.ui.adapter.InvoiceAdapter
import com.example.practicafacturas.viewmodel.InvoiceViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
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
                // Hacer que se cumplan los filtros de importe
                invoiceList = comprobarImporteFiltro(filtro.importe, invoiceList)
                // Hacer que se cumplan los filtros de estado
                invoiceList = comprobarEstadosFiltro(filtro.estado, invoiceList)

                // Hacemos que se ponga la lista filtrada
                invoiceAdapter.setListInvoices(invoiceList)
            }

            // Calcular el máximo importe de la lista
            maxImporte = obtenerMayorImporte(it)
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
        if (fechaMin != getString(R.string.btn_fecha) && fechaMax != getString(R.string.btn_fecha)) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val fechaMinima: Date? = sdf.parse(fechaMin)
            val fechaMaxima: Date? = sdf.parse(fechaMax)

            for (factura in invoiceList) {
                val fecha = sdf.parse(factura.fecha)!!
                if (fecha.after(fechaMinima) && fecha.before(fechaMaxima)) {
                    listaAux.add(factura)
                }
            }

        } else {
            return invoiceList
        }
        return listaAux
    }

    // Funcion para los filtros del importe
    private fun comprobarImporteFiltro(importe: Double, invoiceList: List<Invoice>): List<Invoice> {
        // Creamos una lista auxiliar para después devolverla
        var listaAux = ArrayList<Invoice>()
        // Recorremos la lista y si el importe de la factura es menor que el importe seleccionado, la añadimos a la lista
        for (factura in invoiceList) {
            if (factura.importeOrdenacion!! < importe) {
                listaAux.add(factura)
            }
        }
        return listaAux
    }

    // Funcion para los filtros de estado
    private fun comprobarEstadosFiltro(estado: HashMap<String, Boolean>, invoiceList: List<Invoice>): List<Invoice> {
        // Creamos una lista auxiliar para después devolverla
        var listaAux = ArrayList<Invoice>()

        val chBoxPagadas = estado[PAGADAS] ?: false
        val chBoxAnuladas = estado[ANULADAS] ?: false
        val chBoxCuotaFija = estado[CUOTA_FIJA] ?: false
        val chBoxPendientePago = estado[PENDIENTES_PAGO] ?: false
        val chBoxPlanPago = estado[PLAN_PAGO] ?: false

        if (!chBoxPagadas && !chBoxAnuladas && !chBoxCuotaFija && !chBoxPendientePago && !chBoxPlanPago) {
            return invoiceList
        }

        for (invoice in invoiceList) {
            val invoiceState = invoice.descEstado
            val esPagada = invoiceState == getString(R.string.chBox_pagadas)
            val esAnulada = invoiceState == getString(R.string.chBox_anuladas)
            val esCuotaFija = invoiceState == getString(R.string.chBox_cFija)
            val esPendientePago = invoiceState == getString(R.string.chBox_pendientePago)
            val esPlanPago = invoiceState == getString(R.string.chBox_planPago)

            if ((esPagada && chBoxPagadas) || (esAnulada && chBoxAnuladas) || (esCuotaFija && chBoxCuotaFija) || (esPendientePago && chBoxPendientePago) || (esPlanPago && esPlanPago)) {
                listaAux.add(invoice)
            }
        }
        return listaAux
    }
}