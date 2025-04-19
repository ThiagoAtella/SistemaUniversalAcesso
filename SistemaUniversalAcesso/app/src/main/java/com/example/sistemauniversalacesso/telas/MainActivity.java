package com.example.sistemauniversalacesso.telas;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sistemauniversalacesso.utils.PasswordUtils;
import com.example.sistemauniversalacesso.utils.UsuarioAdapter;
import com.example.sistemauniversalacesso.database.SistemaDatabase;
import com.example.sistemauniversalacesso.databinding.ActivityMainBinding;
import com.example.sistemauniversalacesso.databinding.DialogEditarUsuarioBinding;
import com.example.sistemauniversalacesso.models.Usuario;
import com.example.sistemauniversalacesso.utils.SessionManager;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SistemaDatabase db;
    private UsuarioAdapter adapter;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = SistemaDatabase.getInstance(this);
        session = new SessionManager(this);

        // Proteção extra: se não estiver logado, volta pro login
        if (!session.isLogado()) {
            startActivity(new Intent(this, login_activity.class));
            finish();
            return;
        }

        // Boas-vindas
        String nomeUsuario = session.getNome();
        Toast.makeText(this, "Bem-vindo(a), " + nomeUsuario, Toast.LENGTH_SHORT).show();

        // Setup da RecyclerView
        binding.recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));
        carregarUsuarios();

        // Botão adicionar
        binding.btnAdicionar.setOnClickListener(v -> mostrarDialogAdicionarUsuario());

        // Botão logout
        binding.btnLogout.setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(MainActivity.this, login_activity.class));
            finish();
        });
    }

    private void carregarUsuarios() {
        new Thread(() -> {
            Usuario[] usuariosArray = db.UsuarioDao().loadAllUsers();
            List<Usuario> usuarios = Arrays.asList(usuariosArray);

            runOnUiThread(() -> {
                adapter = new UsuarioAdapter(usuarios, new UsuarioAdapter.UsuarioCallback() {
                    @Override
                    public void onEditar(Usuario usuario) {
                        mostrarDialogEdicao(usuario);
                    }

                    @Override
                    public void onDeletar(Usuario usuario) {
                        deletarUsuario(usuario);
                    }
                });
                binding.recyclerUsuarios.setAdapter(adapter);
            });
        }).start();
    }

    private void mostrarDialogAdicionarUsuario() {
        DialogEditarUsuarioBinding dialogBinding = DialogEditarUsuarioBinding.inflate(getLayoutInflater());

        new AlertDialog.Builder(this)
                .setTitle("Adicionar Usuário")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String nome = dialogBinding.etNome.getText().toString();
                    String email = dialogBinding.etEmail.getText().toString();
                    String senha = dialogBinding.etSenha.getText().toString();

                    if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                        Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String senhaCriptografada = PasswordUtils.generateSecurePassword(senha);
                    Usuario novoUsuario = new Usuario(nome, email, senhaCriptografada);


                    new Thread(() -> {
                        db.UsuarioDao().inserir(novoUsuario);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Usuário adicionado com sucesso", Toast.LENGTH_SHORT).show();
                            carregarUsuarios();
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogEdicao(Usuario usuario) {
        DialogEditarUsuarioBinding dialogBinding = DialogEditarUsuarioBinding.inflate(getLayoutInflater());

        dialogBinding.etNome.setText(usuario.getNome());
        dialogBinding.etEmail.setText(usuario.getEmail());

        new AlertDialog.Builder(this)
                .setTitle("Editar Usuário")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Salvar", (dialog, which) -> {
                    usuario.setNome(dialogBinding.etNome.getText().toString());
                    usuario.setEmail(dialogBinding.etEmail.getText().toString());

                    String novaSenha = dialogBinding.etSenha.getText().toString();
                    if (!novaSenha.isEmpty()) {
                        String senhaCriptografada = PasswordUtils.generateSecurePassword(novaSenha);
                        usuario.setSenha(senhaCriptografada);
                    }

                    new Thread(() -> {
                        db.UsuarioDao().update(usuario);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Usuário atualizado", Toast.LENGTH_SHORT).show();
                            carregarUsuarios();
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deletarUsuario(Usuario usuario) {
        new Thread(() -> {
            db.UsuarioDao().delete(usuario);
            runOnUiThread(() -> {
                Toast.makeText(this, "Usuário deletado", Toast.LENGTH_SHORT).show();
                carregarUsuarios();
            });
        }).start();
    }
}
