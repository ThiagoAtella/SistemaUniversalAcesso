package com.example.sistemauniversalacesso.telas;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sistemauniversalacesso.utils.UsuarioAdapter;
import com.example.sistemauniversalacesso.database.SistemaDatabase;
import com.example.sistemauniversalacesso.databinding.ActivityMainBinding;
import com.example.sistemauniversalacesso.databinding.DialogEditarUsuarioBinding;
import com.example.sistemauniversalacesso.models.Usuario;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SistemaDatabase db;
    private UsuarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = SistemaDatabase.getInstance(this);
        binding.recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));

        carregarUsuarios();
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

    private void mostrarDialogEdicao(Usuario usuario) {
        DialogEditarUsuarioBinding dialogBinding = DialogEditarUsuarioBinding.inflate(getLayoutInflater());

        dialogBinding.etNome.setText(usuario.getNome());
        dialogBinding.etEmail.setText(usuario.getEmail());
        dialogBinding.etSenha.setText(usuario.getSenha());

        new AlertDialog.Builder(this)
                .setTitle("Editar Usuário")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Salvar", (dialog, which) -> {
                    usuario.setNome(dialogBinding.etNome.getText().toString());
                    usuario.setEmail(dialogBinding.etEmail.getText().toString());
                    usuario.setSenha(dialogBinding.etSenha.getText().toString());

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
