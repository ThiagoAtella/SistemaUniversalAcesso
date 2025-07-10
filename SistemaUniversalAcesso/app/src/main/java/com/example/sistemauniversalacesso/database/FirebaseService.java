package com.example.sistemauniversalacesso.database;

import com.example.sistemauniversalacesso.database.FirebaseConfig;
import com.example.sistemauniversalacesso.models.LocalAcesso;
import com.example.sistemauniversalacesso.models.Usuario;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FirebaseService {

    public static String inserirUsuario(Usuario usuario) {
        try {
            URL url = new URL(FirebaseConfig.DATABASE_URL + "/usuarios.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("nome", usuario.getNome());
            json.put("email", usuario.getEmail());
            json.put("senha", usuario.getSenha());
            json.put("nivel", usuario.getNivel());

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            JSONObject resJson = new JSONObject(response.toString());
            return "Inserido com ID: " + resJson.optString("name");

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro: " + e.getMessage();
        }
    }

    public static List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        try {
            URL url = new URL(FirebaseConfig.DATABASE_URL + "/usuarios.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            JSONObject json = new JSONObject(response.toString());

            for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                String id = it.next(); // <- ESTE É O firebaseId

                JSONObject obj = json.getJSONObject(id);

                Usuario usuario = new Usuario();
                usuario.setNome(obj.optString("nome"));
                usuario.setEmail(obj.optString("email"));
                usuario.setSenha(obj.optString("senha"));
                usuario.setNivel(obj.optString("nivel"));
                usuario.setFirebaseId(id);

                lista.add(usuario);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static JSONObject getAllUsuariosJson() {
        try {
            URL url = new URL(FirebaseConfig.DATABASE_URL + "/usuarios.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return new JSONObject(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject(); // Retorna vazio em caso de falha
        }
    }


    public static String atualizarUsuario(String firebaseId, Usuario usuario) {
        try {
            URL url = new URL(FirebaseConfig.DATABASE_URL + "/usuarios/" + firebaseId + ".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("nome", usuario.getNome());
            json.put("email", usuario.getEmail());
            json.put("senha", usuario.getSenha());
            json.put("nivel", usuario.getNivel());

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return "Atualizado: " + response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro: " + e.getMessage();
        }
    }

    public static String excluirUsuario(String firebaseId) {
        try {
            URL url = new URL(FirebaseConfig.DATABASE_URL + "/usuarios/" + firebaseId + ".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            return (responseCode == 200) ? "Excluído com sucesso" : "Erro ao excluir: " + responseCode;

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro: " + e.getMessage();
        }
    }
    public static List<LocalAcesso> listarLocais() {
        List<LocalAcesso> locais = new ArrayList<>();
        try {
            // Conectar ao Firebase Realtime Database (com URL correta)
            URL url = new URL(FirebaseConfig.DATABASE_URL + "/locais.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Convertendo a resposta JSON para uma lista de locais
            JSONObject jsonResponse = new JSONObject(response.toString());
            Iterator<String> keys = jsonResponse.keys();

            // Itera pelos locais e cria objetos de LocalAcesso
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject localJson = jsonResponse.getJSONObject(key);

                String nome = localJson.optString("nome");
                String tipo = localJson.optString("tipo");
                int capacidade = localJson.optInt("capacidade");
                String endereco = localJson.optString("endereco");
                boolean exigePagamento = localJson.optBoolean("exigePagamento");

                LocalAcesso local = new LocalAcesso(key, nome, tipo, capacidade, endereco, exigePagamento);
                locais.add(local);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return locais;
    }
    public static void salvarLocal(LocalAcesso local) {
        try {
            URL url = new URL(FirebaseConfig.DATABASE_URL + "/locais/" + local.getId() + ".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("id", local.getId());
            json.put("nome", local.getNome());
            json.put("tipo", local.getTipo());
            json.put("capacidade", local.getCapacidade());
            json.put("endereco", local.getEndereco());
            json.put("exigePagamento", local.isExigePagamento());

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            os.close();
            conn.getInputStream(); // Força o envio
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void atualizarLocal(LocalAcesso local) {
        try {
            URL url = new URL(FirebaseConfig.DATABASE_URL + "/locais/" + local.getId() + ".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("nome", local.getNome());
            json.put("tipo", local.getTipo());
            json.put("capacidade", local.getCapacidade());
            json.put("endereco", local.getEndereco());
            json.put("exigePagamento", local.isExigePagamento());

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            os.close();
            conn.getInputStream(); // Força o envio
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void excluirLocal(String localId) {
        try {
            URL url = new URL(FirebaseConfig.DATABASE_URL + "/locais/" + localId + ".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");

            conn.getInputStream(); // Força a execução da operação
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


