package com.example.sistemauniversalacesso.models;

public class LocalAcesso {
    private String id;
    private String nome;
    private String descricao;
    private String tipo;
    private boolean ativo;

    public LocalAcesso() { }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public boolean isAtivo() { return ativo; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    @Override
    public String toString() {
        return nome; // vai mostrar o nome no Spinner
    }

}
