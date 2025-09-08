package com.example.sistemauniversalacesso.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sistemauniversalacesso.databinding.FragmentConfigBinding;
import com.example.sistemauniversalacesso.ui.login_activity;
import com.google.firebase.auth.FirebaseAuth;

public class ConfigFragment extends Fragment {

    private FragmentConfigBinding binding;

    public ConfigFragment() {
        // Construtor vazio obrigatório
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentConfigBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Clique no botão de logout (sem diálogo)
        binding.btnLogout.setOnClickListener(v -> {
            // Faz logout no Firebase
            FirebaseAuth.getInstance().signOut();

            // Redireciona para a tela de login
            Intent intent = new Intent(requireActivity(), login_activity.class);
            startActivity(intent);

            // Finaliza a activity atual para evitar voltar com o botão de "voltar"
            requireActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
