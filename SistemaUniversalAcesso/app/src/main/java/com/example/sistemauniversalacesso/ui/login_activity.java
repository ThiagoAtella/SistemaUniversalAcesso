package com.example.sistemauniversalacesso.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.sistemauniversalacesso.database.FirebaseService;
import com.example.sistemauniversalacesso.databinding.LoginBinding;
import com.example.sistemauniversalacesso.models.Usuario;
import com.example.sistemauniversalacesso.utils.PasswordUtils;
import com.example.sistemauniversalacesso.utils.SessionManager;

import org.json.JSONObject;

import java.util.Iterator;

public class login_activity extends AppCompatActivity {

    private LoginBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);

        if (session.isLogado()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> realizarLogin());
        binding.btnCadastro.setOnClickListener(v -> {
            startActivity(new Intent(this, cadastro_activity.class));
        });
    }

    private boolean isEmailValido(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isSenhaSegura(String senha) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return senha.matches(regex);
    }

    private void showSuccessAnimation() {
        binding.lottieSuccess.setVisibility(android.view.View.VISIBLE);
        binding.lottieSuccess.playAnimation();
    }

    private void showErrorAnimation() {
        binding.lottieError.setVisibility(android.view.View.VISIBLE);
        binding.lottieError.playAnimation();
    }

    private void realizarLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String senhaDigitada = binding.etSenha.getText().toString();

        if (email.isEmpty() || senhaDigitada.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEmailValido(email)) {
            binding.etEmail.setError("Email inválido");
            return;
        }

        if (!isSenhaSegura(senhaDigitada)) {
            binding.etSenha.setError("Senha inválida.");
            return;
        }

        new Thread(() -> {
            try {
                JSONObject json = FirebaseService.getAllUsuariosJson();
                Usuario usuarioEncontrado = null;

                for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                    String id = it.next();
                    JSONObject obj = json.getJSONObject(id);
                    String emailBanco = obj.optString("email");

                    if (emailBanco.equalsIgnoreCase(email)) {
                        Usuario u = new Usuario();
                        u.setNome(obj.optString("nome"));
                        u.setEmail(emailBanco);
                        u.setSenha(obj.optString("senha"));
                        u.setNivel(obj.optString("nivel"));
                        u.setFirebaseId(id);
                        usuarioEncontrado = u;
                        break;
                    }
                }

                Usuario finalUsuario = usuarioEncontrado;
                runOnUiThread(() -> {
                    if (finalUsuario != null && PasswordUtils.verifyPassword(senhaDigitada, finalUsuario.getSenha())) {
                        showSuccessAnimation();

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            session.salvarSessao(
                                    finalUsuario.getNome(),
                                    finalUsuario.getEmail(),
                                    finalUsuario.getNivel()
                            );

                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        }, 1500);
                    } else {
                        showErrorAnimation();
                        Toast.makeText(this, "Usuário ou senha inválidos", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    showErrorAnimation();
                    Toast.makeText(this, "Erro ao conectar ao Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}
