package com.example.practicafacturas.ui

data class Filter(
    var fechaMax: String,
    var fechaMin: String,
    var importe: Double,
    var estado: HashMap<String, Boolean>)