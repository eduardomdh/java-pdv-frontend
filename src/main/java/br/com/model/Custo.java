package br.com.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Custo {

    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate dataVencimento;

    public Custo(Long id, String descricao, BigDecimal valor, LocalDate dataVencimento) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
}
