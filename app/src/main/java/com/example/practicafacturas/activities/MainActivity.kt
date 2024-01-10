package com.example.practicafacturas.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practicafacturas.Bill
import com.example.practicafacturas.R
import com.example.practicafacturas.adapter.BillAdapter
import com.example.practicafacturas.adapter.BillProvider
import com.example.practicafacturas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BillAdapter
    private lateinit var lista: MutableList<Bill>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Llenar con la lista del provider
        lista = mutableListOf()
        lista = BillProvider.lista

        // Configurar la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.mainActivity_titulo)

        adapter = BillAdapter(lista){ factura ->
            onItemSelected(factura)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvFacturas.layoutManager = layoutManager

        // Estetico: divisor para cada elemento del recyclerView
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvFacturas.context,
            layoutManager.orientation
        )
        binding.rvFacturas.addItemDecoration(dividerItemDecoration)

        binding.rvFacturas.adapter = adapter

        // TODO: Hacer que si la lista está vacia muestre el textView de lista vacía
        /*
        val recyclerView: RecyclerView = findViewById(R.id.rvPaises)
        val textViewListaVacia: TextView = findViewById(R.id.textViewListaVacia)

        // Tu lógica para llenar el RecyclerView

        if (miListaDeElementos.isEmpty()) {
            // Si la lista está vacía, muestra el TextView
            textViewListaVacia.visibility = View.VISIBLE
        } else {
            // Si la lista no está vacía, oculta el TextView
            textViewListaVacia.visibility = View.GONE
        }
         */
    }

    // Funcion alertDialog al pulsar en una factura
    private fun onItemSelected(factura: Bill) {
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
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}