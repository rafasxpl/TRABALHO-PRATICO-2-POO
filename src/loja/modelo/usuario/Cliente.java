package loja.modelo.usuario;

import loja.modelo.pedido.Carrinho;

public class Cliente extends Usuario {

    private String cpf;
    private String email;
    private String endereco;
    private final Carrinho carrinho = new Carrinho();

    public Cliente(int id, String nome, String login, String senha,
                    String cpf, String email, String endereco) {
        super(id, nome, login, senha);
        this.cpf = cpf;
        this.email = email;
        this.endereco = endereco;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Carrinho getCarrinho() {
        return carrinho;
    }

    @Override
    public String getPerfil() {
        return "CLIENTE";
    }

    @Override
    public String paraLinha() {
        return "CLIENTE;" + getId() + ";" + getNome() + ";" + getLogin() + ";" + getSenha() + ";"
                + cpf + ";" + email + ";" + endereco;
    }
}
