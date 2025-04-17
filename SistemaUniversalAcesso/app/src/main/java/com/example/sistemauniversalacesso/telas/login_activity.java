package com.example.sistemauniversalacesso.telas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sistemauniversalacesso.database.SistemaDatabase;
import com.example.sistemauniversalacesso.databinding.LoginBinding;
import com.example.sistemauniversalacesso.models.Usuario;

public class login_activity extends AppCompatActivity {
    private LoginBinding binding;
    private SistemaDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = SistemaDatabase.getInstance(this);

        // Botão de login
        binding.btnLogin.setOnClickListener(v -> RealizarLogin());

        // CADASTRO -----
        binding.btnCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(login_activity.this, cadastro_activity.class);
            startActivity(intent);
        });
    }

    private void RealizarLogin() {
        String email = binding.etEmail.getText().toString();
        String senha = binding.etSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            Usuario usuario = db.UsuarioDao().login(email, senha);

            runOnUiThread(() -> {
                if (usuario != null) {
                    // Salvar login
                    SharedPreferences prefs = getSharedPreferences("BikeAppPrefs", MODE_PRIVATE);
                    prefs.edit().putBoolean("Logado", true).apply();

                    // Ir para MainActivity
                    Intent intent = new Intent(login_activity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}