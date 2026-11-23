package loja.excecao;

// Carrinho vazio ao finalizar, cancelamento ou troca de situação não permitidos.
public class OperacaoPedidoException extends LojaException {
    public OperacaoPedidoException(String mensagem) {
        super(mensagem);
    }
}
