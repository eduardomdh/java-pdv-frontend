package br.com.service;

import br.com.api.client.ApiClient;
import br.com.api.dto.EstoqueRequest;
import br.com.api.dto.EstoqueResponse;
import br.com.api.dto.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

public class EstoqueService {

    private final ApiClient apiClient;

    public EstoqueService() {
        this.apiClient = new ApiClient();
    }

    public PageResponse<EstoqueResponse> listEstoques(int page, int size) throws IOException, InterruptedException {
        return apiClient.get("/estoques?page=" + page + "&size=" + size, new TypeReference<>() {});
    }

    public EstoqueResponse createEstoque(EstoqueRequest estoqueRequest) throws IOException, InterruptedException {
        return apiClient.post("/estoques", estoqueRequest, EstoqueResponse.class);
    }

    public EstoqueResponse getEstoquePorLoteFabricacao(String loteFabricacao) throws IOException, InterruptedException {
        return apiClient.get("/estoques?loteFabricacao=" + loteFabricacao, EstoqueResponse.class);
    }
}
