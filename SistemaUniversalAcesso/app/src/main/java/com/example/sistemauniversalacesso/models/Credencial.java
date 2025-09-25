package com.example.sistemauniversalacesso.models;

public class Credencial {
    // Campos do Firebase
    public String token;
    public long expires_at;
    public String local;
    public boolean is_active;

    // Campo extra para uso no app
    private String tipo; // "QR Code" ou "NFC"

    public Credencial() {}

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}