package com.example.practicafacturas.ui.view.activities

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.widget.Toolbar
import com.example.practicafacturas.R
import com.example.practicafacturas.databinding.ActivityFilterBinding
import com.example.practicafacturas.ui.view.constants.Constants.Companion.ANULADAS
import com.example.practicafacturas.ui.view.constants.Constants.Companion.CUOTA_FIJA
import com.example.practicafacturas.ui.view.constants.Constants.Companion.PAGADAS
import com.example.practicafacturas.ui.view.constants.Constants.Companion.PENDIENTES_PAGO
import com.example.practicafacturas.ui.view.constants.Constants.Companion.PLAN_PAGO
import com.example.practicafacturas.ui.view.Filter
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FilterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterBinding
    private var maxPrice = 0
    private var filter: Filter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Función que inicializa la toolbar y la personaliza
        initializateToolbar()
        // Función que inicializa los componentes de la actividad
        initializateComponents()
    }

    // Crear el menu para ir a la actividad principal
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_filter_view, menu)
        return true
    }

    // Funcionalidad del menu para ir a la actividad principal sin hacer nada
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.cerrar_menu -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // Funcion para inicializar los componentes de la clase
    private fun initializateComponents() {
        // Funciones para inicializar los botones de fecha y el slider
        initializateCalendar()
        initializateSlider()
        // Funcion para aplicar los filtros guardados en las shared preferences
        applySavedFilters()

        // Obtenemos el objeto filtro de la otra clase, lo deserializamos y si no es nulo cargamos los filtros
        val jsonFilter = intent.getStringExtra("FILTRO_DATOS")
        if (jsonFilter != null) {
            filter = Gson().fromJson(jsonFilter, Filter::class.java)
            filter?.let { nonNullFilter ->
                loadFilters(nonNullFilter)
            }
        }

        // Funcionalidad al darle al botón de aplicar los filtros
        binding.btnApply.setOnClickListener {
            uploadSharedPreferences()
            filterValues()
        }

        // Funcionalidad al darle al boton de eliminar filtros
        binding.btnDelete.setOnClickListener { deleteValues() }
    }

    // Configurar la toolbar
    private fun initializateToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.filterActivity_title)
    }

    // Inicializamos los botones que funcionan como datePicker
    private fun initializateCalendar() {
        // Funcionalidad botones de fecha
        binding.btnDateFrom.setOnClickListener { obtainDate(binding.btnDateFrom, false) }
        binding.btnDateTo.setOnClickListener { obtainDate(binding.btnDateTo, true, minDate = obtainMinDateAux()) }
    }

    // Funcion para manejar la fecha seleccionada en los botones
    private fun obtainDate(btnDate: Button, minDateRestriction: Boolean, minDate: Long? = null) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        // Crear un DatePickerDialog con la fecha actual como predeterminada
        val datePickerDialog = DatePickerDialog(this,
            { view, year1, month1, dayOfMonth ->
                btnDate.text = "$dayOfMonth/${month1 + 1}/$year1"
            }, year, month, day)

        // Aplicar restriccion de fecha minima si es necesario
        if(minDateRestriction) { minDate?.let { datePickerDialog.datePicker.minDate = it } }
        datePickerDialog.show()
    }

    // Funcion para obtener el valor del boton de la fecha minima permitida
    private fun obtainMinDateAux(): Long {
        // Formato de fecha esperado en el botón
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Obtiene la fecha desde tu botón
        val selectedDateFrom = binding.btnDateFrom.text.toString()

        try {
            // Parsea la fecha y la devuelve como tipo Date
            val dateFrom = dateFormat.parse(selectedDateFrom)
            return dateFrom?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // En caso de error, devuelve 0
        return 0L
    }

    // Funcion para inicializar el slider de importe
    private fun initializateSlider() {
        // Obtenemos el mayor importe de la MainActivity
        maxPrice = intent.getDoubleExtra("maxPrice", 0.0).toInt() + 1

        // Establecer los valores de los TextView para el slider
        binding.tvMinSlider.text = "${getString(R.string.txt_min_value_slider)} €"
        binding.tvMaxSlider.text = "${maxPrice} €"
        binding.tvCurrentValue.text = "${maxPrice} €"

        // Configurar el slider
        controlSlider()
    }

    // Función para controlar la configuración del slider de importe
    private fun controlSlider() {
        // Valores mínimo, máximo y progreso del slider
        binding.slider.min = 0
        binding.slider.max = maxPrice
        binding.slider.progress = maxPrice

        // Listener para el cambio de progreso en el slider
        binding.slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            // Actualizar el valor actual en el TextView según el progreso del slider
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvCurrentValue.text = "${progress} €"
            }
            // Acciones cuando se inicia la interacción con el slider
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d("onStartTrackingTouch", "onStartTrackingTouch: ha fallado")
            }
            // Acciones cuando se detiene la interacción con el slider
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d("onStopTrackingTouch", "onStopTrackingTouch: ha fallado")
            }
        })
    }

    // Función para aplicar los filtros y volver a la actividad principal
    private fun filterValues() {
        val gson = Gson()
        val intent = Intent(this, MainActivity::class.java)

        // Guardamos los valores en un objeto filtro para despues pasarlo a la otra actividad
        val chBoxStatus = hashMapOf(
            PAGADAS to binding.cbPaid.isChecked,
            ANULADAS to binding.cbCancelled.isChecked,
            CUOTA_FIJA to binding.cbFixedFee.isChecked,
            PENDIENTES_PAGO to binding.cbPendingPayment.isChecked,
            PLAN_PAGO to binding.cbPaymentPlan.isChecked
        )
        val minDate = binding.btnDateFrom.text.toString()
        val maxDate = binding.btnDateTo.text.toString()
        val price = binding.slider.progress.toDouble()
        // Objeto filtro con todos los valores obtenidos
        val filtro = Filter(maxDate, minDate, price, chBoxStatus)

        // Guardamos los estados de los filtros
        saveFilterStatus(filtro)
        intent.putExtra("datosFiltro", gson.toJson(filtro))

        startActivity(intent)

        finish()
    }

    // Función que restablece los valores de los filtros
    private fun deleteValues() {
        // Resetear valores de los botones de fecha
        binding.btnDateFrom.setText(R.string.btn_date)
        binding.btnDateTo.setText(R.string.btn_date)

        // Restablecer valor del slider al máximo
        binding.slider.setProgress(maxPrice)

        // Restablecer valores de las checkBox
        binding.cbPaid.isChecked = false
        binding.cbCancelled.isChecked = false
        binding.cbFixedFee.isChecked = false
        binding.cbPendingPayment.isChecked = false
        binding.cbPaymentPlan.isChecked = false
    }

    // Función para guardar el estado de los filtros en SharedPreferences
    private fun saveFilterStatus(filter: Filter) {
        // Obtenemos el objeto SharedPreferences
        val preferences = getPreferences(MODE_PRIVATE)

        val gson = Gson()
        val jsonFilter = gson.toJson(filter)

        // Guardar el JSON resultante en SharedPreferences con la clave "ESTADO_FILTRO"
        preferences.edit().putString("ESTADO_FILTRO", jsonFilter).apply()
    }

    // Función para aplicar los filtros guardados desde SharedPreferences
    private fun applySavedFilters() {
        // Obtenemos las SharedPrefereces
        val preferences = getPreferences(MODE_PRIVATE)
        // Obtenemos el JSON cuya clave es "ESTADO_FILTRO"
        val jsonFilter = preferences.getString("ESTADO_FILTRO", null)

        // Verificar si se ha encontrado una cadena JSON
        if (jsonFilter != null) {
            val gson = Gson()
            filter = gson.fromJson(jsonFilter, Filter::class.java)
            // Si el objeto filtro no es nulo cargar los filtros
            filter?.let { nonNullFilter ->
                loadFilters(nonNullFilter)
            }
        }
    }

    // Función para cargar los filtros
    private fun loadFilters(filter: Filter) {
        binding.btnDateFrom.text = filter.fechaMin
        binding.btnDateTo.text = filter.fechaMax
        binding.slider.progress = filter.importe.toInt()
        binding.cbPaid.isChecked = filter.estado[PAGADAS] ?: false
        binding.cbCancelled.isChecked = filter.estado[ANULADAS] ?: false
        binding.cbFixedFee.isChecked = filter.estado[CUOTA_FIJA] ?: false
        binding.cbPendingPayment.isChecked = filter.estado[PENDIENTES_PAGO] ?: false
        binding.cbPaymentPlan.isChecked = filter.estado[PLAN_PAGO] ?: false
    }

    // Función para actualizar SharedPreferences con los filtros actuales
    private fun uploadSharedPreferences() {
        // Obtenemos todos los datos que necesitamos y los almacenaremos en un objeto filtro
        val slider = binding.slider.progress.toDouble()
        val checkBoxes = hashMapOf(
            PAGADAS to binding.cbPaid.isChecked,
            ANULADAS to binding.cbCancelled.isChecked,
            CUOTA_FIJA to binding.cbFixedFee.isChecked,
            PENDIENTES_PAGO to binding.cbPendingPayment.isChecked,
            PLAN_PAGO to binding.cbPaymentPlan.isChecked
        )
        val minDate = binding.btnDateFrom.text.toString()
        val maxDate = binding.btnDateTo.text.toString()
        filter = Filter(maxDate, minDate, slider, checkBoxes)

        // Guardar el estado actual de los filtros en las Sharedreferences
        saveFilterStatus(filter!!)
    }
}