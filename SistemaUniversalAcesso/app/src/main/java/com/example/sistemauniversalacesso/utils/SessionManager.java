package com.example.sistemauniversalacesso.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "SUAAppPrefs";
    private static final String KEY_LOGADO = "Logado";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_NOME = "Nome";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void salvarSessao(String nome, String email) {
        editor.putBoolean(KEY_LOGADO, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NOME, nome);
        editor.apply();
    }

    public boolean isLogado() {
        return prefs.getBoolean(KEY_LOGADO, false);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getNome() {
        return prefs.getString(KEY_NOME, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
