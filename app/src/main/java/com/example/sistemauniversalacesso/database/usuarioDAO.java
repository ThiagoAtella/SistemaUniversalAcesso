package com.example.sistemauniversalacesso.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import com.example.sistemauniversalacesso.models.usuario;

@Dao
public interface usuarioDAO {
    @Query("SELECT *  FROM Usuarios")
    usuario[] loadAllUsers();

    @Query("SELECT * FROM Usuarios WHERE id IN (:userIds)")
    List<usuario> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM Usuarios WHERE email = :email AND senha = :senha LIMIT 1")
    usuario login(String email, String senha);
    @Query("SELECT * FROM Usuarios WHERE nome = :nome ")
    List<usuario> loadAllApelido(String nome);


    @Query("SELECT * FROM Usuarios WHERE email = :email")
    List<usuario> loadALLEmail(String email);

    @Query("SELECT * FROM Usuarios WHERE senha = :senha")
    List<usuario> loadALLSenha(String senha);


    @Update
    void update(usuario Usuario);

    @Insert
    void inserir(usuario Usuario);
    @Delete
    void delete(usuario Usuario);
}
