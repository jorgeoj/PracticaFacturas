package com.example.practicafacturas.network

import com.example.practicafacturas.model.RepositoriesList
import retrofit2.Call
import retrofit2.http.GET

interface RetroServiceInterface {
    @GET("facturas")
    fun getDataFromAPI(): Call<RepositoriesList>
}