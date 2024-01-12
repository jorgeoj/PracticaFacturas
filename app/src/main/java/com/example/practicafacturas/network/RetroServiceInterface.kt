package com.example.practicafacturas.network

import com.example.practicafacturas.model.RepositoriesList
import retrofit2.Call
import retrofit2.http.GET

// Interfaz para hacer solicitudes a la API usando retrofit
interface RetroServiceInterface {
    // Método que realiza una solicitud GET a la ruta "facturas" en la API
    @GET("facturas")
    fun getDataFromAPI(): Call<RepositoriesList>
}