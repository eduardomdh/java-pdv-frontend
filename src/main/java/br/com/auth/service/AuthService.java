package br.com.auth.service;

import br.com.auth.dto.LoginRequest;
import br.com.auth.dto.LoginResponse;
import br.com.common.http.ApiClient;
import br.com.common.service.ApiServiceException;
import br.com.common.session.SessionManager;

import java.io.IOException;

public class AuthService {

    private final ApiClient apiClient;

    public AuthService() {
        this.apiClient = new ApiClient();
    }

    public void login(String username, String password) throws IOException, ApiServiceException {
        LoginRequest request = new LoginRequest(username, password);
        LoginResponse response = apiClient.post("/acessos/login", request, LoginResponse.class);

        if (response != null && response.token() != null) {
            SessionManager.getInstance().setToken(response.token());
        } else {
            System.out.println("DEBUG: LoginResponse recebido: " + response);
            if (response != null) {
                System.out.println("DEBUG: Token dentro de LoginResponse: " + response.token());
            }
            throw new ApiServiceException("Token não recebido na resposta de login.");
        }
    }
}
