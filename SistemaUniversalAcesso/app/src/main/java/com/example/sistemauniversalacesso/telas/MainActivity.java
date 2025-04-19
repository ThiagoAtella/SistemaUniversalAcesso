package com.example.sistemauniversalacesso.telas;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sistemauniversalacesso.R;
import com.example.sistemauniversalacesso.fragments.ConfigFragment;
import com.example.sistemauniversalacesso.fragments.UsuariosFragment;
import com.example.sistemauniversalacesso.utils.PasswordUtils;
import com.example.sistemauniversalacesso.utils.UsuarioAdapter;
import com.example.sistemauniversalacesso.database.SistemaDatabase;
import com.example.sistemauniversalacesso.databinding.ActivityMainBinding;
import com.example.sistemauniversalacesso.databinding.DialogEditarUsuarioBinding;
import com.example.sistemauniversalacesso.models.Usuario;
import com.example.sistemauniversalacesso.utils.SessionManager;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SistemaDatabase db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = SistemaDatabase.getInstance(this);
        session = new SessionManager(this);

        // Proteção extra: se não estiver logado, volta pro login
        if (!session.isLogado()) {
            startActivity(new Intent(this, login_activity.class));
            finish();
            return;
        }

        // Boas-vindas
        String nomeUsuario = session.getNome();
        Toast.makeText(this, "Bem-vindo(a), " + nomeUsuario, Toast.LENGTH_SHORT).show();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.nav_usuarios) {
                fragment = new UsuariosFragment();
            } else if (item.getItemId() == R.id.nav_perfil) {
                Toast.makeText(this, "Perfil (em breve)", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_config) {
                fragment = new ConfigFragment();
            }

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                return true;
            }

            return false;
        });
    }

}
