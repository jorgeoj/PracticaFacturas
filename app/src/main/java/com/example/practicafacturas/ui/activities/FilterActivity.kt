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

        // Funcionalidad botones de fecha
        binding.btnFechaDesde.setOnClickListener { obtenerFecha(binding.btnFechaDesde) }
        binding.btnFechaHasta.setOnClickListener { obtenerFecha(binding.btnFechaHasta) }

        // Valores de los textView para el slider y cosas del slider
        binding.tvMinSlider.text = "${getString(R.string.txt_min_valor_slider)} €"
        binding.tvMaxSlider.text = "${(maxImporte.toInt() + 1)} €"
        binding.tvValorActual.text = "${getString(R.string.txt_min_valor_slider)} €"
        controlarSlider(maxImporte)


        // TODO Funcion al darle al botón de aplicar los filtros
        binding.btnAplicar.setOnClickListener { filtrarValores() }

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

    private fun obtenerFecha(btnFecha: Button) {
        val calendario = Calendar.getInstance()
        val anyo = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH) + 1
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,
            { view, year1, month, dayOfMonth ->
                btnFecha.text = "$dayOfMonth/${month + 1}/$year1"
            }, anyo, mes, dia)

        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    private fun controlarSlider(maxImporte: Double) {
        binding.slider.min = 0
        binding.slider.max = maxImporte.toInt() + 1
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

    //
    private fun filtrarValores() {
        // TODO aun hay que implementar el pasar los datos para filtrar
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // Función que restablece los filtros
    private fun eliminarValores() {
        // Resetear valores de los botones de fecha (No estoy seguro si esto funcionaría)
        binding.btnFechaDesde.setText(R.string.btn_fecha)
        binding.btnFechaHasta.setText(R.string.btn_fecha)

        // Restablecer valor del slider
        binding.slider.setProgress(1, true)

        // Restablecer valores de las checkBox
        binding.cbPagadas.isChecked = false
        binding.cbAnuladas.isChecked = false
        binding.cbCuotaFija.isChecked = false
        binding.cbPendientesPago.isChecked = false
        binding.cbPlanPago.isChecked = false
    }
}