package br.com.estoque.service;

import br.com.common.http.ApiClient;
import br.com.common.service.ApiServiceException;
import br.com.estoque.dto.EstoqueListResponse; // Importar a nova classe
import br.com.estoque.dto.EstoqueRequest;
import br.com.estoque.dto.EstoqueResponse;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class EstoqueService {

    private final ApiClient apiClient;

    public EstoqueService() {
        this.apiClient = new ApiClient();
    }

    public List<EstoqueResponse> findEstoques() throws IOException, ApiServiceException {
        // Mudar para esperar EstoqueListResponse
        EstoqueListResponse estoqueListResponse = apiClient.get("/estoques", EstoqueListResponse.class);
        return estoqueListResponse.getEstoques(); // Extrair a lista de estoques
    }

    public EstoqueResponse createEstoque(EstoqueRequest estoqueRequest) throws IOException, ApiServiceException {
        return apiClient.post("/estoques", estoqueRequest, EstoqueResponse.class);
    }

    public void deleteEstoque(Long id) throws IOException, ApiServiceException {
        apiClient.delete("/estoques/" + id);
    }
}
