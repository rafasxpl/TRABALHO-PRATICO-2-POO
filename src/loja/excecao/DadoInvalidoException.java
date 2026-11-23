package loja.excecao;

// Campo vazio, preço ou peso negativo, número mal formatado, parcelas fora de 1 a 6.
public class DadoInvalidoException extends LojaException {
    public DadoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
