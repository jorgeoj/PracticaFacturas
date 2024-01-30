package com.example.practicafacturas.ui.view

data class Filter(
    var fechaMax: String,
    var fechaMin: String,
    var importe: Double,
    var estado: HashMap<String, Boolean>)