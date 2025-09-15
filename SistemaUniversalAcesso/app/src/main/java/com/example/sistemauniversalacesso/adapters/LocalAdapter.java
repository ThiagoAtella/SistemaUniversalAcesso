package com.example.sistemauniversalacesso.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemauniversalacesso.databinding.ItemLocalBinding;
import com.example.sistemauniversalacesso.models.LocalAcesso;

import java.util.List;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalViewHolder> {

    // Interface de callback para ações no item (editar/deletar)
    public interface LocalCallback {
        void onEditar(LocalAcesso local);
        void onDeletar(LocalAcesso local);
    }

    private final List<LocalAcesso> locais;   // Lista de locais a serem exibidos
    private final LocalCallback callback;     // Callback para ações dos botões

    // Construtor do adapter que recebe a lista e o callback
    public LocalAdapter(List<LocalAcesso> locais, LocalCallback callback) {
        this.locais = locais;
        this.callback = callback;
    }

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLocalBinding binding = ItemLocalBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new LocalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
        LocalAcesso local = locais.get(position);
        holder.bind(local);
    }

    @Override
    public int getItemCount() {
        return locais.size();
    }

    class LocalViewHolder extends RecyclerView.ViewHolder {
        private final ItemLocalBinding binding;

        public LocalViewHolder(ItemLocalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(LocalAcesso local) {
            // Exibe os campos principais
            binding.tvNome.setText(local.getNome());

            // Ação do botão Editar
            binding.btnEditar.setOnClickListener(v -> callback.onEditar(local));

            // Ação do botão Deletar
            binding.btnDeletar.setOnClickListener(v -> callback.onDeletar(local));
        }
    }
}
