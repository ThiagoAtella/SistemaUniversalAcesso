package com.example.sistemauniversalacesso.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sistemauniversalacesso.database.FirebaseService;
import com.example.sistemauniversalacesso.databinding.ActivityCadastroLocalBinding;
import com.example.sistemauniversalacesso.models.LocalAcesso;

import java.util.UUID;

public class CadastroLocalActivity extends AppCompatActivity {

    private ActivityCadastroLocalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroLocalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_local, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spTipo.setAdapter(adapter);

        binding.btnSalvar.setOnClickListener(v -> salvarLocal());
    }

    private void salvarLocal() {
        String nome = binding.etNomeLocal.getText().toString().trim();
        String endereco = binding.etEndereco.getText().toString().trim();
        String tipo = binding.spTipo.getSelectedItem().toString();
        String capacidadeStr = binding.etCapacidade.getText().toString().trim();
        boolean exigePagamento = binding.cbPagamento.isChecked();

        if (nome.isEmpty() || endereco.isEmpty() || capacidadeStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacidade = Integer.parseInt(capacidadeStr);
        String id = UUID.randomUUID().toString();

        LocalAcesso local = new LocalAcesso(id, nome, tipo, capacidade, endereco, exigePagamento);

        new Thread(() -> {
            FirebaseService.salvarLocal(local);
            runOnUiThread(() -> {
                Toast.makeText(this, "Local cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
