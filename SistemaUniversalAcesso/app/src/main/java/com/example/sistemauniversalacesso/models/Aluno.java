package com.example.sistemauniversalacesso.models;

public class Aluno extends Usuario {
    private int matricula;  // Identificador específico do aluno
    private String turma;

    public Aluno(int id, String nome, String senha, int idade, String nivel, String email, int matricula, String turma) {
        super(id, nome, senha, idade, nivel, email);
        this.matricula = matricula;
        this.turma = turma;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }
}
