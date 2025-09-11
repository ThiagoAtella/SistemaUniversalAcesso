package com.example.sistemauniversalacesso.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sistemauniversalacesso.databinding.FragmentSolicitarCredencialBinding;
import com.example.sistemauniversalacesso.models.SolicitacaoCredencial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SolicitarCredencialFragment extends Fragment {

    private FragmentSolicitarCredencialBinding binding;
    private DatabaseReference dbRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSolicitarCredencialBinding.inflate(inflater, container, false);

        dbRef = FirebaseDatabase.getInstance().getReference("solicitacoes_credenciais");

        binding.btnEnviar.setOnClickListener(v -> enviarSolicitacao());

        return binding.getRoot();
    }

    private void enviarSolicitacao() {
        String justificativa = binding.etJustificativa.getText().toString().trim();
        String localAcesso = binding.etLocalAcesso.getText().toString().trim();
        String tipo = binding.etTipo.getText().toString().trim();

        if (justificativa.isEmpty() || localAcesso.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos!", Toast.LENGTH_LONG).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String username = FirebaseAuth.getInstance().getCurrentUser().getEmail(); // ou displayName se tiver

        String id = dbRef.push().getKey();

        SolicitacaoCredencial solicitacao = new SolicitacaoCredencial();
        solicitacao.setId(id);
        solicitacao.setUser_id(userId);
        solicitacao.setUsername(username);
        solicitacao.setJustificativa(justificativa);
        solicitacao.setLocal_acesso(localAcesso);
        solicitacao.setTipo(tipo);
        solicitacao.setStatus("pendente");
        solicitacao.setCredential_generated(false);
        solicitacao.setData_solicitacao(System.currentTimeMillis() / 1000);

        dbRef.child(id).setValue(solicitacao).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Solicitação enviada com sucesso!", Toast.LENGTH_LONG).show();
                binding.etJustificativa.setText("");
                binding.etLocalAcesso.setText("");
                binding.etTipo.setText("");
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
