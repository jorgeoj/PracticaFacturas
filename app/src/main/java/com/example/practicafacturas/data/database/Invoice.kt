package com.example.practicafacturas.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Define una entidad (modelo) que será inyectada en la BBDD con room
@Entity(tableName = "invoice_table")
class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descEstado: String?,
    val importeOrdenacion: Double?,
    val fecha: String?
) {}