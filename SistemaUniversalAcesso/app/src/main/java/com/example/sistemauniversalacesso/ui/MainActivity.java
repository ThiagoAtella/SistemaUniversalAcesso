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

public class MainActivity extends AppCompatActivity {

    // View binding da main
    private ActivityMainBinding binding;
    // gerenciador de sessão do usuário
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new SessionManager(this);
        //verifica se o usuário já está logado
        if (!session.isLogado()) {
            startActivity(new Intent(this, login_activity.class)); // inicia a tela do login caso não esteja logado
            finish();
            return;
        }
        //toast de entrada com nome do usuário
        String nomeUsuario = session.getNome();
        Toast.makeText(this, "Bem-vindo(a), " + nomeUsuario, Toast.LENGTH_SHORT).show();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.fragmentContainer.getId(), new UsuariosFragment())
                    .commit(); // define o fragment usuario como o padrão ao iniciar a main
        }
        // configuração para o clique do bottomNavigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            // troca de fragment ao clicar no bottomNavigation
            if (item.getItemId() == R.id.nav_usuarios) {
                fragment = new UsuariosFragment();
            } else if (item.getItemId() == R.id.nav_perfil) {
                Toast.makeText(this, "Perfil (em breve)", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_config) {
                fragment = new ConfigFragment();
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
