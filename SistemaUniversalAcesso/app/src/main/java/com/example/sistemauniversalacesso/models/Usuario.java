package com.example.sistemauniversalacesso.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

@Entity(tableName = "usuarios")
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    @Exclude // Impede que este campo seja enviado para o Firebase
    private int id;

    @Exclude // O ID do documento do Firebase é a chave, não um campo dentro dele
    private String uid; // Para armazenar o ID do documento do Firebase (ex: "0rmonzzecKNx...")

    // --- Campos que correspondem ao Firebase ---

    @ColumnInfo(name = "avatar")
    private String avatar;

    @PropertyName("can_edit_users") // Mapeia o campo "can_edit_users" do Firebase
    @ColumnInfo(name = "can_edit_users")
    private boolean canEditUsers;

    @PropertyName("data_cadastro") // Mapeia o campo "data_cadastro" do Firebase
    @ColumnInfo(name = "data_cadastro")
    private String dataCadastro;

    @ColumnInfo(name = "email")
    private String email;

    @PropertyName("is_master") // Mapeia o campo "is_master" do Firebase
    @ColumnInfo(name = "is_master")
    private boolean isMaster;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "tipo")
    private String tipo; // Campo "nivel" foi renomeado para "tipo"

    // Construtor vazio (essencial para o Firebase)
    public Usuario() {}

    // --- Getters e Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    @PropertyName("can_edit_users")
    public boolean isCanEditUsers() { return canEditUsers; }
    @PropertyName("can_edit_users")
    public void setCanEditUsers(boolean canEditUsers) { this.canEditUsers = canEditUsers; }

    @PropertyName("data_cadastro")
    public String getDataCadastro() { return dataCadastro; }
    @PropertyName("data_cadastro")
    public void setDataCadastro(String dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @PropertyName("is_master")
    public boolean isMaster() { return isMaster; }
    @PropertyName("is_master")
    public void setMaster(boolean master) { isMaster = master; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}