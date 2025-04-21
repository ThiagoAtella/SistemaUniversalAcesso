package com.example.sistemauniversalacesso.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sistemauniversalacesso.database.SistemaDatabase;
import com.example.sistemauniversalacesso.databinding.DialogEditarUsuarioBinding;
import com.example.sistemauniversalacesso.databinding.FragmentUsuariosBinding;
import com.example.sistemauniversalacesso.models.Usuario;
import com.example.sistemauniversalacesso.utils.PasswordUtils;
import com.example.sistemauniversalacesso.adapters.UsuarioAdapter;

import java.util.Arrays;
import java.util.List;

public class UsuariosFragment extends Fragment {

    private FragmentUsuariosBinding binding;
    private SistemaDatabase db;
    private UsuarioAdapter adapter;

    public UsuariosFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsuariosBinding.inflate(inflater, container, false);
        db = SistemaDatabase.getInstance(requireContext());

        binding.recyclerUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        carregarUsuarios();

        binding.btnAdicionar.setOnClickListener(v -> mostrarDialogAdicionarUsuario());

        return binding.getRoot();
    }

    private void carregarUsuarios() {
        new Thread(() -> {
            Usuario[] usuariosArray = db.UsuarioDao().loadAllUsers();
            List<Usuario> usuarios = Arrays.asList(usuariosArray);

            requireActivity().runOnUiThread(() -> {
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

        new AlertDialog.Builder(requireContext())
                .setTitle("Adicionar Usuário")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String nome = dialogBinding.etNome.getText().toString();
                    String email = dialogBinding.etEmail.getText().toString();
                    String senha = dialogBinding.etSenha.getText().toString();

                    if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                        Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String senhaCriptografada = PasswordUtils.generateSecurePassword(senha);
                    Usuario novoUsuario = new Usuario(nome, email, senhaCriptografada);

                    new Thread(() -> {
                        db.UsuarioDao().inserir(novoUsuario);
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Usuário adicionado", Toast.LENGTH_SHORT).show();
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

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar Usuário")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Salvar", (dialog, which) -> {
                    usuario.setNome(dialogBinding.etNome.getText().toString());
                    usuario.setEmail(dialogBinding.etEmail.getText().toString());

                    String novaSenha = dialogBinding.etSenha.getText().toString();
                    if (!novaSenha.isEmpty()) {
                        usuario.setSenha(PasswordUtils.generateSecurePassword(novaSenha));
                    }

                    new Thread(() -> {
                        db.UsuarioDao().update(usuario);
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Usuário atualizado", Toast.LENGTH_SHORT).show();
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
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Usuário deletado", Toast.LENGTH_SHORT).show();
                carregarUsuarios();
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
