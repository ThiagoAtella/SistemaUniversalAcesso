package com.example.sistemauniversalacesso; // VERIFIQUE SE ESTE É O SEU PACOTE PRINCIPAL

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MeuAplicativo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Esta linha vai forçar a inicialização do Firebase antes de qualquer outra coisa.
        FirebaseApp.initializeApp(this);
    }
}