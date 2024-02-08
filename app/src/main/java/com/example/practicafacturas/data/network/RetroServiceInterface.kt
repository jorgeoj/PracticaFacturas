package com.example.practicafacturas.data.network

import co.infinum.retromock.meta.Mock
import co.infinum.retromock.meta.MockCircular
import co.infinum.retromock.meta.MockResponse
import co.infinum.retromock.meta.MockResponses
import com.example.practicafacturas.data.network.model.RepositoriesList
import retrofit2.Call
import retrofit2.http.GET

// Interfaz para simular respuestas de la API utilizando Retrofit y herramientas de mock
interface APIRetromockService: RetroServiceInterface {
    @Mock
    @MockResponses(
        MockResponse(body = "mock.json"),
        MockResponse(body = "mock2.json"),
        MockResponse(body = "mock3.json")
    )
    @MockCircular
    @GET("/")
    override fun getDataFromAPI(): Call<RepositoriesList>
}

// Interfaz para realizar solicitudes reales a la API en producción
interface APIRetrofitService: RetroServiceInterface {
    @GET("facturas")
    override fun getDataFromAPI(): Call<RepositoriesList>
}


// Interfaz para hacer solicitudes a la API usando retrofit
interface RetroServiceInterface {
    // Método que realiza una solicitud GET a la ruta "facturas" en la API
    fun getDataFromAPI(): Call<RepositoriesList>
}