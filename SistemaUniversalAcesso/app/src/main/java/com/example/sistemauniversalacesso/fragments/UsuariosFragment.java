package com.example.sistemauniversalacesso.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UsuariosFragment extends Fragment {

    private FragmentUsuariosBinding binding;
    private UsuarioAdapter adapter;
    private FirebaseAuth mAuth;

    public UsuariosFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsuariosBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();

        setupRecyclerView();
        carregarUsuarios();

        binding.btnAdicionar.setOnClickListener(v -> mostrarDialogAdicionarUsuario());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void carregarUsuarios() {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Usuario> usuarios = new ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            Usuario usuario = userSnapshot.getValue(Usuario.class);
                            if (usuario != null) {
                                usuario.setUid(userSnapshot.getKey()); // pega o UID
                                usuarios.add(usuario);
                            }
                        }
                        adapter = new UsuarioAdapter(usuarios, new UsuarioAdapter.UsuarioCallback() {
                            @Override
                            public void onEditar(Usuario usuario) {
                                mostrarDialogEdicao(usuario);
                            }

                            @Override
                            public void onDeletar(Usuario usuario) {
                                confirmarDelecao(usuario);
                            }
                        });
                        binding.recyclerUsuarios.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Erro: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDialogAdicionarUsuario() {
        DialogEditarUsuarioBinding dialogBinding = DialogEditarUsuarioBinding.inflate(getLayoutInflater());

        // 👉 Agora a senha aparece para o admin preencher
        dialogBinding.etSenha.setVisibility(View.VISIBLE);

        new AlertDialog.Builder(requireContext())
                .setTitle("Adicionar Novo Usuário")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String nome = dialogBinding.etNome.getText().toString().trim();
                    String email = dialogBinding.etEmail.getText().toString().trim();
                    String senha = dialogBinding.etSenha.getText().toString();
                    String tipo = dialogBinding.spNivel.getSelectedItem().toString();

                    if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                        Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAuth.createUserWithEmailAndPassword(email, senha)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = task.getResult().getUser();
                                    String uid = firebaseUser.getUid();

                                    Usuario novoUsuario = new Usuario();
                                    novoUsuario.setNome(nome);
                                    novoUsuario.setEmail(email);
                                    novoUsuario.setTipo(tipo);
                                    novoUsuario.setAvatar("avatar1");
                                    novoUsuario.setCanEditUsers(false);
                                    novoUsuario.setMaster(false);

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    novoUsuario.setData_cadastro(sdf.format(new Date()));

                                    // 🚀 Salva no Realtime Database
                                    FirebaseDatabase.getInstance().getReference("users")
                                            .child(uid)
                                            .setValue(novoUsuario)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(getContext(), "Usuário criado com sucesso!", Toast.LENGTH_LONG).show();
                                                carregarUsuarios(); // Atualiza lista
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });

                                } else {
                                    Toast.makeText(getContext(), "Falha: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogEdicao(Usuario usuario) {
        DialogEditarUsuarioBinding dialogBinding = DialogEditarUsuarioBinding.inflate(getLayoutInflater());

        dialogBinding.etNome.setText(usuario.getNome());
        dialogBinding.etEmail.setText(usuario.getEmail());
        dialogBinding.etSenha.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.niveis_usuario, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spNivel.setAdapter(spinnerAdapter);
        for (int i = 0; i < spinnerAdapter.getCount(); i++) {
            if (spinnerAdapter.getItem(i).toString().equalsIgnoreCase(usuario.getTipo())) {
                dialogBinding.spNivel.setSelection(i);
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar Usuário")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Salvar", (dialog, which) -> {
                    usuario.setNome(dialogBinding.etNome.getText().toString().trim());
                    usuario.setEmail(dialogBinding.etEmail.getText().toString().trim());
                    usuario.setTipo(dialogBinding.spNivel.getSelectedItem().toString());

                    FirebaseService.atualizarUsuario(usuario.getUid(), usuario, (success, message) -> {
                        if (success) {
                            Toast.makeText(getContext(), "Usuário atualizado", Toast.LENGTH_SHORT).show();
                            carregarUsuarios();
                        } else {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarDelecao(Usuario usuario) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Deletar Usuário")
                .setMessage("Tem certeza que deseja deletar " + usuario.getNome() + "?\n\nAtenção: Isso remove apenas os dados do banco, não o login do usuário.")
                .setPositiveButton("Deletar", (dialog, which) -> deletarUsuario(usuario))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deletarUsuario(Usuario usuario) {
        FirebaseService.excluirDadosUsuario(usuario.getUid(), (success, message) -> {
            if (success) {
                Toast.makeText(getContext(), "Dados do usuário deletados", Toast.LENGTH_SHORT).show();
                carregarUsuarios();
            } else {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}