package br.com.mock;

import br.com.model.Preco;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrecoMock {

    private static final List<Preco> precos = new ArrayList<>();
    private static long nextId = 1;

    static {
        // IDs de produto correspondem aos mocks de Produto (1: Gasolina Comum, 2: Gasolina Aditivada, 3: Óleo)
        precos.add(new Preco(nextId++, new BigDecimal("5.89"), LocalDate.now().minusDays(10), 1L));
        precos.add(new Preco(nextId++, new BigDecimal("5.79"), LocalDate.now().minusDays(20), 1L)); // Preço antigo para o mesmo produto
        precos.add(new Preco(nextId++, new BigDecimal("6.19"), LocalDate.now().minusDays(5), 2L));
        precos.add(new Preco(nextId++, new BigDecimal("45.50"), LocalDate.now().minusDays(30), 3L));
    }

    public static List<Preco> getPrecos() {
        return new ArrayList<>(precos);
    }

    public static void addPreco(Preco preco) {
        preco.setId(nextId++);
        precos.add(preco);
    }

    public static void removePreco(Long id) {
        precos.removeIf(p -> p.getId().equals(id));
    }
}
