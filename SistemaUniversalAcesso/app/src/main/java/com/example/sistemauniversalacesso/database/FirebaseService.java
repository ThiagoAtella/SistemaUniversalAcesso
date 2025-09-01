package com.example.sistemauniversalacesso.database;

import com.example.sistemauniversalacesso.models.LocalAcesso;
import com.example.sistemauniversalacesso.models.Usuario;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.List;

/**
 * Versão final e recomendada da FirebaseService.
 * Usa o SDK oficial do Cloud Firestore, é assíncrona e usa callbacks.
 */
public class FirebaseService {

    // --- Interfaces de Callback para lidar com respostas assíncronas ---
    public interface FirebaseCallback {
        void onComplete(boolean success, String message);
    }

    public interface FirebaseDataCallback<T> {
        void onComplete(T data);
        void onFailure(Exception e);
    }

    // --- Método auxiliar para obter a instância do Firestore ---
    private static FirebaseFirestore getDb() {
        return FirebaseFirestore.getInstance();
    }

    // --- MÉTODOS PARA USUÁRIOS ---

    /**
     * Salva os dados de um usuário no Firestore usando o UID da autenticação como ID do documento.
     */
    public static void salvarDadosUsuario(String uid, Usuario usuario, final FirebaseCallback callback) {
        getDb().collection("users").document(uid)
                .set(usuario)
                .addOnSuccessListener(aVoid -> callback.onComplete(true, "Dados do usuário salvos com sucesso."))
                .addOnFailureListener(e -> callback.onComplete(false, "Erro ao salvar dados: " + e.getMessage()));
    }

    /**
     * Lista todos os usuários do Firestore.
     */
    public static void listarUsuarios(final FirebaseDataCallback<List<Usuario>> callback) {
        getDb().collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Usuario> usuarios = queryDocumentSnapshots.toObjects(Usuario.class);
                    // Adiciona o UID a cada objeto, pois ele não vem por padrão
                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                        usuarios.get(i).setUid(queryDocumentSnapshots.getDocuments().get(i).getId());
                    }
                    callback.onComplete(usuarios);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Atualiza os dados de um usuário existente.
     */
    public static void atualizarUsuario(String uid, Usuario usuario, final FirebaseCallback callback) {
        getDb().collection("users").document(uid)
                .set(usuario, SetOptions.merge()) // SetOptions.merge() evita sobrescrever campos não alterados
                .addOnSuccessListener(aVoid -> callback.onComplete(true, "Usuário atualizado com sucesso."))
                .addOnFailureListener(e -> callback.onComplete(false, "Erro ao atualizar: " + e.getMessage()));
    }

    /**
     * Exclui os dados de um usuário do Firestore (não remove da autenticação).
     */
    public static void excluirDadosUsuario(String uid, final FirebaseCallback callback) {
        getDb().collection("users").document(uid).delete()
                .addOnSuccessListener(aVoid -> callback.onComplete(true, "Dados do usuário excluídos com sucesso."))
                .addOnFailureListener(e -> callback.onComplete(false, "Erro ao excluir dados: " + e.getMessage()));
    }


    // --- MÉTODOS PARA LOCAIS DE ACESSO ---

    /**
     * Lista todos os locais de acesso do Firestore.
     */
    public static void listarLocais(final FirebaseDataCallback<List<LocalAcesso>> callback) {
        getDb().collection("locais").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<LocalAcesso> locais = queryDocumentSnapshots.toObjects(LocalAcesso.class);
                    callback.onComplete(locais);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Salva um novo local ou sobrescreve um existente com o mesmo ID.
     */
    public static void salvarLocal(LocalAcesso local, final FirebaseCallback callback) {
        String id = local.getId();
        if (id == null || id.isEmpty()) {
            // Se o local é novo, o Firestore gera um ID automaticamente
            id = getDb().collection("locais").document().getId();
            local.setId(id);
        }

        getDb().collection("locais").document(id)
                .set(local)
                .addOnSuccessListener(aVoid -> callback.onComplete(true, "Local salvo com sucesso."))
                .addOnFailureListener(e -> callback.onComplete(false, "Erro ao salvar local: " + e.getMessage()));
    }

    /**
     * Exclui um local de acesso pelo seu ID.
     */
    public static void excluirLocal(String localId, final FirebaseCallback callback) {
        getDb().collection("locais").document(localId).delete()
                .addOnSuccessListener(aVoid -> callback.onComplete(true, "Local excluído com sucesso."))
                .addOnFailureListener(e -> callback.onComplete(false, "Erro ao excluir local: " + e.getMessage()));
    }
    // Adicione este método dentro da sua classe FirebaseService.java

    public static void atualizarLocal(LocalAcesso local, final FirebaseCallback callback) {
        getDb().collection("locais").document(local.getId())
                .set(local, SetOptions.merge()) // Usamos merge para não sobrescrever dados desnecessariamente
                .addOnSuccessListener(aVoid -> callback.onComplete(true, "Local atualizado com sucesso."))
                .addOnFailureListener(e -> callback.onComplete(false, "Erro ao atualizar local: " + e.getMessage()));
    }
}