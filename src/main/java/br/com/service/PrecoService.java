package br.com.service;

import br.com.api.client.ApiClient;
import br.com.api.dto.PageResponse;
import br.com.api.dto.PrecoRequest;
import br.com.api.dto.PrecoResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

public class PrecoService {

    private final ApiClient apiClient;

    public PrecoService() {
        this.apiClient = new ApiClient();
    }

    public PageResponse<PrecoResponse> listPrecos(int page, int size) throws IOException, InterruptedException {
        return apiClient.get("/precos?page=" + page + "&size=" + size, new TypeReference<>() {});
    }

    public PrecoResponse createPreco(PrecoRequest precoRequest) throws IOException, InterruptedException {
        return apiClient.post("/precos", precoRequest, PrecoResponse.class);
    }
}
