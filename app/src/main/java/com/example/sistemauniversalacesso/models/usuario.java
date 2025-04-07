package com.example.sistemauniversalacesso.models;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Usuarios")
public class usuario {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "nome")
    private String nome;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "senha")
    private String senha;

    public usuario(String nome, String email, String senha){
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
