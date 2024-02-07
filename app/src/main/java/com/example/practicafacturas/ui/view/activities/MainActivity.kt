package com.example.practicafacturas.ui.view.activities

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
import com.example.practicafacturas.data.database.Invoice
import com.example.practicafacturas.ui.view.Filter
import com.example.practicafacturas.ui.view.adapter.InvoiceAdapter
import com.example.practicafacturas.ui.viewmodel.InvoiceViewModel
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
        invoiceAdapter = InvoiceAdapter { onItemSelected() }

        // Inicializamos el viewModel y el de la actividad
        initViewModel()
        initMainViewModel()

        // Configurar el administrador de diseño para el RecyclerView
        val layoutManager = LinearLayoutManager(this)
        binding.rvInvoices.layoutManager = layoutManager

        // Estetico: divisor para cada elemento del recyclerView
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvInvoices.context,
            layoutManager.orientation
        )
        binding.rvInvoices.addItemDecoration(dividerItemDecoration)

        //establecer el estado del switch
        val switchEstado = cargarEstadoSwitch()
        binding.switchRetromock.isChecked = switchEstado
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
                    val gson = Gson()
                    intent.putExtra("FILTRO_DATOS", gson.toJson(filter))
                }

                // TODO Mirar esto que me dijo Carlos y hacer pruebas
                //startActivityForResult(intent, 20)

                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // TODO Mirar esto que me dijo Carlos y hacer pruebas
    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
    */

    // Al presionar hacia atrás en MainActivity, cierra la aplicación
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    // Configurar la toolbar
    private fun initializateToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.mainActivity_title)
    }

    // Configurar el RecyclerView y su adaptador dentro del ViewModel
    private fun initViewModel() {
        binding.rvInvoices.apply {
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

            binding.switchRetromock.setOnClickListener {
                val isChecked = binding.switchRetromock.isChecked
                guardarEstadoSwitch(isChecked)
                if (binding.switchRetromock.isChecked) {
                    viewModel.changeService("ficticio")
                    viewModel.makeApiCall()
                } else {
                    viewModel.changeService("real")
                    viewModel.makeApiCall()
                }
            }
        }
    }

    // Método para guardar el estado del switch en las SharedPreferences
    private fun guardarEstadoSwitch(estado: Boolean) {
        val preferences = getPreferences(MODE_PRIVATE)
        preferences.edit().putBoolean("switch_estado", estado).apply()
    }

    // Método para cargar el estado del switch desde las SharedPreferences
    private fun cargarEstadoSwitch(): Boolean {
        val preferences = getPreferences(MODE_PRIVATE)
        return preferences.getBoolean("switch_estado", false) // El segundo parámetro es el valor por defecto
    }

    // Manejar la lista de facturas
    private fun handleInvoiceList(invoices: List<Invoice>) {
        // Obtenemos la lista filtrada de las sharedPreferences y verificamos si esta vacía o nula
        val listWithFilter = getFilteredListSharedPreferences()

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
                    binding.tvEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvEmpty.visibility = View.INVISIBLE
                }

                // Actualizar adaptador con la lista filtrada
                invoiceAdapter.setListInvoices(invoiceList)
            }
        }
    }

    // Funcion alertDialog al pulsar en una factura
    private fun onItemSelected() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.aDialog_title)
        builder.setMessage(R.string.aDialog_message)
        builder.setPositiveButton(R.string.aDialog_button) { dialog, _ ->
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
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Para parsear las fechas

        var dateMinimum: Date?
        var maximumDate: Date?

        // Verificar si se han seleccionado las dos fechas
        if (minDate != getString(R.string.btn_date) && maxDate != getString(R.string.btn_date)) {
            dateMinimum = sdf.parse(minDate)
            maximumDate = sdf.parse(maxDate)


        } else if (minDate != getString(R.string.btn_date) && maxDate == getString(R.string.btn_date)) {
            dateMinimum = sdf.parse(minDate)
            maximumDate = Date() // Obtiene la fecha actual

        } else if(minDate == getString(R.string.btn_date) && maxDate != getString(R.string.btn_date)) {
            dateMinimum = Date(Long.MIN_VALUE) // Obtiene el minimo valor posible
            maximumDate = sdf.parse(maxDate)

        } else {
            return invoiceList // Devuelve la lista recibida en caso que no cumpla ninguna condicion
        }

        for (bill in invoiceList) {
            val date = bill.fecha?.let { sdf.parse(it) }!! // Obtenemos la fecha y la parseamos

            // Verificar si la fecha de la factura esta dentro del rango
            if (date.after(dateMinimum) && date.before(maximumDate)) {
                auxList.add(bill)
            }
        }

        return auxList
    }

    // Funcion para los filtros del importe
    private fun checkFilterPrice(price: Double, invoiceList: List<Invoice>): List<Invoice> {
        // Creamos una lista auxiliar para después devolverla
        val auxList = ArrayList<Invoice>()
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