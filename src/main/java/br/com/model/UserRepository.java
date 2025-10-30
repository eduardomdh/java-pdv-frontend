package br.com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserRepository atua como um repositório de dados em memória (simulado).
 * Utiliza o padrão Singleton para garantir uma única fonte de verdade para os dados do usuário
 * em toda a aplicação.
 */
public class UserRepository {

    private static UserRepository instance;
    private final List<User> users = new ArrayList<>();
    private long nextId = 4; // Inicia após os dados semeados

    // Construtor privado para evitar instanciação externa
    private UserRepository() {
        // Dados Iniciais (Seed) com a correção
        seedInitialData();
    }

    /**
     * Retorna a única instância do repositório.
     */
    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    private void seedInitialData() {
        users.add(new User(1L, "Lucas", "123", "funcionario"));
        users.add(new User(2L, "Pedro", "456", "gerente")); // Corrigido de 'gerencia' para 'Pedro'
        users.add(new User(3L, "sistema", "789", "administrador"));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users); // Retorna uma cópia para evitar modificação externa direta
    }

    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equalsIgnoreCase(login))
                .findFirst();
    }

    public void addUser(User user) {
        user.setId(nextId++);
        users.add(user);
    }

    public void updateUser(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(updatedUser.getId())) {
                users.set(i, updatedUser);
                return;
            }
        }
    }

    public void removeUser(Long userId) {
        users.removeIf(user -> user.getId().equals(userId));
    }

    public Optional<User> findById(Long id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst();
    }
}
