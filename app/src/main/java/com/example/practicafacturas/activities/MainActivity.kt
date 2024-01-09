package com.example.practicafacturas.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.practicafacturas.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar la barra de herramientas genérica
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Puedes modificar el título de la barra de herramientas si es necesario
        supportActionBar?.title = "Título de tu Actividad Principal"

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
}