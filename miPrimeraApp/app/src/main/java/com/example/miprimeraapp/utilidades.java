package com.example.miprimeraapp;

import android.util.Base64;

public class utilidades {
    static String url_consulta = "http://192.168.1.3:5984/steven/_design/yeimy/_view/yeimy";
    static String url_mto = "http://192.168.1.3:5984/steven";
    static String user = "yeimy";
    static String passwd = "*****";
    static String credencialesCodificadas = Base64.encodeToString((user + ":" + passwd).getBytes(), Base64.NO_WRAP);

    public String generarUnicoId() {
        return java.util.UUID.randomUUID().toString();
    }
}
