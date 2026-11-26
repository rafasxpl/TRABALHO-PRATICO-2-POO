package loja.modelo.usuario;

public abstract class Usuario {

    private int id;
    private String nome;
    private String login;
    private String senha;

    protected Usuario(int id, String nome, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    protected String getSenha() {
        return senha;
    }

    public boolean conferirSenha(String senhaDigitada) {
        return senha != null && senha.equals(senhaDigitada);
    }

    public abstract String getPerfil();

    public abstract String paraLinha();
}
