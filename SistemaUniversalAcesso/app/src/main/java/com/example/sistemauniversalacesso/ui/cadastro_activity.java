package com.example.sistemauniversalacesso.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sistemauniversalacesso.database.SistemaDatabase;
import com.example.sistemauniversalacesso.databinding.CadastroBinding;
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"usuario", "adm"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spNivel.setAdapter(adapter);

        binding.btnCadastrar.setOnClickListener(v -> realizarCadastro());
        binding.btnVoltar.setOnClickListener(v -> voltarLogin());
    }

    /**
     * Valida email com base no padrão Android
     */
    private boolean isEmailValido(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Valida senha forte (Regex):
     * - Mínimo 8 caracteres
     * - Pelo menos 1 maiúscula, 1 minúscula, 1 número e 1 símbolo
     */
    private boolean isSenhaSegura(String senha) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return senha.matches(regex);
    }

    /**
     * Realiza o cadastro do usuário após validações
     */
    private void realizarCadastro() {
        String nome = binding.etNome.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String senha = binding.etSenha.getText().toString();
        String nivel = binding.spNivel.getSelectedItem().toString();

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

            // Criptografar senha e salvar no banco
            String senhaCriptografada = PasswordUtils.generateSecurePassword(senha);
            Usuario novoUsuario = new Usuario(nome, email, senhaCriptografada, nivel);
            db.UsuarioDao().inserir(novoUsuario);

            runOnUiThread(() -> {
                Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                finish(); // Volta pra tela anterior (login)
            });
        }).start();
    }

    /**
     * Volta para a tela de login
     */
    private void voltarLogin() {
        Intent intent = new Intent(this, login_activity.class);
        startActivity(intent);
        finish(); // Fecha esta activity pra não voltar com o botão "Voltar"
    }
}
