package com.example.sistemauniversalacesso.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemauniversalacesso.databinding.ItemUsuarioBinding;
import com.example.sistemauniversalacesso.models.Usuario;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    public interface UsuarioCallback {
        void onEditar(Usuario usuario);
        void onDeletar(Usuario usuario);
    }

    private final List<Usuario> usuarios;
    private final UsuarioCallback callback;

    public UsuarioAdapter(List<Usuario> usuarios, UsuarioCallback callback) {
        this.usuarios = usuarios;
        this.callback = callback;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUsuarioBinding binding = ItemUsuarioBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new UsuarioViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.bind(usuario);
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private final ItemUsuarioBinding binding;

        public UsuarioViewHolder(ItemUsuarioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Usuario usuario) {
            binding.tvNome.setText(usuario.getNome());
            binding.tvEmail.setText(usuario.getEmail());

            binding.btnEditar.setOnClickListener(v -> callback.onEditar(usuario));
            binding.btnDeletar.setOnClickListener(v -> callback.onDeletar(usuario));
        }
    }
}
