package com.example.sistemauniversalacesso.fragments;

import android.graphics.Bitmap;
import android.content.Intent;
import android.app.AlertDialog;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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

        SessionManager session = new SessionManager(requireContext());

        binding.btnGerarQr.setOnClickListener(v -> {
            String conteudo = "Nome: " + session.getNome()
                    + "\nEmail: " + session.getEmail()
                    + "\nNível: " + session.getNivel();

            try {
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bitmap = encoder.encodeBitmap(conteudo, BarcodeFormat.QR_CODE, 500, 500);
                binding.imgQRCode.setImageBitmap(bitmap);
                binding.imgQRCode.setVisibility(View.VISIBLE);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        });

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
