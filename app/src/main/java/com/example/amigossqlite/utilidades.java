package com.example.amigossqlite;

import android.util.Base64;

public class utilidades {
    // Esta es la URL para ver los datos de la vista (API de CouchDB)
    static String url_consulta = "http://192.168.1.10:5984/amigos/_design/yeimy/_view/yeimy";

    // Esta es la URL que usará la App para Insertar, Modificar y Eliminar (API de CouchDB)
    static String url_mto = "http://192.168.1.10:5984/amigos";
    static String user = "yeimy";
    static String passwd = "liSSa_1906";
    
    // Usamos android.util.Base64 para compatibilidad con Android
    static String credencialesCodificadas = Base64.encodeToString((user + ":" + passwd).getBytes(), Base64.NO_WRAP);

    public String generarUnicoId(){
        return java.util.UUID.randomUUID().toString();
    }
}
