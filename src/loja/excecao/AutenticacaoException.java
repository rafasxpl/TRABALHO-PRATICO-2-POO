package loja.excecao;

// Login inexistente ou senha errada.
public class AutenticacaoException extends LojaException {
    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }
}
