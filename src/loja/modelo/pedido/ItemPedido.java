package loja.modelo.pedido;

import loja.excecao.DadoInvalidoException;
import loja.modelo.produto.Produto;

// Item de um pedido ja fechado. O preco fica congelado na data da compra
public class ItemPedido {
    private final Produto produto;
    private final int quantidade;
    private final double precoUnitario;

    // Usado para ler o pedido do arquivo (preço vem gravado)
    public ItemPedido(Produto produto, int quantidade, double precoUnitario) throws DadoInvalidoException {
        if (produto == null) {
            throw new DadoInvalidoException("Produto não informado.");
        }

        if (quantidade <= 0) {
            throw new DadoInvalidoException("A quantidade deve ser maior que zero.");
        }

        if (precoUnitario < 0) {
            throw new DadoInvalidoException("O preço não pode ser negativo.");
        }

        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    // usado para finalizar a compra. Congela o preço atual do produto
    public ItemPedido(ItemCarrinho item) throws DadoInvalidoException {
        this(item.getProduto(), item.getQuantidade(), item.getProduto().getPreco());
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public double getSubtotal() {
        return precoUnitario * quantidade;
    }
}
