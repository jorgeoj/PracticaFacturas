package com.example.practicafacturas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.practicafacturas.model.Invoice
import com.example.practicafacturas.network.InvoiceRepository
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