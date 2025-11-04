package br.com.estoque.dto;

import br.com.estoque.enums.TipoEstoque;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EstoqueResponse(
    Long id,
    BigDecimal quantidade,
    String localTannque,
    String localEndereco,
    String localFabricacao,
    LocalDate dataValidade,
    TipoEstoque tipoEstoque
) {}
