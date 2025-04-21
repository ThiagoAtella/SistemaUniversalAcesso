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

    // View Binding para acessar os elementos da tela de cadastro
    private CadastroBinding binding;

    // Instância do banco de dados
    private SistemaDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa o binding com o layout da activity
        binding = CadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Pega a instância do banco de dados Room
        db = SistemaDatabase.getInstance(this);

        // Define o clique do botão "Cadastrar"
        binding.btnCadastrar.setOnClickListener(v -> realizarCadastro());

        // Define o clique do botão "Voltar"
        binding.btnVoltar.setOnClickListener(view -> voltarLogin());
    }

    /**
     * Realiza o cadastro do usuário após validações
     */
    private void realizarCadastro() {
        // Obtém os valores inseridos nos campos
        String nome = binding.etNome.getText().toString();
        String email = binding.etEmail.getText().toString();
        String senha = binding.etSenha.getText().toString();

        // Verifica se todos os campos foram preenchidos
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Realiza as operações com o banco de dados em uma thread separada
        new Thread(() -> {
            // Verifica se o e-mail já está cadastrado
            int emailExists = db.UsuarioDao().checkEmailExists(email);

            if (emailExists > 0) {
                // Se já existe, mostra mensagem e retorna
                runOnUiThread(() ->
                        Toast.makeText(this, "Email já cadastrado", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            // Criptografa a senha antes de salvar
            String senhaCriptografada = PasswordUtils.generateSecurePassword(senha);

            // Cria novo objeto de usuário
            Usuario novoUsuario = new Usuario(nome, email, senhaCriptografada);

            // Salva no banco
            db.UsuarioDao().inserir(novoUsuario);

            // Atualiza a UI após salvar
            runOnUiThread(() -> {
                Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                finish(); // Finaliza a tela de cadastro (volta pra tela anterior)
            });
        }).start();
    }

    /**
     * Volta para a tela de login
     */
    private void voltarLogin() {
        Intent intent = new Intent(cadastro_activity.this, login_activity.class);
        startActivity(intent);
        finish(); // Finaliza para evitar que volte com o botão "Voltar"
    }
}
