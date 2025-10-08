package br.com.model;

public enum TipoPessoa {
    FISICA("Pessoa Física"),
    JURIDICA("Pessoa Jurídica");

    private final String descricao;

    TipoPessoa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.name(); // Para exibição no JComboBox
    }
}
