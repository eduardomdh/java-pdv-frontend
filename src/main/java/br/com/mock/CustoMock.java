package br.com.mock;

import br.com.model.Custo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustoMock {

    private static final List<Custo> custos = new ArrayList<>();
    private static long nextId = 1;

    static {
        custos.add(new Custo(nextId++, "Conta de Energia Elétrica", new BigDecimal("1250.75"), LocalDate.now().plusDays(5)));
        custos.add(new Custo(nextId++, "Conta de Água", new BigDecimal("480.50"), LocalDate.now().plusDays(10)));
        custos.add(new Custo(nextId++, "Salários Funcionários", new BigDecimal("15800.00"), LocalDate.now().plusDays(2)));
    }

    public static List<Custo> getCustos() {
        return new ArrayList<>(custos);
    }

    public static void addCusto(Custo custo) {
        custo.setId(nextId++);
        custos.add(custo);
    }

    public static void removeCusto(Long id) {
        custos.removeIf(c -> c.getId().equals(id));
    }
}
