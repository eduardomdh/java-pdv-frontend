package br.com.api.dto;

public enum TipoAcesso {

    ADMINISTRADOR("Acesso Administrador"),
    FUNCIONARIO("Acesso Funcionário"),
    GERENCIA("Acesso Gerência");

    private  final String descricao;

    private TipoAcesso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
