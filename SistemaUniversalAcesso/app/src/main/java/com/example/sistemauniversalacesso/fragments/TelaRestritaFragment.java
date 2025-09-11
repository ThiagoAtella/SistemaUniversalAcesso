package com.example.sistemauniversalacesso.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sistemauniversalacesso.R;
import com.example.sistemauniversalacesso.databinding.FragmentTelaRestritaBinding;

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

        // Inicializa com Perfil
        getChildFragmentManager().beginTransaction()
                .replace(binding.fragmentUserContainer.getId(), new PerfilFragment())
                .commit();

        binding.bottomNavUser.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_perfil) {
                selectedFragment = new PerfilFragment();
            }
            else if (item.getItemId() == R.id.nav_solicitacao){
                selectedFragment = new SolicitarCredencialFragment();
            }
            else if (item.getItemId() == R.id.nav_qrcode) {
                selectedFragment = new QrFragment();
            }

            if (selectedFragment != null) {
                getChildFragmentManager().beginTransaction()
                        .replace(binding.fragmentUserContainer.getId(), selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
