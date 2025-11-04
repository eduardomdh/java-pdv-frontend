package br.com.service;

import br.com.api.client.ApiClient;
import br.com.api.dto.PageResponse;
import br.com.api.dto.ProdutoRequest;
import br.com.api.dto.ProdutoResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

public class ProdutoService {

    private final ApiClient apiClient;

    public ProdutoService() {
        this.apiClient = new ApiClient();
    }

    public PageResponse<ProdutoResponse> listProdutos(int page, int size) throws IOException, InterruptedException {
        return apiClient.get("/produtos?page=" + page + "&size=" + size, new TypeReference<>() {});
    }

    public ProdutoResponse createProduto(ProdutoRequest produtoRequest) throws IOException, InterruptedException {
        return apiClient.post("/produtos", produtoRequest, ProdutoResponse.class);
    }

    public ProdutoResponse getProdutoPorReferencia(String referencia) throws IOException, InterruptedException {
        return apiClient.get("/produtos?referencia=" + referencia, ProdutoResponse.class);
    }
}
