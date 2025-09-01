package com.example.sistemauniversalacesso.database;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MeuApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializa o Firebase aqui
        FirebaseApp.initializeApp(this);
    }
}