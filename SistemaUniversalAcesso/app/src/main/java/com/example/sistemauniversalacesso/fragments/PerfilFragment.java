package com.example.sistemauniversalacesso.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sistemauniversalacesso.databinding.FragmentPerfilBinding;
import com.example.sistemauniversalacesso.ui.login_activity;
import com.example.sistemauniversalacesso.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager session = new SessionManager(requireContext());

        // Preenche os dados
        binding.tvNome.setText(session.getNome());
        binding.tvEmail.setText(session.getEmail());

        // Clique no botão de editar (em breve)
        binding.btnEditarPerfil.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Funcionalidade de edição em breve!", Toast.LENGTH_SHORT).show();
        });

        // Clique no botão de sair
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Faz logout no Firebase
                FirebaseAuth.getInstance().signOut();

                // Redireciona para a tela de login
                Intent intent = new Intent(requireActivity(), login_activity.class);
                startActivity(intent);

                // Finaliza a activity atual para evitar voltar com o botão de "voltar"
                requireActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
