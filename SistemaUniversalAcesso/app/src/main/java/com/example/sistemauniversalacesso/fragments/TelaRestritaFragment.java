package com.example.sistemauniversalacesso.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sistemauniversalacesso.databinding.FragmentTelaRestritaBinding;
import com.example.sistemauniversalacesso.ui.login_activity;
import com.example.sistemauniversalacesso.utils.SessionManager;

public class TelaRestritaFragment extends Fragment {

    private FragmentTelaRestritaBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTelaRestritaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirmação")
                    .setMessage("Deseja realmente sair da conta?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        SessionManager session = new SessionManager(requireContext());
                        session.logout();

                        startActivity(new Intent(requireContext(), login_activity.class));
                        requireActivity().finish(); // 🔒 impede voltar com botão
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

