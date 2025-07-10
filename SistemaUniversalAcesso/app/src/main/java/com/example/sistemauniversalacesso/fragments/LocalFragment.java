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
        new Thread(() -> {
            List<LocalAcesso> locais = FirebaseService.listarLocais();

            // Atualiza a UI na thread principal
            requireActivity().runOnUiThread(() -> {
                // Inicializa o adapter com a lista de locais
                adapter = new LocalAdapter(locais, new LocalAdapter.LocalCallback() {
                    @Override
                    public void onEditar(LocalAcesso local) {
                        mostrarDialogEdicao(local);
                    }

                    @Override
                    public void onDeletar(LocalAcesso local) {
                        deletarLocal(local);
                    }
                });
                // Configura o RecyclerView com o adapter
                binding.recyclerLocais.setAdapter(adapter);
            });
        }).start();
    }


    private void mostrarDialogAdicionarLocal() {
        DialogEditarLocalBinding dialogBinding = DialogEditarLocalBinding.inflate(getLayoutInflater());
        new AlertDialog.Builder(requireContext())
                .setTitle("Adicionar Local")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String nome = dialogBinding.etNomeLocal.getText().toString();
                    String endereco = dialogBinding.etEndereco.getText().toString();
                    String tipo = dialogBinding.spTipo.getSelectedItem().toString();
                    String capacidadeStr = dialogBinding.etCapacidade.getText().toString();
                    boolean exigePagamento = dialogBinding.cbPagamento.isChecked();

                    if (nome.isEmpty() || endereco.isEmpty() || capacidadeStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int capacidade = Integer.parseInt(capacidadeStr);
                    LocalAcesso novoLocal = new LocalAcesso(UUID.randomUUID().toString(), nome, tipo, capacidade, endereco, exigePagamento);

                    new Thread(() -> {
                        FirebaseService.salvarLocal(novoLocal);
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Local cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                            carregarLocais();
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void mostrarDialogEdicao(LocalAcesso local) {
        DialogEditarLocalBinding dialogBinding = DialogEditarLocalBinding.inflate(getLayoutInflater());

        dialogBinding.etNomeLocal.setText(local.getNome());
        dialogBinding.etEndereco.setText(local.getEndereco());
        dialogBinding.etCapacidade.setText(String.valueOf(local.getCapacidade()));

        // Define o tipo de local selecionado
        String[] tipos = getResources().getStringArray(R.array.tipos_local);
        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].equals(local.getTipo())) {
                dialogBinding.spTipo.setSelection(i);
                break;
            }
        }

        dialogBinding.cbPagamento.setChecked(local.isExigePagamento());

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar Local")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Salvar", (dialog, which) -> {
                    local.setNome(dialogBinding.etNomeLocal.getText().toString());
                    local.setEndereco(dialogBinding.etEndereco.getText().toString());
                    local.setTipo(dialogBinding.spTipo.getSelectedItem().toString());
                    local.setCapacidade(Integer.parseInt(dialogBinding.etCapacidade.getText().toString()));
                    local.setExigePagamento(dialogBinding.cbPagamento.isChecked());

                    new Thread(() -> {
                        FirebaseService.atualizarLocal(local);
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Local atualizado", Toast.LENGTH_SHORT).show();
                            carregarLocais();
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deletarLocal(LocalAcesso local) {
        new Thread(() -> {
            FirebaseService.excluirLocal(local.getId());
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Local deletado", Toast.LENGTH_SHORT).show();
                carregarLocais();
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
