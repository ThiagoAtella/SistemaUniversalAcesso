package com.example.sistemauniversalacesso.models;

public class EntradaRegistro {
    private String id;
    private String usuarioId;
    private String nomeUsuario;
    private String localId;
    private String nomeLocal;
    private String dataHora;
    private boolean entradaLiberada;

    public EntradaRegistro(String id, String usuarioId, String nomeUsuario, String localId, String nomeLocal, String dataHora, boolean entradaLiberada) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
        this.localId = localId;
        this.nomeLocal = nomeLocal;
        this.dataHora = dataHora;
        this.entradaLiberada = entradaLiberada;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getNomeLocal() {
        return nomeLocal;
    }

    public void setNomeLocal(String nomeLocal) {
        this.nomeLocal = nomeLocal;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public boolean isEntradaLiberada() {
        return entradaLiberada;
    }

    public void setEntradaLiberada(boolean entradaLiberada) {
        this.entradaLiberada = entradaLiberada;
    }
}
