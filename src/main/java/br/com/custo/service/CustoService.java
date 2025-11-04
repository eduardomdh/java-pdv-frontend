package br.com.custo.service;

import br.com.common.http.ApiClient;
import br.com.common.service.ApiServiceException;
import br.com.custo.dto.CustoListResponse; // Importar a nova classe
import br.com.custo.dto.CustoRequest;
import br.com.custo.dto.CustoResponse;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class CustoService {

    private final ApiClient apiClient;

    public CustoService() {
        this.apiClient = new ApiClient();
    }

    public List<CustoResponse> findCustos() throws IOException, ApiServiceException {
        // Mudar para esperar CustoListResponse
        CustoListResponse custoListResponse = apiClient.get("/custos", CustoListResponse.class);
        return custoListResponse.getCustos(); // Extrair a lista de custos
    }

    public CustoResponse createCusto(CustoRequest custoRequest) throws IOException, ApiServiceException {
        return apiClient.post("/custos", custoRequest, CustoResponse.class);
    }

    public CustoResponse updateCusto(Long id, CustoRequest custoRequest) throws IOException, ApiServiceException {
        return apiClient.put("/custos/" + id, custoRequest, CustoResponse.class);
    }

    public void deleteCusto(Long id) throws IOException, ApiServiceException {
        apiClient.delete("/custos/" + id);
    }
}
