package com.example.sistemauniversalacesso.telas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sistemauniversalacesso.R;
import com.example.sistemauniversalacesso.database.SistemaDatabase;
import com.example.sistemauniversalacesso.models.usuario;

public class login_activity extends AppCompatActivity {
    private EditText etEmail, etSenha;
    private Button btnLogin, btnCadastro;
    private SistemaDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btnLogin = findViewById(R.id.btnLogin);
        btnCadastro = findViewById(R.id.btnCadastro);
        db = SistemaDatabase.getInstance(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String senha = etSenha.getText().toString();

                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(login_activity.this, "É necessário preencher todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                new LoginTask().execute(email, senha);
            }
        });

        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login_activity.this, cadastro_activity.class);
                startActivity(intent);
            }
        });
    }

    private class LoginTask extends AsyncTask<String, Void, usuario> {

        @Override
        protected usuario doInBackground(String... params) {
            String email = params[0];
            String senha = params[1];

            return db.UuarioDao().login(email, senha);
        }

        @Override
        protected void onPostExecute(usuario Usuario) {
            super.onPostExecute(Usuario);
            if (Usuario != null) {
                Intent intent = new Intent(login_activity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(login_activity.this, "Email ou senha incorretos!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
