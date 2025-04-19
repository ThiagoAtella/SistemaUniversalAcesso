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

import com.example.sistemauniversalacesso.databinding.FragmentConfigBinding;
import com.example.sistemauniversalacesso.telas.login_activity;
import com.example.sistemauniversalacesso.utils.SessionManager;

public class ConfigFragment extends Fragment {

    private FragmentConfigBinding binding;
    private SessionManager session;

    public ConfigFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentConfigBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        session = new SessionManager(requireContext());

        binding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirmação")
                    .setMessage("Deseja realmente sair da conta?")
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
