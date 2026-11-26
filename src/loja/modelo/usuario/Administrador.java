package loja.modelo.usuario;

public class Administrador extends Usuario {

    public Administrador(int id, String nome, String login, String senha) {
        super(id, nome, login, senha);
    }

    @Override
    public String getPerfil() {
        return "ADMIN";
    }

    @Override
    public String paraLinha() {
        return "ADMIN;" + getId() + ";" + getNome() + ";" + getLogin() + ";" + getSenha();
    }
}
