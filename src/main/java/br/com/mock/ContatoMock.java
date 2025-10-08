package br.com.mock;

import br.com.model.Contato;

import java.util.ArrayList;
import java.util.List;

public class ContatoMock {

    private static final List<Contato> contatos = new ArrayList<>();
    private static long nextId = 1;

    static {
        contatos.add(new Contato(nextId++, "(11) 99999-8888", "joao.silva@email.com", "Rua das Flores, 123"));
        contatos.add(new Contato(nextId++, "(21) 88888-7777", "maria.o@email.com", "Avenida Principal, 456"));
        contatos.add(new Contato(nextId++, "(31) 77777-6666", "contato@xyz.com", "Praça da Empresa, 789"));
    }

    public static List<Contato> getContatos() {
        return new ArrayList<>(contatos);
    }

    public static void addContato(Contato contato) {
        contato.setId(nextId++);
        contatos.add(contato);
    }

    public static void removeContato(Long id) {
        contatos.removeIf(c -> c.getId().equals(id));
    }
}
