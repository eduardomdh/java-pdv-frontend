package br.com.model;

public class User {
    private Long id;
    private String login;
    private String password;
    private String tipoAcesso; // funcionario, gerente, administrador

    public User(Long id, String login, String password, String tipoAcesso) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.tipoAcesso = tipoAcesso;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipoAcesso() {
        return tipoAcesso;
    }

    public void setTipoAcesso(String tipoAcesso) {
        this.tipoAcesso = tipoAcesso;
    }
}
