package com.example.sistemauniversalacesso.models;

import androidx.room.Entity;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@IgnoreExtraProperties
@Entity
public class Usuario {

    private String uid;
    private String nome;
    private String email;
    private String avatar;
    private String tipo;
    private boolean can_edit_users;
    private boolean is_master;
    private String data_cadastro;

    public Usuario() {}

    @PropertyName("uid")
    public String getUid() { return uid; }
    @PropertyName("uid")
    public void setUid(String uid) { this.uid = uid; }

    @PropertyName("nome")
    public String getNome() { return nome; }
    @PropertyName("nome")
    public void setNome(String nome) { this.nome = nome; }

    @PropertyName("email")
    public String getEmail() { return email; }
    @PropertyName("email")
    public void setEmail(String email) { this.email = email; }

    @PropertyName("avatar")
    public String getAvatar() { return avatar; }
    @PropertyName("avatar")
    public void setAvatar(String avatar) { this.avatar = avatar; }

    @PropertyName("tipo")
    public String getTipo() { return tipo; }
    @PropertyName("tipo")
    public void setTipo(String tipo) { this.tipo = tipo; }

    @PropertyName("can_edit_users")
    public boolean isCanEditUsers() { return can_edit_users; }
    @PropertyName("can_edit_users")
    public void setCanEditUsers(boolean can_edit_users) { this.can_edit_users = can_edit_users; }

    @PropertyName("is_master")
    public boolean isMaster() { return is_master; }
    @PropertyName("is_master")
    public void setMaster(boolean is_master) { this.is_master = is_master; }

    @PropertyName("data_cadastro")
    public String getData_Cadastro() { return data_cadastro; }
    @PropertyName("data_cadastro")
    public void setData_cadastro(String data_cadastro) { this.data_cadastro = data_cadastro; }
}
