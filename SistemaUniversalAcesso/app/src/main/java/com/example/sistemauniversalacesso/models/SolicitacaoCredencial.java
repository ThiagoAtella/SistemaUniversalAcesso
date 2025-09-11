package com.example.sistemauniversalacesso.models;

public class SolicitacaoCredencial {
    private String id;
    private String aprovado_por;
    private boolean credential_generated;
    private long data_aprovacao;
    private long data_solicitacao;
    private String ip_address;
    private String justificativa;
    private String local_acesso;
    private String status;
    private String tipo;
    private String user_id;
    private String username;
    private int validade_dias;

    public SolicitacaoCredencial() {}

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAprovado_por() { return aprovado_por; }
    public void setAprovado_por(String aprovado_por) { this.aprovado_por = aprovado_por; }

    public boolean isCredential_generated() { return credential_generated; }
    public void setCredential_generated(boolean credential_generated) { this.credential_generated = credential_generated; }

    public long getData_aprovacao() { return data_aprovacao; }
    public void setData_aprovacao(long data_aprovacao) { this.data_aprovacao = data_aprovacao; }

    public long getData_solicitacao() { return data_solicitacao; }
    public void setData_solicitacao(long data_solicitacao) { this.data_solicitacao = data_solicitacao; }

    public String getIp_address() { return ip_address; }
    public void setIp_address(String ip_address) { this.ip_address = ip_address; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public String getLocal_acesso() { return local_acesso; }
    public void setLocal_acesso(String local_acesso) { this.local_acesso = local_acesso; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getValidade_dias() { return validade_dias; }
    public void setValidade_dias(int validade_dias) { this.validade_dias = validade_dias; }
}
