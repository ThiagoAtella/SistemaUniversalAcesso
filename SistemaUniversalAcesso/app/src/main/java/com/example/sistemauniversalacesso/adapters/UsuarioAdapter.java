package com.example.sistemauniversalacesso.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sistemauniversalacesso.databinding.ItemUsuarioBinding;
import com.example.sistemauniversalacesso.models.Usuario;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    //Interface de callback para ações no item (editar/deletar)
    public interface UsuarioCallback {
        void onEditar(Usuario usuario);
        void onDeletar(Usuario usuario);
    }

    private final List<Usuario> usuarios;      // Lista de usuários a serem exibidos
    private final UsuarioCallback callback;    // Callback para ações dos botões

    //Construtor do adapter que recebe a lista e o callback
    public UsuarioAdapter(List<Usuario> usuarios, UsuarioCallback callback) {
        this.usuarios = usuarios;
        this.callback = callback;
    }

    //Infla o layout XML de cada item da lista
    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Cria o binding para o layout do item (item_usuario.xml)
        ItemUsuarioBinding binding = ItemUsuarioBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new UsuarioViewHolder(binding);
    }

    //Associa os dados do usuário ao ViewHolder
    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.bind(usuario); // Associa os dados ao layout
    }

    //Retorna o número total de itens na lista
    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    //ViewHolder responsável por gerenciar o layout de cada item da lista
    class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private final ItemUsuarioBinding binding;

        public UsuarioViewHolder(ItemUsuarioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        //Popula os dados no item da lista e configura os botões
        void bind(Usuario usuario) {
            // Exibe nome e email no card
            binding.tvNome.setText(usuario.getNome());
            binding.tvEmail.setText(usuario.getEmail());

            // Ação do botão Editar
            binding.btnEditar.setOnClickListener(v -> callback.onEditar(usuario));

            // Ação do botão Deletar
            binding.btnDeletar.setOnClickListener(v -> callback.onDeletar(usuario));
        }
    }
}
