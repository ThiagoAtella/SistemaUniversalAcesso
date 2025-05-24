package com.example.sistemauniversalacesso.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    // Nome do arquivo de SharedPreferences
    private static final String PREF_NAME = "SUAAppPrefs";

    // Chaves para os dados salvos
    private static final String KEY_LOGADO = "Logado";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_NOME = "Nome";

    // Instâncias do SharedPreferences e do seu Editor
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    //Construtor que inicializa o SharedPreferences e seu Editor
    public SessionManager(Context context) {
        // Cria ou acessa o arquivo de preferências com o nome definido
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    //Salva os dados da sessão do usuário (login)
    public void salvarSessao(String nome, String email) {
        editor.putBoolean(KEY_LOGADO, true);     // Marca como logado
        editor.putString(KEY_EMAIL, email);      // Salva o e-mail
        editor.putString(KEY_NOME, nome);        // Salva o nome
        editor.apply();                          // Aplica as mudanças
    }

    //Verifica se o usuário está logado
    public boolean isLogado() {
        return prefs.getBoolean(KEY_LOGADO, false);
    }

    //Retorna o e-mail do usuário salvo na sessão
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    //Retorna o nome do usuário salvo na sessão
    public String getNome() {
        return prefs.getString(KEY_NOME, null);
    }

    //Limpa todos os dados da sessão (logout)
    public void logout() {
        editor.clear(); // Remove tudo
        editor.apply(); // Aplica a limpeza
    }
}
