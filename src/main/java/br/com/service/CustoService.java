package br.com.service;

import br.com.api.client.ApiClient;
import br.com.api.dto.CustoRequest;
import br.com.api.dto.CustoResponse;
import br.com.api.dto.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.time.LocalDate;

public class CustoService {

    private final ApiClient apiClient;

    public CustoService() {
        this.apiClient = new ApiClient();
    }

    public PageResponse<CustoResponse> listCustos(int page, int size) throws IOException, InterruptedException {
        return apiClient.get("/custos?page=" + page + "&size=" + size, new TypeReference<>() {});
    }

    public CustoResponse createCusto(CustoRequest custoRequest) throws IOException, InterruptedException {
        return apiClient.post("/custos", custoRequest, CustoResponse.class);
    }

    public CustoResponse getCustoPorDataProcessamento(LocalDate dataProcessamento) throws IOException, InterruptedException {
        return apiClient.get("/custos?dataProcessamento=" + dataProcessamento, CustoResponse.class);
    }
}
