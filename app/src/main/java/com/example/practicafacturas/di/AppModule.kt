package com.example.practicafacturas.di

import android.content.Context
import com.example.practicafacturas.database.InvoiceDao
import com.example.practicafacturas.database.InvoiceDatabase
import com.example.practicafacturas.network.RetroServiceInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Poporciona las dependencias necesarias para la inyección de dependencias en la app
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    // Método que proporciona una instancia de la BBDD Room
    @Provides
    @Singleton
    fun getAppDatabase(@ApplicationContext context: Context): InvoiceDatabase {
        return InvoiceDatabase.getAppDBInstance(context)
    }

    // Método que proporciona una instancia del DAO
    @Provides
    @Singleton
    fun getAppDao(invoiceDatabase: InvoiceDatabase): InvoiceDao {
        return invoiceDatabase.getAppDao()
    }

    // URL para las solicitudes de retrofit
    val URL = "https://viewnextandroid.wiremockapi.cloud/"

    // Método que proporciona una instancia del retrofit
    @Provides
    @Singleton
    fun getRetroServiceInterface(retrofit: Retrofit): RetroServiceInterface {
        return retrofit.create(RetroServiceInterface::class.java)
    }

    // Método que proporciona una instancia de retrofit
    @Provides
    @Singleton
    fun getRetroInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}