package com.example.sistemauniversalacesso.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sistemauniversalacesso.R;
import com.example.sistemauniversalacesso.databinding.FragmentQrBinding;
import com.example.sistemauniversalacesso.utils.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrFragment extends Fragment {

    private FragmentQrBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQrBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Usa o layout com o ImageView e o botão
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager session = new SessionManager(requireContext());

        binding.btnGerarQRCode.setOnClickListener(v -> {
            String conteudoQR = "Nome: " + session.getNome() + "\nEmail: " + session.getEmail() + "\nNível: " + session.getNivel();

            try {
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap qrBitmap = encoder.encodeBitmap(conteudoQR, BarcodeFormat.QR_CODE, 600, 600);
                binding.ivQRCode.setImageBitmap(qrBitmap);
                Toast.makeText(requireContext(), "QR Code gerado com sucesso!", Toast.LENGTH_SHORT).show();
            } catch (WriterException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
