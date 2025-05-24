package com.example.sistemauniversalacesso.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.sistemauniversalacesso.R;
import com.example.sistemauniversalacesso.databinding.ActivityMainBinding;
import com.example.sistemauniversalacesso.fragments.ConfigFragment;
import com.example.sistemauniversalacesso.fragments.UsuariosFragment;
import com.example.sistemauniversalacesso.utils.SessionManager;

public class MainActivityAdm extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);

        if (!session.isLogado()) {
            startActivity(new Intent(this, login_activity.class));
            finish();
            return;
        }

        String nomeUsuario = session.getNome();
        Toast.makeText(this, "Administrador: " + nomeUsuario, Toast.LENGTH_SHORT).show();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.fragmentContainer.getId(), new UsuariosFragment()) // pode trocar por fragment específico do ADM
                    .commit();
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.nav_usuarios) {
                fragment = new UsuariosFragment();
            } else if (item.getItemId() == R.id.nav_config) {
                fragment = new ConfigFragment();
            } else if (item.getItemId() == R.id.nav_perfil) {
                Toast.makeText(this, "Painel ADM ainda em construção", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.fragmentContainer.getId(), fragment)
                        .commit();
                return true;
            }
            return false;
        });
    }
}
