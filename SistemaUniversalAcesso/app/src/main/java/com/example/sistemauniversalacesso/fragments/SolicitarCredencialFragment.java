package com.example.sistemauniversalacesso.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.sistemauniversalacesso.databinding.FragmentSolicitarCredencialBinding;
import com.example.sistemauniversalacesso.models.SolicitacaoCredencial;
import com.example.sistemauniversalacesso.models.LocalAcesso;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SolicitarCredencialFragment extends Fragment {

    private FragmentSolicitarCredencialBinding binding;
    private DatabaseReference dbRef;

    private List<LocalAcesso> listaLocais = new ArrayList<>();
    private ArrayAdapter<LocalAcesso> locaisAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSolicitarCredencialBinding.inflate(inflater, container, false);

        dbRef = FirebaseDatabase.getInstance().getReference();

        // Adapter para o Spinner
        locaisAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, listaLocais);
        locaisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spLocal.setAdapter(locaisAdapter);

        carregarLocais();

        binding.btnEnviar.setOnClickListener(v -> enviarSolicitacao());

        return binding.getRoot();
    }

    private void carregarLocais() {
        dbRef.child("locais_acesso").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaLocais.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    LocalAcesso local = ds.getValue(LocalAcesso.class);
                    if (local != null && local.isAtivo()) {
                        String idDoLocal = ds.getKey(); // <-- Pega o ID único do Firebase
                        local.setId(idDoLocal);         // <-- Armazena o ID no objeto
                        listaLocais.add(local);
                    }
                }
                locaisAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                        "Erro ao carregar locais: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void enviarSolicitacao() {
        String justificativa = binding.etJustificativa.getText().toString().trim();
        LocalAcesso localSelecionado = (LocalAcesso) binding.spLocal.getSelectedItem();
        String tipo = binding.spTipo.getSelectedItem() != null ? binding.spTipo.getSelectedItem().toString() : "";

        if (justificativa.isEmpty() || localSelecionado == null || tipo.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos!", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Usuário não autenticado!", Toast.LENGTH_LONG).show();
            return;
        }

        String id = dbRef.child("solicitacoes_credenciais").push().getKey();

        SolicitacaoCredencial solicitacao = new SolicitacaoCredencial();
        solicitacao.setId(id);
        solicitacao.setUser_id(user.getUid());
        solicitacao.setUsername(user.getEmail());
        solicitacao.setJustificativa(justificativa);
        solicitacao.setLocal_acesso(localSelecionado.getId()); // Pega o ID do local
        solicitacao.setTipo(tipo);
        solicitacao.setStatus("pendente");
        solicitacao.setCredential_generated(false);
        solicitacao.setValidade_dias(1);
        solicitacao.setData_solicitacao(System.currentTimeMillis());

        dbRef.child("solicitacoes_credenciais").child(id).setValue(solicitacao).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Solicitação enviada com sucesso!", Toast.LENGTH_LONG).show();
                binding.etJustificativa.setText("");
                binding.spLocal.setSelection(0);
                binding.spTipo.setSelection(0);
            } else {
                Toast.makeText(getContext(), "Erro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

