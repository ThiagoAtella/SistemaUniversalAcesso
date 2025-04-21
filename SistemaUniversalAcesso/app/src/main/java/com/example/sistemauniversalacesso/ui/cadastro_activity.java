package com.example.sistemauniversalacesso.ui;

import android.content.Intent;
import android.os.Bundle;
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

        binding.btnCadastrar.setOnClickListener(v -> realizarCadastro());
        binding.btnVoltar.setOnClickListener(view -> voltarLogin());
    }

    private void realizarCadastro() {
        String nome = binding.etNome.getText().toString();
        String email = binding.etEmail.getText().toString();
        String senha = binding.etSenha.getText().toString();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() ) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
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

            String senhaCriptografada = PasswordUtils.generateSecurePassword(senha);
            Usuario novoUsuario = new Usuario(nome, email, senhaCriptografada);
            db.UsuarioDao().inserir(novoUsuario);


            runOnUiThread(() -> {
                Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
    private void voltarLogin(){
    Intent intent = new Intent(cadastro_activity.this, login_activity.class);
    startActivity(intent);
    finish();
    }
}