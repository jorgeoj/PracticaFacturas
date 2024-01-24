package com.example.practicafacturas.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
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
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var invoiceAdapter: InvoiceAdapter
    private var filter: Filter? = null
    private var maxPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos la toolbar
        initializateToolbar()

        // Creamos un adaptador de facturas con una función cuando se selecciona una factura
        invoiceAdapter = InvoiceAdapter { invoice -> onItemSelected() }

        // Inicializamos el viewModel y el de la actividad
        initViewModel()
        initMainViewModel()

        // Configurar el administrador de diseño para el RecyclerView
        val layoutManager = LinearLayoutManager(this)
        binding.rvFacturas.layoutManager = layoutManager

        // Estetico: divisor para cada elemento del recyclerView
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvFacturas.context,
            layoutManager.orientation
        )
        binding.rvFacturas.addItemDecoration(dividerItemDecoration)
    }

    // Crear el menu para ir a la actividad de los filtros
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_view, menu)
        return true
    }

    // Funcionalidad del menu para ir a la actividad de los filtros
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filtros_menu -> {
                val intent = Intent(this, FilterActivity::class.java)
                intent.putExtra("maxPrice", maxPrice)
                if (filter != null) {
                    var gson = Gson()
                    intent.putExtra("FILTRO_DATOS", gson.toJson(filter))
                }
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Configurar la toolbar
    private fun initializateToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.mainActivity_titulo)
    }

    // Configurar el RecyclerView y su adaptador dentro del ViewModel
    private fun initViewModel() {
        binding.rvFacturas.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            invoiceAdapter = InvoiceAdapter {
                invoice -> onItemSelected()
            }
            adapter = invoiceAdapter
        }
    }

    // Inicializar y configurar el ViewModel principal
    private fun initMainViewModel() {
        val viewModel = ViewModelProvider(this).get(InvoiceViewModel::class.java)

        // Observar la lista de facturas en el ViewModel y manejar los cambios
        viewModel.getAllRepositoryList().observe(this) { invoices ->
            handleInvoiceList(invoices)
        }
    }

    // Manejar la lista de facturas
    private fun handleInvoiceList(invoices: List<Invoice>) {
        // Obtenemos la lista filtrada de las sharedPreferences y verificamos si esta vacía o nula
        var listWithFilter = getFilteredListSharedPreferences()

        if (listWithFilter == null || listWithFilter.isEmpty()) {
            invoiceAdapter.setListInvoices(invoices) // Establecer lista original en el adaptador
        } else {
            invoiceAdapter.setListInvoices(listWithFilter) // Establecer lista filtrada en el adaptador
        }

        // Notificar sobre los cambios en la lista
        invoiceAdapter.notifyDataSetChanged()

        // Manejar la situación en que la lista está vacía
        if (invoices.isEmpty()) { handleEmptyInvoiceList() }

        // Manejar los datos de filtro desde la otra actividad
        handleFilterData(intent.getStringExtra("datosFiltro"), invoices)

        // Calcular el máximo importe
        maxPrice = getMaxPrice(invoices)
    }

    // Función para manejar la situación en la que la lista está vacía
    private fun handleEmptyInvoiceList() {
        val viewModel = ViewModelProvider(this).get(InvoiceViewModel::class.java)
        viewModel.makeApiCall()
        Log.d("Datos", "Lista vacia")
    }

    // Manejar los datos de filtro de FilterActivity
    private fun handleFilterData(filterData: String?, invoices: List<Invoice>) {
        // Verificar si los datos de filtro no son nulos
        filterData?.let {
            filter = Gson().fromJson(it, Filter::class.java)

            // Verificar que el objeto filtro obtenido no es nulo
            filter?.let { nonNullFilter ->
                var invoiceList = invoices

                // Aplicar filtros de fecha, importe y estado
                invoiceList = checkFilterDate(
                    nonNullFilter.fechaMin,
                    nonNullFilter.fechaMax,
                    invoiceList
                )
                invoiceList = checkFilterPrice(nonNullFilter.importe, invoiceList)
                invoiceList = checkFilterChBoxStatus(nonNullFilter.estado, invoiceList)

                // Guardar lista filtrada en SharedPreferences
                saveFilteredListSharedPreferences(invoiceList)

                // Mostrar textView si la lista filtrada es vacía
                if (invoiceList.isEmpty()) {
                    binding.tvVacio.visibility = View.VISIBLE
                }

                // Actualizar adaptador con la lista filtrada
                invoiceAdapter.setListInvoices(invoiceList)
            }
        }
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

    // Funcion para obtener el mayor importe de la lista
    // Utiliza maxByOrNull para encontrar el valor máximo del importe, si es nulo se usa el valor predeterminado 0.0
    private fun getMaxPrice(billList: List<Invoice>): Double {
        return billList.maxByOrNull { it.importeOrdenacion ?: 0.0 }?.importeOrdenacion ?: 0.0
    }

    // Funcion para los filtros de la fecha
    private fun checkFilterDate(minDate: String, maxDate: String, invoiceList: List<Invoice>): List<Invoice> {
        // Lista auxiliar para devolverla despues
        val auxList = ArrayList<Invoice>()

        // Verificar si se han seleccionado fechas válidas
        if (minDate != getString(R.string.btn_fecha) && maxDate != getString(R.string.btn_fecha)) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Para parsear las fechas

            val dateMinimum: Date? = sdf.parse(minDate)
            val maximumDate: Date? = sdf.parse(maxDate)

            for (bill in invoiceList) {
                val date = sdf.parse(bill.fecha)!! // Obtenemos la fecha y la parseamos

                // Verificar si la fecha de la factura esta dentro del rango
                if (date.after(dateMinimum) && date.before(maximumDate)) {
                    auxList.add(bill)
                }
            }

        } else {
            return invoiceList
        }
        return auxList
    }

    // Funcion para los filtros del importe
    private fun checkFilterPrice(price: Double, invoiceList: List<Invoice>): List<Invoice> {
        // Creamos una lista auxiliar para después devolverla
        var auxList = ArrayList<Invoice>()
        // Recorremos la lista y si el importe de la factura es menor que el importe seleccionado, la añadimos a la lista
        for (bill in invoiceList) {
            if (bill.importeOrdenacion!! < price) {
                auxList.add(bill)
            }
        }
        return auxList
    }

    // Funcion para los filtros de los estados de las checkBox
    private fun checkFilterChBoxStatus(status: HashMap<String, Boolean>, invoiceList: List<Invoice>): List<Invoice> {
        val filteredList = ArrayList<Invoice>()

        val selectedStatus = status.filterValues { it }.keys

        if (selectedStatus.isEmpty()) {
            return invoiceList
        }

        for (invoice in invoiceList) {
            val invoiceState = invoice.descEstado

            if (selectedStatus.contains(invoiceState)) {
                filteredList.add(invoice)
            }
        }

        return filteredList
    }

    // Función para guardar la lista filtrada en SharedPreferences
    private fun saveFilteredListSharedPreferences(filteredList: List<Invoice>) {
        val gson = Gson()
        val jsonFilteredList = gson.toJson(filteredList)

        // Obtenemos una instancia de las SharedPreferences y guardamos la lista filtrada en SharedPreferences con una clave "LISTA_FILTRADA"
        val sharedPreferences: SharedPreferences = getPreferences(MODE_PRIVATE)
        sharedPreferences.edit().putString("LISTA_FILTRADA", jsonFilteredList).apply()
    }

    // Función para obtener la lista filtrada desde SharedPreferences
    private fun getFilteredListSharedPreferences(): List<Invoice>? {
        val sharedPreferences: SharedPreferences = getPreferences(MODE_PRIVATE)
        val jsonFilteredList: String? = sharedPreferences.getString("LISTA_FILTRADA", null)

        // Si la lista filtrada en formato JSON no es nula, convertirla a una lista de Invoice
        return if (jsonFilteredList != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Invoice>>() {}.type
            gson.fromJson(jsonFilteredList, type)
        } else {
            null // Si la lista filtrada en formato JSON es nula, devolver null
        }
    }
}