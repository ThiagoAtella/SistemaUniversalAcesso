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

import com.example.sistemauniversalacesso.R;
import com.example.sistemauniversalacesso.adapters.UsuarioAdapter;
import com.example.sistemauniversalacesso.database.FirebaseService;
import com.example.sistemauniversalacesso.databinding.DialogEditarUsuarioBinding;
import com.example.sistemauniversalacesso.databinding.FragmentUsuariosBinding;
import com.example.sistemauniversalacesso.models.Usuario;
import com.example.sistemauniversalacesso.utils.PasswordUtils;

import java.util.List;

public class UsuariosFragment extends Fragment {

    private FragmentUsuariosBinding binding;
    private UsuarioAdapter adapter;

    public UsuariosFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsuariosBinding.inflate(inflater, container, false);
        binding.recyclerUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        carregarUsuarios();

        binding.btnAdicionar.setOnClickListener(v -> mostrarDialogAdicionarUsuario());

        return binding.getRoot();
    }

    private void carregarUsuarios() {
        new Thread(() -> {
            List<Usuario> usuarios = FirebaseService.listarUsuarios();
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
                    String nivel = dialogBinding.spNivel.getSelectedItem().toString();

                    if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                        Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String senhaCriptografada = PasswordUtils.generateSecurePassword(senha);
                    Usuario novoUsuario = new Usuario(nome, email, senhaCriptografada, nivel);

                    new Thread(() -> {
                        FirebaseService.inserirUsuario(novoUsuario);
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Usuário adicionado com sucesso", Toast.LENGTH_SHORT).show();
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

        String[] niveis = getResources().getStringArray(R.array.niveis_usuario);
        for (int i = 0; i < niveis.length; i++) {
            if (niveis[i].equals(usuario.getNivel())) {
                dialogBinding.spNivel.setSelection(i);
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar Usuário")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Salvar", (dialog, which) -> {
                    usuario.setNome(dialogBinding.etNome.getText().toString());
                    usuario.setEmail(dialogBinding.etEmail.getText().toString());
                    usuario.setNivel(dialogBinding.spNivel.getSelectedItem().toString());

                    String novaSenha = dialogBinding.etSenha.getText().toString();
                    if (!novaSenha.isEmpty()) {
                        String senhaCriptografada = PasswordUtils.generateSecurePassword(novaSenha);
                        usuario.setSenha(senhaCriptografada);
                    }

                    new Thread(() -> {
                        FirebaseService.atualizarUsuario(usuario.getFirebaseId(), usuario);
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
            FirebaseService.excluirUsuario(usuario.getFirebaseId());
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
