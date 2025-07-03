package com.example.sistemauniversalacesso.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import com.example.sistemauniversalacesso.models.Usuario;

@Dao
public interface usuarioDAO {
    @Query("SELECT *  FROM usuarios")
    Usuario[] loadAllUsers();

    @Query("SELECT * FROM usuarios WHERE id IN (:userIds)")
    List<Usuario> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM usuarios WHERE email = :email AND senha = :senha LIMIT 1")
    Usuario login(String email, String senha);
    @Query("SELECT * FROM usuarios WHERE nome = :nome ")
    List<Usuario> loadAllApelido(String nome);


    @Query("SELECT * FROM usuarios WHERE email = :email")
    List<Usuario> loadALLEmail(String email);

    @Query("SELECT * FROM usuarios WHERE senha = :senha")
    List<Usuario> loadALLSenha(String senha);

    @Query("SELECT COUNT(*) FROM usuarios WHERE email = :email")
    int checkEmailExists(String email); // Verifica se já existe algum usuário com o email fornecido,
    // retornando a contagem (0 se não existir, número maior que 0 se existir).
    @Update
    void update(Usuario Usuario);

    @Insert
    void inserir(Usuario Usuario);
    @Delete
    void delete(Usuario Usuario);
}
