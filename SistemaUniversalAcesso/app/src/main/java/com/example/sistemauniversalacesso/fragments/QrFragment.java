package com.example.sistemauniversalacesso.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sistemauniversalacesso.databinding.FragmentQrBinding;
import com.example.sistemauniversalacesso.utils.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

public class QrFragment extends Fragment {

    private FragmentQrBinding binding;
    private CountDownTimer timer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQrBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnGerarQRCode.setOnClickListener(v -> gerarQRCodeComValidade());
    }

    private void gerarQRCodeComValidade() {
        SessionManager session = new SessionManager(requireContext());
        String email = session.getEmail();
        long agora = System.currentTimeMillis() / 1000; // segundos
        long expira = agora + (1 * 60);

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("expira_em", expira);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String conteudoQR = json.toString();

        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(conteudoQR, BarcodeFormat.QR_CODE, 400, 400);
            binding.ivQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        iniciarContagemRegressiva(expira - agora);
    }

    private void iniciarContagemRegressiva(long segundos) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(segundos * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                binding.tvValidade.setText("QR expira em: " + millisUntilFinished / 1000 + " segundos");
            }

            public void onFinish() {
                binding.tvValidade.setText("QR expirado ❌");
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        if (timer != null) timer.cancel();
        super.onDestroyView();
        binding = null;
    }
}
