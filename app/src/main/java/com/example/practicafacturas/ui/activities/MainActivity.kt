package com.example.practicafacturas.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practicafacturas.R
import com.example.practicafacturas.databinding.ActivityMainBinding
import com.example.practicafacturas.model.Invoice
import com.example.practicafacturas.ui.adapter.InvoiceAdapter
import com.example.practicafacturas.viewmodel.InvoiceViewModel
import dagger.hilt.android.AndroidEntryPoint

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
                invoice -> onItemSelected(invoice)
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

        // Calcular el máximo importe de la lista
        // TODO arreglar esto
        // maxImporte = obtenerMayorImporte()
    }



    private fun initViewModel() {
        binding.rvFacturas.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            invoiceAdapter = InvoiceAdapter() {
                invoice -> onItemSelected(invoice)
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
        })
    }

    // Funcion alertDialog al pulsar en una factura
    private fun onItemSelected(factura: Invoice) {
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
    /*
    private fun obtenerMayorImporte(): Double {
        var importeMaximo = 0.0
        for (factura in listaOriginal) {
            val facturaActual = factura.importeOrdenacion
            if(importeMaximo < facturaActual) importeMaximo = facturaActual
        }
        return  importeMaximo
    }*/
}