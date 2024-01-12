package com.example.practicafacturas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.practicafacturas.model.Invoice
import com.example.practicafacturas.network.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(private val invoiceRepository: InvoiceRepository): ViewModel() {
    fun getAllRepositoryList(): LiveData<List<Invoice>> {
        return invoiceRepository.getAllInvoicesFromRoom()
    }

    fun makeApiCall() {
        invoiceRepository.makeApiCall()
    }
}