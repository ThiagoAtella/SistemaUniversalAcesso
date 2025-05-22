package com.example.sistemauniversalacesso.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sistemauniversalacesso.database.SistemaDatabase;
import com.example.sistemauniversalacesso.databinding.LoginBinding;
import com.example.sistemauniversalacesso.models.Usuario;
import com.example.sistemauniversalacesso.utils.PasswordUtils;
import com.example.sistemauniversalacesso.utils.SessionManager;

import java.util.List;

public class login_activity extends AppCompatActivity {
    private LoginBinding binding;
    private SistemaDatabase db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = SistemaDatabase.getInstance(this);
        session = new SessionManager(this);

        // Auto-login se já estiver logado
        if (session.isLogado()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Botão de login
        binding.btnLogin.setOnClickListener(v -> RealizarLogin());

        // Ir para cadastro
        binding.btnCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(login_activity.this, cadastro_activity.class);
            startActivity(intent);
        });
    }

    private boolean isEmailValido(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isSenhaSegura(String senha) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return senha.matches(regex);
    }


    private void RealizarLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String senha = binding.etSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEmailValido(email)) {
            binding.etEmail.setError("Email inválido");
            return;
        }

        if (!isSenhaSegura(senha)) {
            binding.etSenha.setError("Senha não atende aos critérios mínimos.");
            return;
        }

        new Thread(() -> {
            List<Usuario> usuarios = db.UsuarioDao().loadALLEmail(email);
            Usuario usuario = (usuarios.isEmpty()) ? null : usuarios.get(0);

            runOnUiThread(() -> {
                if (usuario != null && PasswordUtils.verifyPassword(senha, usuario.getSenha())) {
                    SharedPreferences prefs = getSharedPreferences("SUAAppPrefs", MODE_PRIVATE);
                    prefs.edit().putBoolean("Logado", true).apply();

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