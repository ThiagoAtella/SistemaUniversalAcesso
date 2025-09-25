package com.example.sistemauniversalacesso.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemauniversalacesso.R;
import com.example.sistemauniversalacesso.adapters.CredencialAdapter;
import com.example.sistemauniversalacesso.models.Credencial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinhasCredenciaisFragment extends Fragment {

    private RecyclerView recyclerView;
    private CredencialAdapter adapter;
    private List<Credencial> listaCredenciais;
    private Map<String, String> mapaNomesLocais;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private DatabaseReference dbRef;
    private FirebaseUser currentUser;
    private int fetchesCompleted = 0; // Contador para controlar buscas assíncronas

    // 1. onCreate: Apenas para inicializar dados e objetos não-visuais.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference();
        listaCredenciais = new ArrayList<>();
        mapaNomesLocais = new HashMap<>();
    }

    // 2. onCreateView: Apenas para inflar o layout XML e retornar a View.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // A linha abaixo substitui o setContentView()
        return inflater.inflate(R.layout.fragment_minhas_credenciais, container, false);
    }

    // 3. onViewCreated: Onde você interage com os componentes visuais.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Agora é seguro usar findViewById, mas a partir da 'view' do fragment.
        recyclerView = view.findViewById(R.id.recyclerViewCredenciais);
        progressBar = view.findViewById(R.id.progressBar);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);

        // Configuração do RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CredencialAdapter(getContext(), listaCredenciais, mapaNomesLocais);
        recyclerView.setAdapter(adapter);

        if (currentUser == null) {
            // Tratar caso de usuário não logado
            progressBar.setVisibility(View.GONE);
            textViewEmpty.setText("Usuário não autenticado.");
            textViewEmpty.setVisibility(View.VISIBLE);
            return;
        }

        // Inicia o carregamento dos dados
        carregarLocaisECredenciais();
    }

    private void carregarLocaisECredenciais() {
        progressBar.setVisibility(View.VISIBLE);
        textViewEmpty.setVisibility(View.GONE);

        dbRef.child("locais_acesso").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mapaNomesLocais.clear(); // Limpar para evitar duplicação se o fragment for recriado
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = ds.getKey();
                    String nome = ds.child("nome").getValue(String.class);
                    if (id != null && nome != null) {
                        mapaNomesLocais.put(id, nome);
                    }
                }
                carregarCredenciais();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                carregarCredenciais();
            }
        });
    }

    private void carregarCredenciais() {
        String userId = currentUser.getUid();
        fetchesCompleted = 0;
        listaCredenciais.clear(); // Limpar a lista antes de carregar novos dados

        // Buscar QR Codes
        dbRef.child("qrcodes").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Credencial credencial = snapshot.getValue(Credencial.class);
                    if (credencial != null && credencial.is_active && credencial.expires_at > (System.currentTimeMillis() / 1000)) {
                        credencial.setTipo("QR Code");
                        listaCredenciais.add(credencial);
                    }
                }
                checkFetchesCompleted();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkFetchesCompleted();
            }
        });

        // Buscar NFCs
        dbRef.child("nfcs").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Credencial credencial = snapshot.getValue(Credencial.class);
                    if (credencial != null && credencial.is_active && credencial.expires_at > (System.currentTimeMillis() / 1000)) {
                        credencial.setTipo("NFC");
                        listaCredenciais.add(credencial);
                    }
                }
                checkFetchesCompleted();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkFetchesCompleted();
            }
        });
    }

    private void checkFetchesCompleted() {
        fetchesCompleted++;
        if (fetchesCompleted == 2) { // 2 = QR Code + NFC
            progressBar.setVisibility(View.GONE);
            if (listaCredenciais.isEmpty()) {
                textViewEmpty.setVisibility(View.VISIBLE);
            } else {
                textViewEmpty.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        }
    }
}