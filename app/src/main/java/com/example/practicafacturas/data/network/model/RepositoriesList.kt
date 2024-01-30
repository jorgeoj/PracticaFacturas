package com.example.practicafacturas.data.network.model

// Clase de datos que representa una lista de facturas junto con el número total de facturas
data class RepositoriesList(
    val numFacturas: Int, // Número total de facturas
    val facturas: List<InvoiceResponse> // Lista de facturas
)
