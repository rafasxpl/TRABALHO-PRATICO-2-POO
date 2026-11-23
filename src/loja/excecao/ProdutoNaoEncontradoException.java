package loja.excecao;

// Código de produto inexistente.
public class ProdutoNaoEncontradoException extends LojaException {
    public ProdutoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
