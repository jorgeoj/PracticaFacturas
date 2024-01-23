package com.example.practicafacturas.ui.activities

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
import com.example.practicafacturas.ui.Constants.Companion.ANULADAS
import com.example.practicafacturas.ui.Constants.Companion.CUOTA_FIJA
import com.example.practicafacturas.ui.Constants.Companion.PAGADAS
import com.example.practicafacturas.ui.Constants.Companion.PENDIENTES_PAGO
import com.example.practicafacturas.ui.Constants.Companion.PLAN_PAGO
import com.example.practicafacturas.ui.Filter
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FilterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterBinding
    private var maxImporte = 0
    private var filtro: Filter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarComponentes()

        // Configurar la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.filterActivity_titulo)
    }

    // Crear el menu para ir a la actividad principal
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_filter_view, menu)
        return true
    }

    // Funcionalidad del menu para ir a la actividad principal sin hacer nada
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cerrar_menu -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // Funcion para inicializar los componentes de la clase
    private fun inicializarComponentes() {
        inicializarCalendario()
        inicializarSlider()

        aplicarFiltrosGuardados()

        val filtroJson = intent.getStringExtra("FILTRO_DATOS")
        if (filtroJson != null) {
            filtro = Gson().fromJson(filtroJson, Filter::class.java)
            filtro?.let { filtroNoNulo ->
                cargarFiltros(filtroNoNulo)
            }
        }

        // Funcionalidad al darle al botón de aplicar los filtros
        binding.btnAplicar.setOnClickListener {
            actualizarSharedPreferences()
            filtrarValores()
        }

        // Funcionalidad al darle al boton de eliminar filtros
        binding.btnEliminar.setOnClickListener { eliminarValores() }
    }

    private fun inicializarCalendario() {
        // Funcionalidad botones de fecha
        binding.btnFechaDesde.setOnClickListener { obtenerFecha(binding.btnFechaDesde, false) }
        binding.btnFechaHasta.setOnClickListener { obtenerFecha(binding.btnFechaHasta, true, fechaMinima = obtenerFechaDesdeAux()) }
    }

    private fun inicializarSlider() {
        maxImporte = intent.getDoubleExtra("maxImporte", 0.0).toInt() + 1

        // Valores de los textView para el slider y cosas del slider
        binding.tvMinSlider.text = "${getString(R.string.txt_min_valor_slider)} €"
        binding.tvMaxSlider.text = "${maxImporte} €"
        binding.tvValorActual.text = "${maxImporte} €"
        controlarSlider()
    }

    private fun aplicarFiltrosGuardados() {
        val preferences = getPreferences(MODE_PRIVATE)
        val filtroJson = preferences.getString("ESTADO_FILTRO", null)

        if (filtroJson != null) {
            val gson = Gson()
            filtro = gson.fromJson(filtroJson, Filter::class.java)
            filtro?.let { filtroNoNulo ->
                cargarFiltros(filtroNoNulo)
            }
        }
    }

    private fun guardarEstadoFiltros(filter: Filter) {
        val preferences = getPreferences(MODE_PRIVATE)
        val gson = Gson()
        val filtroJson = gson.toJson(filter)

        preferences.edit().putString("ESTADO_FILTRO", filtroJson).apply()
    }

    // Funcion para la fecha de los botones
    private fun obtenerFecha(btnFecha: Button, restriccionMinDate: Boolean, fechaMinima: Long? = null) {
        val calendario = Calendar.getInstance()
        val anyo = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH) + 1
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,
            { view, year1, month, dayOfMonth ->
                btnFecha.text = "$dayOfMonth/${month + 1}/$year1"
            }, anyo, mes, dia)

        // datePickerDialog.datePicker.maxDate = Date().time
        if(restriccionMinDate) {
            fechaMinima?.let { datePickerDialog.datePicker.minDate = it }

        }
        datePickerDialog.show()
    }

    // Funcion para obtener el valor del boton de la fecha desde
    private fun obtenerFechaDesdeAux(): Long {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Obtiene la fecha desde tu botón
        val fechaDesdeTexto = binding.btnFechaDesde.text.toString()

        try {
            // Parsea la fecha y devuelve el tiempo en milisegundos
            val fechaDesde = formato.parse(fechaDesdeTexto)
            return fechaDesde?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // En caso de error, devuelve 0
        return 0L
    }

    private fun controlarSlider() {
        binding.slider.min = 0
        binding.slider.max = maxImporte
        binding.slider.progress = maxImporte
        binding.slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvValorActual.text = "${progress} €"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d("onStartTrackingTouch", "onStartTrackingTouch: ha fallado")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d("onStopTrackingTouch", "onStopTrackingTouch: ha fallado")
            }
        })
    }

    // Función para los filtros
    private fun filtrarValores() {
        val gson = Gson()
        val intent = Intent(this, MainActivity::class.java)

        val estadosCB = hashMapOf(
            PAGADAS to binding.cbPagadas.isChecked,
            ANULADAS to binding.cbAnuladas.isChecked,
            CUOTA_FIJA to binding.cbCuotaFija.isChecked,
            PENDIENTES_PAGO to binding.cbPendientesPago.isChecked,
            PLAN_PAGO to binding.cbPlanPago.isChecked
        )

        var fechaMin = binding.btnFechaDesde.text.toString()
        var fechaMax = binding.btnFechaHasta.text.toString()
        var importe = binding.slider.progress.toDouble()

        var filtro = Filter(fechaMax, fechaMin, importe, estadosCB)

        guardarEstadoFiltros(filtro)

        intent.putExtra("datosFiltro", gson.toJson(filtro))

        startActivity(intent)
    }

    // Función que restablece los filtros
    private fun eliminarValores() {
        // Resetear valores de los botones de fecha (No estoy seguro si esto funcionaría)
        binding.btnFechaDesde.setText(R.string.btn_fecha)
        binding.btnFechaHasta.setText(R.string.btn_fecha)

        // Restablecer valor del slider
        binding.slider.setProgress(maxImporte)

        // Restablecer valores de las checkBox
        binding.cbPagadas.isChecked = false
        binding.cbAnuladas.isChecked = false
        binding.cbCuotaFija.isChecked = false
        binding.cbPendientesPago.isChecked = false
        binding.cbPlanPago.isChecked = false
    }

    private fun cargarFiltros(filter: Filter) {
        binding.btnFechaDesde.text = filter.fechaMin
        binding.btnFechaHasta.text = filter.fechaMax
        binding.slider.progress = filter.importe.toInt()
        binding.cbPagadas.isChecked = filter.estado[PAGADAS] ?: false
        binding.cbAnuladas.isChecked = filter.estado[ANULADAS] ?: false
        binding.cbCuotaFija.isChecked = filter.estado[CUOTA_FIJA] ?: false
        binding.cbPendientesPago.isChecked = filter.estado[PENDIENTES_PAGO] ?: false
        binding.cbPlanPago.isChecked = filter.estado[PLAN_PAGO] ?: false
    }

    private fun actualizarSharedPreferences() {
        val slider = binding.slider.progress.toDouble()
        val checkBoxes = hashMapOf(
            PAGADAS to binding.cbPagadas.isChecked,
            ANULADAS to binding.cbAnuladas.isChecked,
            CUOTA_FIJA to binding.cbCuotaFija.isChecked,
            PENDIENTES_PAGO to binding.cbPendientesPago.isChecked,
            PLAN_PAGO to binding.cbPlanPago.isChecked
        )
        val fechaMin = binding.btnFechaDesde.text.toString()
        val fechaMax = binding.btnFechaHasta.text.toString()
        filtro = Filter(fechaMax, fechaMin, slider, checkBoxes)

        guardarEstadoFiltros(filtro!!)
    }
}