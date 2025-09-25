package com.example.sistemauniversalacesso.adapters;// Crie uma nova classe Java
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemauniversalacesso.R;
import com.example.sistemauniversalacesso.models.Credencial;
import com.example.sistemauniversalacesso.ui.VisualizarQrCodeActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CredencialAdapter extends RecyclerView.Adapter<CredencialAdapter.ViewHolder> {

    private List<Credencial> credenciais;
    private Map<String, String> mapaNomesLocais;
    private Context context;

    public CredencialAdapter(Context context, List<Credencial> credenciais, Map<String, String> mapaNomesLocais) {
        this.context = context;
        this.credenciais = credenciais;
        this.mapaNomesLocais = mapaNomesLocais;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credencial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Credencial credencial = credenciais.get(position);

        holder.textViewTipo.setText(credencial.getTipo());

        String nomeLocal = mapaNomesLocais.getOrDefault(credencial.local, "Geral");
        holder.textViewLocal.setText(nomeLocal);

        // Formatar a data de expiração (de segundos para milissegundos)
        Date dataExpiracao = new Date(credencial.expires_at * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.textViewExpira.setText("Expira em: " + sdf.format(dataExpiracao));

        if ("QR Code".equals(credencial.getTipo())) {
            // Se for QR Code, mostra o botão e configura o clique
            holder.btnVisualizar.setVisibility(View.VISIBLE);
            // holder.iconCredencial.setImageResource(R.drawable.ic_qrcode); // opcional
            holder.btnVisualizar.setOnClickListener(v -> {
                Intent intent = new Intent(context, VisualizarQrCodeActivity.class);
                intent.putExtra("QR_TOKEN", credencial.token);
                context.startActivity(intent);
            });
        } else {
            // Se for NFC, esconde o botão
            holder.btnVisualizar.setVisibility(View.GONE);
            // holder.iconCredencial.setImageResource(R.drawable.ic_nfc); // opcional
        }
    }

    @Override
    public int getItemCount() {
        return credenciais.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconCredencial;
        TextView textViewTipo, textViewLocal, textViewExpira;
        Button btnVisualizar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconCredencial = itemView.findViewById(R.id.iconCredencial);
            textViewTipo = itemView.findViewById(R.id.textViewTipo);
            textViewLocal = itemView.findViewById(R.id.textViewLocal);
            textViewExpira = itemView.findViewById(R.id.textViewExpira);
            btnVisualizar = itemView.findViewById(R.id.btnVisualizar);
        }
    }
}