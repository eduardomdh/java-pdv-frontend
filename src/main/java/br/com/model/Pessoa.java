package br.com.model;

import java.time.LocalDate;

public class Pessoa {

    private Long id;
    private String nomeCompleto;
    private String cpfCnpj;
    private Long numeroCtps;
    private LocalDate dataNascimento;
    private TipoPessoa tipoPessoa;

    public Pessoa(Long id, String nomeCompleto, String cpfCnpj, Long numeroCtps, LocalDate dataNascimento, TipoPessoa tipoPessoa) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.cpfCnpj = cpfCnpj;
        this.numeroCtps = numeroCtps;
        this.dataNascimento = dataNascimento;
        this.tipoPessoa = tipoPessoa;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }

    public Long getNumeroCtps() { return numeroCtps; }
    public void setNumeroCtps(Long numeroCtps) { this.numeroCtps = numeroCtps; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public TipoPessoa getTipoPessoa() { return tipoPessoa; }
    public void setTipoPessoa(TipoPessoa tipoPessoa) { this.tipoPessoa = tipoPessoa; }
}
