package com.example.sistemauniversalacesso.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sistemauniversalacesso.database.SistemaDatabase;
import com.example.sistemauniversalacesso.databinding.CadastroBinding;
import com.example.sistemauniversalacesso.database.FirebaseService;
import com.example.sistemauniversalacesso.models.Usuario;
import com.example.sistemauniversalacesso.utils.PasswordUtils;

public class cadastro_activity extends AppCompatActivity {

    private CadastroBinding binding;
    private SistemaDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = SistemaDatabase.getInstance(this);

        binding.btnCadastrar.setOnClickListener(v -> realizarCadastro());
        binding.btnVoltar.setOnClickListener(v -> voltarLogin());
    }

    private boolean isEmailValido(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isSenhaSegura(String senha) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return senha.matches(regex);
    }

    private void realizarCadastro() {
        String nome = binding.etNome.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String senha = binding.etSenha.getText().toString();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEmailValido(email)) {
            binding.etEmail.setError("Email inválido");
            return;
        }

        if (!isSenhaSegura(senha)) {
            binding.etSenha.setError("A senha deve conter no mínimo 8 caracteres, com letras maiúsculas, minúsculas, número e símbolo.");
            return;
        }

        new Thread(() -> {
            int emailExists = db.UsuarioDao().checkEmailExists(email);

            if (emailExists > 0) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Email já cadastrado", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            // Criptografar senha
            String senhaCriptografada = PasswordUtils.generateSecurePassword(senha);
            Usuario novoUsuario = new Usuario(nome, email, senhaCriptografada, "adm");

            // Inserir no Room
            db.UsuarioDao().inserir(novoUsuario);

            // Inserir no Firebase (de forma assíncrona, mas com feedback)
            new Thread(() -> {
                String resultadoFirebase = FirebaseService.inserirUsuario(novoUsuario);
                runOnUiThread(() -> {
                    Toast.makeText(this, resultadoFirebase, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Enviado para Firebase!", Toast.LENGTH_SHORT).show();
                });
            }).start();

            runOnUiThread(() -> {
                Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            });

        }).start();
    }

    private void voltarLogin() {
        startActivity(new Intent(this, login_activity.class));
        finish();
    }
}
