package com.example.sistemauniversalacesso.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sistemauniversalacesso.databinding.CadastroBinding;
import com.example.sistemauniversalacesso.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class cadastro_activity extends AppCompatActivity {

    private CadastroBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase; // 👉 Agora usando Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa o Firebase Auth e Realtime Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        binding.btnCadastrar.setOnClickListener(v -> realizarCadastroFirebase());
        binding.btnVoltar.setOnClickListener(v -> finish());
    }

    private boolean isEmailValido(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isSenhaSegura(String senha) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return senha.matches(regex);
    }

    private void realizarCadastroFirebase() {
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

        // 🚀 Cria o usuário no Authentication
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            salvarDadosUsuarioRealtime(uid, nome, email);
                        }
                    } else {
                        Toast.makeText(cadastro_activity.this, "Falha no cadastro: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void salvarDadosUsuarioRealtime(String uid, String nome, String email) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);
        novoUsuario.setAvatar("avatar1");
        novoUsuario.setTipo("admin_view");
        novoUsuario.setCanEditUsers(false);
        novoUsuario.setMaster(false);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dataAtual = sdf.format(new Date());
        novoUsuario.setData_cadastro(dataAtual);


        // 🚀 Salva no Realtime Database em JSON
        mDatabase.child(uid).setValue(novoUsuario)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(cadastro_activity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(cadastro_activity.this, "Erro ao salvar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
