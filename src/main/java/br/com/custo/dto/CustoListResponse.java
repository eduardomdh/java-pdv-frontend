package br.com.custo.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CustoListResponse {

    @SerializedName("content") // Mapeia a chave 'content' do JSON para o campo 'custos'
    private List<CustoResponse> custos;

    public List<CustoResponse> getCustos() {
        return custos;
    }

    public void setCustos(List<CustoResponse> custos) {
        this.custos = custos;
    }
}
