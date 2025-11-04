package br.com.contato.service;

import br.com.common.http.ApiClient;
import br.com.common.service.ApiServiceException;
import br.com.contato.dto.ContatoListResponse; // Importar a nova classe
import br.com.contato.dto.ContatoRequest;
import br.com.contato.dto.ContatoResponse;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ContatoService {

    private final ApiClient apiClient;

    public ContatoService() {
        this.apiClient = new ApiClient();
    }

    public List<ContatoResponse> findContatos() throws IOException, ApiServiceException {
        // Mudar para esperar ContatoListResponse
        ContatoListResponse contatoListResponse = apiClient.get("/contatos", ContatoListResponse.class);
        return contatoListResponse.getContatos(); // Extrair a lista de contatos
    }

    public ContatoResponse createContato(ContatoRequest contatoRequest) throws IOException, ApiServiceException {
        return apiClient.post("/contatos", contatoRequest, ContatoResponse.class);
    }

    public void deleteContato(Long id) throws IOException, ApiServiceException {
        apiClient.delete("/contatos/" + id);
    }
}
