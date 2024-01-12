package com.example.practicafacturas.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.practicafacturas.model.Invoice

@Database(entities = [Invoice::class], version = 1, exportSchema = false)
abstract class InvoiceDatabase: RoomDatabase() {
    // Método abstracto para obtener un DAO relacionado con Invoice (InvoiceDao)
    abstract fun getAppDao(): InvoiceDao

    //Creamos una instancia única de la BBDD
    companion object{
        private var DB_INSTANCE: InvoiceDatabase? = null // Almacena la instancia única de la BBDD

        // Método para obtener la instancia de la BBDD
        fun getAppDBInstance(context: Context): InvoiceDatabase {
            if (DB_INSTANCE == null) {
                DB_INSTANCE = Room.databaseBuilder(context.applicationContext, InvoiceDatabase::class.java, "invoice_database")
                    .allowMainThreadQueries()
                    .build()
            }
            return DB_INSTANCE!!
        }
    }
}