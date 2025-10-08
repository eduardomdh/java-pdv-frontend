package br.com.mock;

import br.com.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class ProdutoMock {

    private static final List<Produto> produtos = new ArrayList<>();
    private static long nextId = 1;

    static {
        produtos.add(new Produto(nextId++, "Gasolina Comum", "GC001", "Petrobras", "Petrobras", "Combustível"));
        produtos.add(new Produto(nextId++, "Gasolina Aditivada", "GA002", "Shell", "Shell V-Power", "Combustível"));
        produtos.add(new Produto(nextId++, "Óleo Motor 15W40", "OL003", "Ipiranga", "Ipiranga F1 Master", "Lubrificante"));
    }

    public static List<Produto> getProdutos() {
        return new ArrayList<>(produtos);
    }

    public static void addProduto(Produto produto) {
        produto.setId(nextId++);
        produtos.add(produto);
    }

    public static void removeProduto(Long id) {
        produtos.removeIf(p -> p.getId().equals(id));
    }
}
