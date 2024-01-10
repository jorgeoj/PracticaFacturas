package com.example.practicafacturas.activities

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.example.practicafacturas.R
import com.example.practicafacturas.databinding.ActivityFilterBinding
import java.util.Calendar
import java.util.Date

class FilterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.filterActivity_titulo)

        // Obtenemos los datos al pasar de una actividad a otra
        var maxImporte = intent.getDoubleExtra("maxImporte", 0.0)

        // Valores de los textView para el slider
        binding.tvMinSlider.setText(R.string.txt_min_valor_slider)
        binding.tvMaxSlider.text = maxImporte.toString()
        //TODO mirar como poner valor maximo al slider

        // Funcionalidad boton fecha inicial
        binding.btnFechaDesde.setOnClickListener { obtenerFechaInicio() }

        // TODO Funcion al darle al botón de aplicar los filtros

        // Funcionalidad al darle al boton de eliminar filtros
        binding.btnEliminar.setOnClickListener { eliminarValores() }
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

    private fun obtenerFechaInicio() {
        val calendario = Calendar.getInstance()
        val anyo = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH) + 1
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,
            { view, year1, month, dayOfMonth ->
                binding.btnFechaDesde.text = "$dayOfMonth/${month + 1}/$year1"
            }, anyo, mes, dia)

        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    // Función que restablece los filtros
    private fun eliminarValores() {
        // Resetear valores de los botones de fecha (No estoy seguro si esto funcionaría)
        binding.btnFechaDesde.setText(R.string.btn_fecha)
        binding.btnFechaHasta.setText(R.string.btn_fecha)

        // Restablecer valor del slider
        binding.slider.value = 1.0f

        // Restablecer valores de las checkBox
        binding.cbPagadas.isChecked = false
        binding.cbAnuladas.isChecked = false
        binding.cbCuotaFija.isChecked = false
        binding.cbPendientesPago.isChecked = false
        binding.cbPlanPago.isChecked = false
    }
}