package com.example.practicafacturas.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.practicafacturas.data.database.Invoice
import com.example.practicafacturas.data.repository.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(private val invoiceRepository: InvoiceRepository): ViewModel() {

    // Método que devuelve un LiveData que contiene la lista de facturas almacenadas localmente
    fun getAllRepositoryList(): LiveData<List<Invoice>> {
        return invoiceRepository.getAllInvoicesFromRoom()
    }

    // Método para realizar una llamada a la API y actualizar la BBDD local
    fun makeApiCall() {
        invoiceRepository.makeApiCall()
    }
}