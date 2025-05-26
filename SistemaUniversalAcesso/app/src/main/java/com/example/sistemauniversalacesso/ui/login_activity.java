package com.example.sistemauniversalacesso.ui;

import android.content.Intent;
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

        binding.btnLogin.setOnClickListener(v -> RealizarLogin());
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

                    // ✅ Salvando sessão com nome, email e nível
                    session.salvarSessao(usuario.getNome(), usuario.getEmail(), usuario.getNivel());

                    startActivity(new Intent(login_activity.this, MainActivity.class));
                    finish();

                } else {
                    Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
