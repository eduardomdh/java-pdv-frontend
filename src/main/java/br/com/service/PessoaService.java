package br.com.service;

import br.com.api.client.ApiClient;
import br.com.api.dto.PageResponse;
import br.com.api.dto.PessoaRequest;
import br.com.api.dto.PessoaResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

public class PessoaService {

    private final ApiClient apiClient;

    public PessoaService() {
        this.apiClient = new ApiClient();
    }

    public PageResponse<PessoaResponse> listPessoas(int page, int size) throws IOException, InterruptedException {
        return apiClient.get("/pessoas?page=" + page + "&size=" + size, new TypeReference<>() {});
    }

    public PessoaResponse createPessoa(PessoaRequest pessoaRequest) throws IOException, InterruptedException {
        return apiClient.post("/pessoas", pessoaRequest, PessoaResponse.class);
    }

    public PessoaResponse getPessoaPorCpfCnpj(String cpfCnpj) throws IOException, InterruptedException {
        return apiClient.get("/pessoas?cpfCnpj=" + cpfCnpj, PessoaResponse.class);
    }
}
