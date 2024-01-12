package com.example.practicafacturas.network

import android.view.View
import androidx.lifecycle.LiveData
import androidx.room.Dao
import com.example.practicafacturas.R
import com.example.practicafacturas.database.InvoiceDao
import com.example.practicafacturas.model.Invoice
import com.example.practicafacturas.model.RepositoriesList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class InvoiceRepository @Inject constructor(
    private val retroServiceInterface: RetroServiceInterface,
    private val invoiceDao: InvoiceDao) {

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