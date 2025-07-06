package com.example.sistemauniversalacesso.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sistemauniversalacesso.databinding.FragmentQrScannerBinding;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONObject;

public class QrScannerFragment extends Fragment {

    private FragmentQrScannerBinding binding;

    private final DecoratedBarcodeView.TorchListener torchListener = new DecoratedBarcodeView.TorchListener() {
        @Override public void onTorchOn() {}
        @Override public void onTorchOff() {}
    };

    private final com.journeyapps.barcodescanner.BarcodeCallback callback = new com.journeyapps.barcodescanner.BarcodeCallback() {
        @Override
        public void barcodeResult(com.journeyapps.barcodescanner.BarcodeResult result) {
            binding.qrScanner.pause(); // Evita múltiplas leituras
            validarQRCode(result.getText());
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQrScannerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            iniciarScanner();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private final androidx.activity.result.ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) iniciarScanner();
                else Toast.makeText(requireContext(), "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
            });

    private void iniciarScanner() {
        binding.qrScanner.decodeContinuous(callback);
        binding.qrScanner.setTorchListener(torchListener);
        binding.qrScanner.resume();
    }

    private void validarQRCode(String qrCodeText) {
        try {
            JSONObject obj = new JSONObject(qrCodeText);
            String email = obj.getString("email");
            long expiraEm = obj.getLong("expira_em");

            long agora = System.currentTimeMillis() / 1000;

            if (agora > expiraEm) {
                Toast.makeText(requireContext(), "QR Code expirado ❌", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(requireContext(), "QR válido para: " + email, Toast.LENGTH_LONG).show();
                // Aqui você pode fazer uma consulta Firebase para validar esse usuário, se quiser
            }

        } catch (Exception e) {
            Toast.makeText(requireContext(), "QR inválido", Toast.LENGTH_SHORT).show();
            Log.e("QR", "Erro ao ler: " + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) binding.qrScanner.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding != null) binding.qrScanner.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
