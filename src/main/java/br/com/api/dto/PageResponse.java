package br.com.api.dto;

import java.util.List;

/**
 * Representa uma resposta paginada genérica da API.
 * @param <T> o tipo do conteúdo da página.
 */
public record PageResponse<T>(
    List<T> content,
    int totalPages,
    long totalElements,
    int size,
    int number
) {}
