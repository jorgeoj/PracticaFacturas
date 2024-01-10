package com.example.practicafacturas.adapter

import com.example.practicafacturas.Bill

class BillProvider {
    companion object{
        val lista = mutableListOf<Bill>(
            Bill("Pendiente de pago", 1.56, "07/02/2019"),
            Bill("Pagada", 25.14, "05/02/2019"),
            Bill("Pagada", 22.69, "08/01/2018"),
            Bill("Pendiente de pago", 12.84, "07/12/2018"),
            Bill("Pagada", 35.16, "16/11/2018"),
            Bill("Pagada", 18.27, "05/10/2018"),
            Bill("Pendiente de pago", 61.17, "05/09/2018"),
            Bill("Pagada", 37.18, "07/08/2018")
        )
    }
}