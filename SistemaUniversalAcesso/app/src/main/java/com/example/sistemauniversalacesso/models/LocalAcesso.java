package com.example.sistemauniversalacesso.models;

public class LocalAcesso {
    private String id;
    private String nome;
    private String tipo;
    private int capacidade;
    private String endereco;
    private boolean exigePagamento;

    public LocalAcesso() {}

    public LocalAcesso(String id, String nome, String tipo, int capacidade, String endereco, boolean exigePagamento) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.capacidade = capacidade;
        this.endereco = endereco;
        this.exigePagamento = exigePagamento;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public boolean isExigePagamento() { return exigePagamento; }
    public void setExigePagamento(boolean exigePagamento) { this.exigePagamento = exigePagamento; }
}
