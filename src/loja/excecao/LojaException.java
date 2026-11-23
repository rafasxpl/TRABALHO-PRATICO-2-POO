package loja.excecao;

// Base de todas as exceções de negócio do sistema.
// Permite que as telas capturem tudo com um único catch (LojaException e).
public class LojaException extends Exception {
    public LojaException(String mensagem) {
        super(mensagem);
    }

    public LojaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
