package br.com.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Estoque {

    private Long id;
    private BigDecimal quantidade;
    private String localTanque;
    private String localEndereco;
    private String loteFabricacao;
    private LocalDate dataValidade;
    private Long produtoId; // Referência ao produto

    public Estoque(Long id, BigDecimal quantidade, String localTanque, String localEndereco, String loteFabricacao, LocalDate dataValidade, Long produtoId) {
        this.id = id;
        this.quantidade = quantidade;
        this.localTanque = localTanque;
        this.localEndereco = localEndereco;
        this.loteFabricacao = loteFabricacao;
        this.dataValidade = dataValidade;
        this.produtoId = produtoId;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }

    public String getLocalTanque() { return localTanque; }
    public void setLocalTanque(String localTanque) { this.localTanque = localTanque; }

    public String getLocalEndereco() { return localEndereco; }
    public void setLocalEndereco(String localEndereco) { this.localEndereco = localEndereco; }

    public String getLoteFabricacao() { return loteFabricacao; }
    public void setLoteFabricacao(String loteFabricacao) { this.loteFabricacao = loteFabricacao; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
}
