package com.example.practicafacturas.data.network.model

// Clase de datos que representa la respuesta del servicio relacionado con facturas
data class InvoiceResponse(
    val descEstado: String,
    val fecha: String,
    val importeOrdenacion: Double
)
