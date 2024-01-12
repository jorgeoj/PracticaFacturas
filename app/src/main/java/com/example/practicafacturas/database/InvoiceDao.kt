package com.example.practicafacturas.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.practicafacturas.model.Invoice

// Clase que proporciona metodos para interactuar con la BBDD
@Dao
interface InvoiceDao {
    // Método para obtener todas las facturas de la tabla
    @Query("SELECT * FROM invoice_table")
    fun getAllInvoices(): LiveData<List<Invoice>> // LiveData para notificar cambios automáticamente

    // Método para insertar una factura en la tabla
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInvoices(invoice: Invoice) // En caso de conflicto se usa REPLACE (se reemplaza la fila ya existente)

    // Método para eliminar todas las filas de la tabla
    @Query("DELETE FROM invoice_table")
    fun deleteAllRecords()
}