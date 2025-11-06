package com.example.sistemauniversalacesso.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sistemauniversalacesso.R; // Verifique se o R está correto
import com.example.sistemauniversalacesso.databinding.FragmentQrScannerBinding; // Renomeie se necessário
import com.example.sistemauniversalacesso.models.Credencial; // Importe o modelo
import com.example.sistemauniversalacesso.models.LocalAcesso; // Importe o modelo
import com.example.sistemauniversalacesso.models.RegistroEntrada; // Importe o modelo
import com.google.firebase.auth.FirebaseAuth; // Se precisar do admin logado
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query; // Para buscar último acesso
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QrScannerFragment extends Fragment {

    private FragmentQrScannerBinding binding; // Use o nome correto do seu binding
    private DatabaseReference dbRef;
    private List<LocalAcesso> listaLocais;
    private ArrayAdapter<LocalAcesso> locaisAdapter;
    private String scannedUserId = null; // Armazena o userId lido do QR Code

    // --- Listeners e Callbacks (sem alterações) ---
    private final DecoratedBarcodeView.TorchListener torchListener = new DecoratedBarcodeView.TorchListener() {
        @Override public void onTorchOn() {}
        @Override public void onTorchOff() {}
    };

    private final com.journeyapps.barcodescanner.BarcodeCallback callback = new com.journeyapps.barcodescanner.BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                binding.qrScanner.pause(); // Pausa após leitura
                scannedUserId = result.getText().trim(); // Assume que o QR contém APENAS o userId
                binding.textViewStatus.setText("QR Code lido: " + scannedUserId + ". Selecione Entrada ou Saída.");
                binding.btnRegistrarEntrada.setEnabled(true);
                binding.btnRegistrarSaida.setEnabled(true);
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQrScannerBinding.inflate(inflater, container, false); // Use o nome correto
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbRef = FirebaseDatabase.getInstance().getReference();
        listaLocais = new ArrayList<>();

        // Configurar Adapter e Spinner
        locaisAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, listaLocais);
        locaisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerLocalAcesso.setAdapter(locaisAdapter);

        carregarLocais(); // Carrega os locais no Spinner

        // Configurar botões
        binding.btnRegistrarEntrada.setOnClickListener(v -> processarAcesso(true)); // true para entrada
        binding.btnRegistrarSaida.setOnClickListener(v -> processarAcesso(false)); // false para saída

        // Permissão da Câmera
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
        binding.qrScanner.resume(); // Garante que o scanner inicie
    }

    private void carregarLocais() {
        dbRef.child("locais_acesso").orderByChild("nome").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaLocais.clear();
                listaLocais.add(new LocalAcesso("Selecione um Local...")); // Placeholder inicial

                for (DataSnapshot ds : snapshot.getChildren()) {
                    LocalAcesso local = ds.getValue(LocalAcesso.class);
                    if (local != null && local.isAtivo()) {
                        local.setId(ds.getKey()); // Guarda o ID
                        listaLocais.add(local);
                    }
                }
                locaisAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Erro ao carregar locais.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processarAcesso(boolean isEntrada) {
        if (scannedUserId == null || scannedUserId.isEmpty()) {
            binding.textViewStatus.setText("Leia um QR Code primeiro.");
            return;
        }
        LocalAcesso localSelecionado = (LocalAcesso) binding.spinnerLocalAcesso.getSelectedItem();
        if (localSelecionado == null || localSelecionado.getId() == null) { // Verifica se um local válido foi selecionado
            binding.textViewStatus.setText("Selecione um local válido.");
            return;
        }

        String localId = localSelecionado.getId();
        binding.progressBarAcesso.setVisibility(View.VISIBLE);
        binding.btnRegistrarEntrada.setEnabled(false);
        binding.btnRegistrarSaida.setEnabled(false);

        if (isEntrada) {
            registrarEntrada(scannedUserId, localId);
        } else {
            registrarSaida(scannedUserId, localId);
        }
    }

    // --- LÓGICA DE REGISTRO DE ENTRADA ---
    private void registrarEntrada(String userId, String localId) {
        // 1. Verificar Credencial Válida
        dbRef.child("qrcodes").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot credencialSnapshot) {
                if (!credencialSnapshot.exists()) {
                    showError("QR Code inválido (usuário sem credencial)."); return;
                }
                Credencial credencial = credencialSnapshot.getValue(Credencial.class);
                long agoraSec = System.currentTimeMillis() / 1000;

                if (credencial == null || !credencial.is_active || credencial.expires_at <= agoraSec) {
                    showError("Credencial QR Code inválida ou expirada."); return;
                }
                // Verifica se a credencial é geral ou para o local específico
                if (!"geral".equals(credencial.local) && !localId.equals(credencial.local)) {
                    showError("Credencial não é válida para este local."); return;
                }

                // 2. Verificar se já está DENTRO em OUTRO local
                verificarStatusEmOutroLocal(userId, localId, () -> {
                    // 3. Verificar se já está DENTRO NESTE local
                    buscarUltimoRegistro(userId, localId, (ultimoRegistro, chaveUltimoRegistro) -> {
                        if (ultimoRegistro != null && "dentro".equals(ultimoRegistro.status)) {
                            showError("Usuário já está DENTRO neste local.");
                        } else {
                            // 4. Pode registrar a entrada
                            criarNovoRegistroEntrada(userId, localId);
                        }
                    });
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { showError("Erro ao verificar credencial."); }
        });
    }

    // --- LÓGICA DE REGISTRO DE SAÍDA ---
    private void registrarSaida(String userId, String localId) {
        // 1. Buscar último registro DENTRO neste local
        buscarUltimoRegistro(userId, localId, (ultimoRegistro, chaveUltimoRegistro) -> {
            if (ultimoRegistro == null || !"dentro".equals(ultimoRegistro.status)) {
                showError("Não há registro de entrada ativo para este usuário neste local.");
            } else {
                // 2. Atualizar o registro para SAÍDA
                atualizarRegistroParaSaida(chaveUltimoRegistro, ultimoRegistro);
            }
        });
    }

    // --- Funções Auxiliares Firebase ---

    private void verificarStatusEmOutroLocal(String userId, String localAtualId, Runnable onSuccess) {
        Query query = dbRef.child("entradas_qrcode").orderByChild("user_id").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean dentroEmOutroLocal = false;
                String nomeOutroLocal = "";
                for (DataSnapshot ds : snapshot.getChildren()) {
                    RegistroEntrada reg = ds.getValue(RegistroEntrada.class);
                    if (reg != null && "dentro".equals(reg.status) && !localAtualId.equals(reg.local)) {
                        dentroEmOutroLocal = true;
                        // Opcional: buscar nome do outro local (requer mapa de locais carregado)
                        // nomeOutroLocal = mapaNomesLocais.getOrDefault(reg.local, reg.local);
                        break;
                    }
                }
                if (dentroEmOutroLocal) {
                    showError("Usuário já está DENTRO em outro local (" + nomeOutroLocal + "). Saída necessária.");
                } else {
                    onSuccess.run(); // Continua para a próxima verificação
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { showError("Erro ao verificar status."); }
        });
    }

    private void buscarUltimoRegistro(String userId, String localId, BiConsumer<RegistroEntrada, String> onComplete) {
        Query query = dbRef.child("entradas_qrcode").orderByChild("user_id").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RegistroEntrada ultimoRegistro = null;
                String chaveUltimoRegistro = null;
                long maiorTimestamp = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    RegistroEntrada reg = ds.getValue(RegistroEntrada.class);
                    // Procura o registro mais recente PARA ESTE LOCAL
                    if (reg != null && localId.equals(reg.local) && reg.entrada > maiorTimestamp) {
                        maiorTimestamp = reg.entrada;
                        ultimoRegistro = reg;
                        chaveUltimoRegistro = ds.getKey();
                    }
                }
                onComplete.accept(ultimoRegistro, chaveUltimoRegistro);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                showError("Erro ao buscar último registro.");
                onComplete.accept(null, null);
            }
        });
    }

    private void criarNovoRegistroEntrada(String userId, String localId) {
        long timestampAgoraMs = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dataFormatada = sdf.format(new Date(timestampAgoraMs));

        // Buscar nome do usuário (opcional, mas recomendado)
        dbRef.child("users").child(userId).child("nome").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot nameSnapshot) {
                String nomeUsuario = nameSnapshot.exists() ? nameSnapshot.getValue(String.class) : "Nome não encontrado";

                RegistroEntrada novoRegistro = new RegistroEntrada(userId, nomeUsuario, localId, timestampAgoraMs, dataFormatada);
                String novaChave = dbRef.child("entradas_qrcode").push().getKey(); // Gera chave única

                if (novaChave != null) {
                    dbRef.child("entradas_qrcode").child(novaChave).setValue(novoRegistro)
                            .addOnSuccessListener(aVoid -> showSuccess("Entrada registrada com sucesso!"))
                            .addOnFailureListener(e -> showError("Falha ao registrar entrada: " + e.getMessage()));
                } else {
                    showError("Não foi possível gerar chave para o registro.");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                // Continua mesmo sem o nome, usando um placeholder
                RegistroEntrada novoRegistro = new RegistroEntrada(userId, "Erro ao buscar nome", localId, timestampAgoraMs, dataFormatada);
                String novaChave = dbRef.child("entradas_qrcode").push().getKey();
                if (novaChave != null) {
                    dbRef.child("entradas_qrcode").child(novaChave).setValue(novoRegistro)
                            .addOnSuccessListener(aVoid -> showSuccess("Entrada registrada com sucesso!"))
                            .addOnFailureListener(e -> showError("Falha ao registrar entrada: " + e.getMessage()));
                } else {
                    showError("Não foi possível gerar chave para o registro.");
                }
            }
        });
    }

    private void atualizarRegistroParaSaida(String chaveRegistro, RegistroEntrada registroEntrada) {
        long timestampAgoraMs = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dataFormatada = sdf.format(new Date(timestampAgoraMs));

        long permanenciaMs = timestampAgoraMs - registroEntrada.entrada;
        long permanenciaSegundos = TimeUnit.MILLISECONDS.toSeconds(permanenciaMs);
        String permanenciaFormatada = formatarTempo(permanenciaSegundos);

        Map<String, Object> updates = registroEntrada.toMapParaSaida(
                timestampAgoraMs, dataFormatada, permanenciaSegundos, permanenciaFormatada
        );

        dbRef.child("entradas_qrcode").child(chaveRegistro).updateChildren(updates)
                .addOnSuccessListener(aVoid -> showSuccess("Saída registrada com sucesso!"))
                .addOnFailureListener(e -> showError("Falha ao registrar saída: " + e.getMessage()));
    }

    // --- Funções de UI e Utilidades ---

    private void showError(String message) {
        if (binding == null) return; // Evita crash se o fragment for destruído
        binding.progressBarAcesso.setVisibility(View.GONE);
        binding.textViewStatus.setText("Erro: " + message);
        binding.btnRegistrarEntrada.setEnabled(scannedUserId != null); // Reabilita se QR foi lido
        binding.btnRegistrarSaida.setEnabled(scannedUserId != null);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        // Reinicia o scanner após um erro para permitir nova leitura
        resetScannerState();
    }

    private void showSuccess(String message) {
        if (binding == null) return;
        binding.progressBarAcesso.setVisibility(View.GONE);
        binding.textViewStatus.setText(message);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        // Reinicia o estado para permitir nova leitura/operação
        resetScannerState();
    }

    private void resetScannerState() {
        scannedUserId = null; // Limpa o ID lido
        if (binding != null) {
            binding.textViewStatus.setText("Aguardando leitura do QR Code...");
            binding.btnRegistrarEntrada.setEnabled(false); // Desabilita botões
            binding.btnRegistrarSaida.setEnabled(false);
            binding.qrScanner.resume(); // Reinicia o scanner
        }
    }

    private String formatarTempo(long totalSeconds) {
        long hours = TimeUnit.SECONDS.toHours(totalSeconds);
        long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) % 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Interface funcional para callback (Java não tem lambda de múltiplos params direto)
    @FunctionalInterface
    interface BiConsumer<T, U> { void accept(T t, U u); }

    // --- Ciclo de Vida do Fragment ---
    @Override
    public void onResume() {
        super.onResume();
        if (binding != null && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            binding.qrScanner.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding != null) binding.qrScanner.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Importante para evitar memory leaks
    }

    // Modelo LocalAcesso simplificado para o placeholder do Spinner
    public static class LocalAcesso {
        private String id;
        private String nome;
        private boolean ativo;
        public LocalAcesso(){} // Necessário para Firebase
        public LocalAcesso(String placeholder){ this.nome = placeholder; } // Construtor para placeholder
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public boolean isAtivo() { return ativo; }
        public void setAtivo(boolean ativo) { this.ativo = ativo; }
        @NonNull @Override public String toString() { return nome != null ? nome : "Local inválido"; } // Mostra o nome no Spinner
    }
}