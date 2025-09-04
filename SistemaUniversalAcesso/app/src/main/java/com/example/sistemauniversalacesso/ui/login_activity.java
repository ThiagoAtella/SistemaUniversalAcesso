package com.example.sistemauniversalacesso.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sistemauniversalacesso.databinding.LoginBinding;
import com.example.sistemauniversalacesso.models.Usuario;
import com.example.sistemauniversalacesso.utils.SessionManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class login_activity extends AppCompatActivity {

    private LoginBinding binding;
    private SessionManager session;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        binding = LoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        session = new SessionManager(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Verifica se o usuário já está logado no Firebase Auth
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        binding.btnLogin.setOnClickListener(v -> realizarLoginFirebase());
        binding.btnCadastro.setOnClickListener(v -> {
            startActivity(new Intent(this, cadastro_activity.class));
        });
    }

    private boolean isEmailValido(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showSuccessAnimation() {
        binding.lottieSuccess.setVisibility(View.VISIBLE);
        binding.lottieSuccess.playAnimation();
    }

    private void showErrorAnimation() {
        binding.lottieError.setVisibility(View.VISIBLE);
        binding.lottieError.playAnimation();
    }

    private void realizarLoginFirebase() {
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

        // --- Passo 1: Autenticar o usuário com Firebase Auth ---
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login bem-sucedido, agora buscamos os dados do usuário no Firestore
                        showSuccessAnimation();
                        buscarDadosDoUsuarioEIniciarSessao(task.getResult().getUser().getUid());
                    } else {
                        // Falha no login
                        showErrorAnimation();
                        Toast.makeText(login_activity.this, "Usuário ou senha inválidos.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void buscarDadosDoUsuarioEIniciarSessao(String uid) {
        db.collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            // --- CORREÇÃO APLICADA AQUI ---
                            // Em vez de converter o objeto inteiro, pegamos cada campo
                            // e garantimos que ele não seja nulo, fornecendo um valor padrão.

                            String nome = document.getString("nome");
                            if (nome == null || nome.isEmpty()) {
                                nome = "Usuário"; // Valor padrão caso o nome seja nulo ou vazio
                            }

                            String email = document.getString("email");
                            if (email == null) {
                                email = ""; // Valor padrão
                            }

                            String tipo = document.getString("tipo");
                            if (tipo == null || tipo.isEmpty()) {
                                tipo = "user"; // Valor padrão importante, para evitar erros de permissão
                            }

                            // Usamos as variáveis seguras (não-nulas) para salvar na sessão
                            final String nomeFinal = nome;
                            final String emailFinal = email;
                            final String tipoFinal = tipo;

                            // Atraso para a animação ser exibida
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                session.salvarSessao(nomeFinal, emailFinal, tipoFinal);
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            }, 1500); // 1.5 segundos de delay

                        } else {
                            showErrorAnimation();
                            Toast.makeText(this, "Dados do usuário não encontrados no banco.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        showErrorAnimation();
                        Toast.makeText(this, "Erro ao buscar dados do usuário.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}