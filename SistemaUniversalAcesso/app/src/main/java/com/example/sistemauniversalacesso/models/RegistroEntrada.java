package com.example.sistemauniversalacesso.models;

import java.util.HashMap;
import java.util.Map;

public class RegistroEntrada {
    public String user_id;
    public String user_name; // Pode buscar do /users/{user_id}/nome
    public String local; // ID do local
    public long entrada; // Timestamp em milissegundos
    public String data_entrada; // Data formatada
    public String metodo_acesso = "qr_code";
    public String status; // "dentro" ou "saiu"
    public Long saida; // Timestamp em milissegundos
    public String data_saida; // Data formatada
    public Long tempo_permanencia; // Em segundos
    public String tempo_permanencia_formatado; // HH:MM:SS
    public String tipo_refeicao; // Opcional, se for refeitório

    public RegistroEntrada() { }

    // Construtor para facilitar a criação na entrada
    public RegistroEntrada(String userId, String userName, String localId, long timestampEntrada, String dataFormatadaEntrada) {
        this.user_id = userId;
        this.user_name = userName;
        this.local = localId;
        this.entrada = timestampEntrada;
        this.data_entrada = dataFormatadaEntrada;
        this.status = "dentro";
        this.metodo_acesso = "qr_code";
    }

    // Método para atualizar na saída
    public Map<String, Object>toMapParaSaida(long timestampSaida, String dataFormatadaSaida, long permanenciaSegundos, String permanenciaFormatada) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", "saiu");
        map.put("saida", timestampSaida);
        map.put("data_saida", dataFormatadaSaida);
        map.put("tempo_permanencia", permanenciaSegundos);
        map.put("tempo_permanencia_formatado", permanenciaFormatada);
        return map;
    }
}