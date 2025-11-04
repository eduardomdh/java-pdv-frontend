package br.com.acesso.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AcessoListResponse {

    @SerializedName("content") // Mapeia a chave 'content' do JSON para o campo 'acessos'
    private List<AcessoResponse> acessos;

    public List<AcessoResponse> getAcessos() {
        return acessos;
    }

    public void setAcessos(List<AcessoResponse> acessos) {
        this.acessos = acessos;
    }
}
