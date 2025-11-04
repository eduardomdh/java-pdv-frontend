package br.com.pessoa.service;

import br.com.common.http.ApiClient;
import br.com.common.service.ApiServiceException;
import br.com.pessoa.dto.PessoaListResponse; // Importar a nova classe
import br.com.pessoa.dto.PessoaRequest;
import br.com.pessoa.dto.PessoaResponse;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class PessoaService {

    private final ApiClient apiClient;

    public PessoaService() {
        this.apiClient = new ApiClient();
    }

    public List<PessoaResponse> findPessoas() throws IOException, ApiServiceException {
        // Mudar para esperar PessoaListResponse
        PessoaListResponse pessoaListResponse = apiClient.get("/pessoas", PessoaListResponse.class);
        return pessoaListResponse.getPessoas(); // Extrair a lista de pessoas
    }

    public PessoaResponse createPessoa(PessoaRequest pessoaRequest) throws IOException, ApiServiceException {
        return apiClient.post("/pessoas", pessoaRequest, PessoaResponse.class);
    }

    public void deletePessoa(Long id) throws IOException, ApiServiceException {
        apiClient.delete("/pessoas/" + id);
    }
}
