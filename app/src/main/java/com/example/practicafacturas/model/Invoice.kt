package com.example.practicafacturas.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "invoice_table")
class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descEstado: String?,
    val importeOrdenacion: Double?,
    val fecha: String?
) {

}