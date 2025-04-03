package com.example.sistemauniversalacesso.models;

public abstract class Usuario {
    private int id;         // PK genérica no banco
    private String nome;
    private String senha;
    private int idade;
    private String nivel;   // Ex.: "Aluno", "Funcionário", "Admin"
    private String email;

    // Construtor
    public Usuario(int id, String nome, String senha, int idade, String nivel, String email) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.idade = idade;
        this.nivel = nivel;
        this.email = email;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
