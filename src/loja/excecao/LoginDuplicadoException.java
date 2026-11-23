package loja.excecao;

// Cadastro de cliente com login já usado.
public class LoginDuplicadoException extends LojaException {
    public LoginDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
