package com.example.practicafacturas.data.repository

import androidx.lifecycle.LiveData
import com.example.practicafacturas.data.database.InvoiceDao
import com.example.practicafacturas.data.database.Invoice
import com.example.practicafacturas.data.network.APIRetrofitService
import com.example.practicafacturas.data.network.APIRetromockService
import com.example.practicafacturas.data.network.model.RepositoriesList
import com.example.practicafacturas.data.network.RetroServiceInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class InvoiceRepository @Inject constructor(

    private var retrofitService: APIRetrofitService,
    private var retromockService: APIRetromockService,
    private val invoiceDao: InvoiceDao
) {
    private lateinit var retroServiceInterface: RetroServiceInterface
    private var datos = "real"

    // Método para establecer el tipo de datos (real o ficticio) y decidir qué servicio usar
    fun setData(newDatos: String) {
        datos = newDatos
        decideService()
    }
    init {
        // Llamado al método decideService() en la inicialización de la clase
        decideService()
    }

    // Método para determinar qué servicio utilizar (real o ficticio)
    fun decideService() {
        if (datos == "ficticio") {
            retroServiceInterface = retromockService
        } else {
            retroServiceInterface = retrofitService
        }
    }

    // Método para obtener todas las facturas almacenadas en la BBDD local (room)
    fun getAllInvoicesFromRoom(): LiveData<List<Invoice>> {
        return invoiceDao.getAllInvoices()
    }

    // Método para insertar una factura en la BBDD local (room)
    fun insertRecord(invoice: Invoice) {
        invoiceDao.insertInvoices(invoice)
    }

    // Método para realizar una llamada a la API mediante retrofit y actualizar la BBDD local
    fun makeApiCall() {
        val call: Call<RepositoriesList> = retroServiceInterface.getDataFromAPI() // Llamada a la API con retrofit
        // Realizar la llamada de manera asíncrona
        call?.enqueue(object : Callback<RepositoriesList> {
            override fun onResponse(
                call: Call<RepositoriesList>,
                response: Response<RepositoriesList>
            ) {
                if (response.isSuccessful) {
                    // Borrar todos los registros existentes en la base de datos local
                    invoiceDao.deleteAllRecords()
                    // Iterar sobre la lista de facturas en la respuesta y agregarlas a la BBDD local
                    response.body()?.facturas?.forEach{
                        insertRecord(Invoice(descEstado = it.descEstado, importeOrdenacion = it.importeOrdenacion, fecha = it.fecha))
                    }
                }
            }

            override fun onFailure(call: Call<RepositoriesList>, t: Throwable) {
                // No hace nada
            }
        })
    }
}