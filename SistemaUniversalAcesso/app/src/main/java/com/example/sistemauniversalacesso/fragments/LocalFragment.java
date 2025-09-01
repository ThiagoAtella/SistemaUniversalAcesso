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
import com.example.sistemauniversalacesso.adapters.LocalAdapter;
import com.example.sistemauniversalacesso.database.FirebaseService;
import com.example.sistemauniversalacesso.databinding.DialogEditarLocalBinding;
import com.example.sistemauniversalacesso.databinding.FragmentLocalBinding;
import com.example.sistemauniversalacesso.models.LocalAcesso;

import java.util.List;
import java.util.UUID;

public class LocalFragment extends Fragment {

    private FragmentLocalBinding binding;
    private LocalAdapter adapter;

    public LocalFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLocalBinding.inflate(inflater, container, false);
        binding.recyclerLocais.setLayoutManager(new LinearLayoutManager(requireContext()));
        carregarLocais();
        binding.btnAdicionar.setOnClickListener(v -> mostrarDialogAdicionarLocal());
        return binding.getRoot();
    }

    private void carregarLocais() {
        FirebaseService.listarLocais(new FirebaseService.FirebaseDataCallback<List<LocalAcesso>>() {
            @Override
            public void onComplete(List<LocalAcesso> locais) {
                adapter = new LocalAdapter(locais, new LocalAdapter.LocalCallback() {
                    @Override
                    public void onEditar(LocalAcesso local) {
                        mostrarDialogEdicao(local);
                    }

                    @Override
                    public void onDeletar(LocalAcesso local) {
                        confirmarDelecao(local);
                    }
                });
                binding.recyclerLocais.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Erro ao carregar locais: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogAdicionarLocal() {
        DialogEditarLocalBinding dialogBinding = DialogEditarLocalBinding.inflate(getLayoutInflater());
        new AlertDialog.Builder(requireContext())
                .setTitle("Adicionar Local")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String nome = dialogBinding.etNomeLocal.getText().toString().trim();
                    String endereco = dialogBinding.etEndereco.getText().toString().trim();
                    String tipo = dialogBinding.spTipo.getSelectedItem().toString();
                    String capacidadeStr = dialogBinding.etCapacidade.getText().toString();
                    boolean exigePagamento = dialogBinding.cbPagamento.isChecked();

                    if (nome.isEmpty() || endereco.isEmpty() || capacidadeStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int capacidade = Integer.parseInt(capacidadeStr);
                    // O ID será gerado pelo FirebaseService se for um novo local
                    LocalAcesso novoLocal = new LocalAcesso(null, nome, tipo, capacidade, endereco, exigePagamento);

                    FirebaseService.salvarLocal(novoLocal, (success, message) -> {
                        if (success) {
                            Toast.makeText(getContext(), "Local salvo com sucesso!", Toast.LENGTH_SHORT).show();
                            carregarLocais();
                        } else {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogEdicao(LocalAcesso local) {
        DialogEditarLocalBinding dialogBinding = DialogEditarLocalBinding.inflate(getLayoutInflater());

        dialogBinding.etNomeLocal.setText(local.getNome());
        dialogBinding.etEndereco.setText(local.getEndereco());
        dialogBinding.etCapacidade.setText(String.valueOf(local.getCapacidade()));
        dialogBinding.cbPagamento.setChecked(local.isExigePagamento());

        // Configura o spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.tipos_local, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spTipo.setAdapter(spinnerAdapter);
        for (int i = 0; i < spinnerAdapter.getCount(); i++) {
            if (spinnerAdapter.getItem(i).toString().equals(local.getTipo())) {
                dialogBinding.spTipo.setSelection(i);
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar Local")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Salvar", (dialog, which) -> {
                    local.setNome(dialogBinding.etNomeLocal.getText().toString().trim());
                    local.setEndereco(dialogBinding.etEndereco.getText().toString().trim());
                    local.setTipo(dialogBinding.spTipo.getSelectedItem().toString());
                    local.setCapacidade(Integer.parseInt(dialogBinding.etCapacidade.getText().toString()));
                    local.setExigePagamento(dialogBinding.cbPagamento.isChecked());

                    FirebaseService.atualizarLocal(local, (success, message) -> {
                        if (success) {
                            Toast.makeText(getContext(), "Local atualizado!", Toast.LENGTH_SHORT).show();
                            carregarLocais();
                        } else {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarDelecao(LocalAcesso local) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Deletar Local")
                .setMessage("Tem certeza que deseja deletar o local '" + local.getNome() + "'?")
                .setPositiveButton("Deletar", (dialog, which) -> deletarLocal(local))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deletarLocal(LocalAcesso local) {
        FirebaseService.excluirLocal(local.getId(), (success, message) -> {
            if (success) {
                Toast.makeText(getContext(), "Local deletado", Toast.LENGTH_SHORT).show();
                carregarLocais();
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