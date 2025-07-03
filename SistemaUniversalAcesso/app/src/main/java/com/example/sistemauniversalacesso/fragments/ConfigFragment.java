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
import com.example.sistemauniversalacesso.ui.login_activity;
import com.example.sistemauniversalacesso.utils.SessionManager;

public class ConfigFragment extends Fragment {

    // ViewBinding para acessar os componentes da UI do fragmento
    private FragmentConfigBinding binding;

    // Gerenciador de sessão (login/logout)
    private SessionManager session;

    public ConfigFragment() {
        // Construtor vazio obrigatório para fragments
    }

    /**
     * Infla o layout do fragment usando o ViewBinding
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inicializa o binding com o layout XML
        binding = FragmentConfigBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Retorna a raiz da view para o sistema
    }

    /**
     * Chamada logo após onCreateView, quando a View já foi criada
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa a sessão
        session = new SessionManager(requireContext());

        // Configura o clique do botão de logout
        binding.btnLogout.setOnClickListener(v -> {
            // Exibe um diálogo de confirmação
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirmação")
                    .setMessage("Deseja realmente sair da conta?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        // Faz o logout limpando a sessão
                        session.logout();

                        // Redireciona para a tela de login
                        startActivity(new Intent(requireContext(), login_activity.class));

                        // Encerra a activity atual para evitar voltar com o botão "Voltar"
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancelar", null) // Não faz nada se o usuário cancelar
                    .show();
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
