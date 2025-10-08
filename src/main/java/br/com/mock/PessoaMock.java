package br.com.mock;

import br.com.model.Pessoa;
import br.com.model.TipoPessoa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PessoaMock {

    private static final List<Pessoa> pessoas = new ArrayList<>();
    private static long nextId = 1;

    // Bloco estático para inicializar com alguns dados
    static {
        pessoas.add(new Pessoa(nextId++, "João da Silva", "111.222.333-44", 12345L, LocalDate.of(1990, 5, 15), TipoPessoa.FISICA));
        pessoas.add(new Pessoa(nextId++, "Maria Oliveira", "222.333.444-55", 54321L, LocalDate.of(1985, 8, 20), TipoPessoa.FISICA));
        pessoas.add(new Pessoa(nextId++, "Empresa XYZ Ltda", "12.345.678/0001-99", null, LocalDate.of(2010, 1, 10), TipoPessoa.JURIDICA));
    }

    public static List<Pessoa> getPessoas() {
        return new ArrayList<>(pessoas); // Retorna uma cópia para evitar modificação externa
    }

    public static void addPessoa(Pessoa pessoa) {
        pessoa.setId(nextId++);
        pessoas.add(pessoa);
    }

    public static void removePessoa(Long id) {
        pessoas.removeIf(p -> p.getId().equals(id));
    }
}
