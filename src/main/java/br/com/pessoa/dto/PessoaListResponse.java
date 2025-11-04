package br.com.pessoa.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PessoaListResponse {

    @SerializedName("content") // Mapeia a chave 'content' do JSON para o campo 'pessoas'
    private List<PessoaResponse> pessoas;

    public List<PessoaResponse> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<PessoaResponse> pessoas) {
        this.pessoas = pessoas;
    }
}
