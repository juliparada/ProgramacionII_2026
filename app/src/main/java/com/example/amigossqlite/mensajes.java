package com.example.amigossqlite;

public class mensajes {
    String idMensaje;
    String de;
    String para;
    String mensaje;
    String fecha;

    public mensajes() {
    }

    public mensajes(String idMensaje, String de, String para, String mensaje, String fecha) {
        this.idMensaje = idMensaje;
        this.de = de;
        this.para = para;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public String getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(String idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
