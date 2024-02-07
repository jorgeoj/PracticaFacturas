package com.example.practicafacturas.di

import android.content.Context
import co.infinum.retromock.Retromock
import com.example.practicafacturas.ResourceBodyFactory
import com.example.practicafacturas.data.database.InvoiceDao
import com.example.practicafacturas.data.database.InvoiceDatabase
import com.example.practicafacturas.data.network.APIRetrofitService
import com.example.practicafacturas.data.network.APIRetromockService
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

    // TODO: volver a comentar esto
    @Provides
    @Singleton
    fun getRetrofit(retrofit: Retrofit): APIRetrofitService {
        return retrofit.create(APIRetrofitService::class.java)

    }

    @Provides
    @Singleton
    fun getRetromock(retromock: Retromock): APIRetromockService {
        return retromock.create(APIRetromockService::class.java)

    }

    @Provides
    @Singleton
    fun buildRetromock(retrofit: Retrofit): Retromock {
        return Retromock.Builder()
            .retrofit(retrofit)
            .defaultBodyFactory(ResourceBodyFactory())
            .build()

    }

    // Método que proporciona una instancia de retrofit
    @Provides
    @Singleton
    fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}