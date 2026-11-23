package loja.excecao;

// Quantidade pedida maior que o estoque do produto físico.
public class EstoqueInsuficienteException extends LojaException {
    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
