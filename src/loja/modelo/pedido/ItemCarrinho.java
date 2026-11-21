package loja.modelo.pedido;

import loja.excecao.DadoInvalidoException;
import loja.modelo.produto.Produto;

// Um produto e a quantidade desejada dentro do carrinho
public class ItemCarrinho {
    private final Produto produto;
    private int quantidade;
    
    public ItemCarrinho(Produto produto, int quantidade) throws DadoInvalidoException {
        if (produto == null) {
            throw new DadoInvalidoException("Produto não informado.");
        }
        this.produto = produto;
        setQuantidade(quantidade);
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) throws DadoInvalidoException {
        if (quantidade <= 0) {
            throw new DadoInvalidoException("A quantidade deve ser maior que zero.");
        }
        this.quantidade = quantidade;
    }

    public double getSubtotal() {
        return produto.getPreco() * quantidade;
    }
}
