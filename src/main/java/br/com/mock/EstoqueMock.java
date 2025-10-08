package br.com.mock;

import br.com.model.Estoque;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EstoqueMock {

    private static final List<Estoque> estoques = new ArrayList<>();
    private static long nextId = 1;

    static {
        // IDs de produto correspondem aos mocks de Produto (1: Gasolina Comum, 2: Gasolina Aditivada)
        estoques.add(new Estoque(nextId++, new BigDecimal("10000.50"), "Tanque 01", "Bloco A", "LOTE-A01", LocalDate.of(2025, 12, 31), 1L));
        estoques.add(new Estoque(nextId++, new BigDecimal("5000.25"), "Tanque 02", "Bloco B", "LOTE-B02", LocalDate.of(2025, 10, 20), 2L));
        estoques.add(new Estoque(nextId++, new BigDecimal("250.00"), "Prateleira 5", "Bloco C", "LOTE-C03", LocalDate.of(2026, 8, 15), 3L));
    }

    public static List<Estoque> getEstoques() {
        return new ArrayList<>(estoques);
    }

    public static void addEstoque(Estoque estoque) {
        estoque.setId(nextId++);
        estoques.add(estoque);
    }

    public static void removeEstoque(Long id) {
        estoques.removeIf(e -> e.getId().equals(id));
    }
}
