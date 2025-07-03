package com.example.sistemauniversalacesso.fragments;

import android.app.AlertDialog;
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
        binding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Sair da conta")
                    .setMessage("Deseja realmente sair?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        session.logout();
                        startActivity(new Intent(requireContext(), login_activity.class));
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
