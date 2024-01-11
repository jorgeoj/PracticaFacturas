package com.example.practicafacturas.network

import androidx.lifecycle.LiveData
import androidx.room.Dao
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

    fun getAllInvoicesFromRoom(): LiveData<List<Invoice>> {
        return invoiceDao.getAllInvoices()
    }

    fun insertRecord(invoice: Invoice) {
        invoiceDao.insertInvoices(invoice)
    }

    //Retofit inserta
    fun makeApiCall() {
        val call: Call<RepositoriesList> = retroServiceInterface.getDataFromAPI()
        call?.enqueue(object : Callback<RepositoriesList> {
            override fun onResponse(
                call: Call<RepositoriesList>,
                response: Response<RepositoriesList>
            ) {
                if (response.isSuccessful) {
                    invoiceDao.deleteAllRecords()
                    response.body()?.facturas?.forEach{
                        insertRecord(Invoice(descEstado = it.descEstado, importeOrdenacion = it.importeOrdenacion, fecha = it.fecha))
                    }
                }
            }

            override fun onFailure(call: Call<RepositoriesList>, t: Throwable) {
                // No hacer nada en caso de fallo
            }
        })
    }
}