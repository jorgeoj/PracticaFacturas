package com.example.practicafacturas

import co.infinum.retromock.BodyFactory
import java.io.IOException
import java.io.InputStream

internal class ResourceBodyFactory : BodyFactory {
    // Este método crea un InputStream a partir de un recurso identificado por su nombre
    @Throws(IOException::class)
    override fun create(input: String): InputStream? {
        // Utiliza el cargador de clases de la clase ResourceBodyFactory para obtener un InputStream del recurso
        return ResourceBodyFactory::class.java.classLoader?.getResourceAsStream(input)
    }
}
