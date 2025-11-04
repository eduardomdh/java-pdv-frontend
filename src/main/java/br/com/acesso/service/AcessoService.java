package br.com.acesso.service;

import br.com.acesso.dto.AcessoListResponse; // Importar a nova classe
import br.com.acesso.dto.AcessoRequest;
import br.com.acesso.dto.AcessoResponse;
import br.com.common.http.ApiClient;
import br.com.common.service.ApiServiceException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class AcessoService {

    private final ApiClient apiClient;

    public AcessoService() {
        this.apiClient = new ApiClient();
    }

    public List<AcessoResponse> findAcessos() throws IOException, ApiServiceException {
        // Mudar para esperar AcessoListResponse
        AcessoListResponse acessoListResponse = apiClient.get("/acessos", AcessoListResponse.class);
        return acessoListResponse.getAcessos(); // Extrair a lista de acessos
    }

    public AcessoResponse createAcesso(AcessoRequest acessoRequest) throws IOException, ApiServiceException {
        return apiClient.post("/acessos", acessoRequest, AcessoResponse.class);
    }

    public void deleteAcesso(Long id) throws IOException, ApiServiceException {
        apiClient.delete("/acessos/" + id);
    }
}
