package br.com.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Preco {

    private Long id;
    private BigDecimal valor;
    private LocalDate dataAlteracao;
    private Long produtoId; // Vínculo com o Produto

    public Preco(Long id, BigDecimal valor, LocalDate dataAlteracao, Long produtoId) {
        this.id = id;
        this.valor = valor;
        this.dataAlteracao = dataAlteracao;
        this.produtoId = produtoId;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getDataAlteracao() { return dataAlteracao; }
    public void setDataAlteracao(LocalDate dataAlteracao) { this.dataAlteracao = dataAlteracao; }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
}
