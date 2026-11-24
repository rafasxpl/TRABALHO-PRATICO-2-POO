package loja.modelo.pedido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import loja.excecao.DadoInvalidoException;
import loja.excecao.EstoqueInsuficienteException;
import loja.excecao.LojaException;
import loja.excecao.OperacaoPedidoException;
import loja.modelo.produto.Produto;

/*
Carrinho de compras do cliente
Apenas valida contra o estoque, a baixa real acontece em PedidoServico.finalizar()
*/
public class Carrinho {

    private final List<ItemCarrinho> itens = new ArrayList<>();

    // Adiciona o produto, se já existir no carrinho soma a quantidade
    public void adicionar(Produto produto, int quantidade) throws LojaException {
        if (produto == null) {
            throw new DadoInvalidoException("Produto não informado.");
        }
        if (quantidade <= 0) {
            throw new DadoInvalidoException("A quantidade deve ser maior que zero.");
        }
        ItemCarrinho existente = buscar(produto);
        int novaQuantidade = quantidade + (existente == null ? 0 : existente.getQuantidade());
        validarEstoque(produto, novaQuantidade);

        if (existente == null) {
            itens.add(new ItemCarrinho(produto, quantidade));
        } else {
            existente.setQuantidade(novaQuantidade);
        }
    }

    // Troca a quantidade de um item que já está no carrinho.
    public void alterarQuantidade(Produto produto, int novaQuantidade) throws LojaException {
        ItemCarrinho item = buscar(produto);
        if (item == null) {
            throw new OperacaoPedidoException("Este produto não está no carrinho.");
        }
        if (novaQuantidade <= 0) {
            throw new DadoInvalidoException("A quantidade deve ser maior que zero. Use remover() para tirar o item.");
        }
        validarEstoque(produto, novaQuantidade);
        item.setQuantidade(novaQuantidade);
    }

    public void remover(Produto produto) throws LojaException {
        ItemCarrinho item = buscar(produto);
        if (item == null) {
            throw new OperacaoPedidoException("Este produto não está no carrinho.");
        }
        itens.remove(item);
    }

    public void esvaziar() {
        itens.clear();
    }

    public double getSubtotal() {
        double total = 0;
        for (ItemCarrinho item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }

    public boolean estaVazio() {
        return itens.isEmpty();
    }

    public List<ItemCarrinho> getItens() {
        return Collections.unmodifiableList(itens);
    }

    private ItemCarrinho buscar(Produto produto) {
        if (produto == null) {
            return null;
        }
        for (ItemCarrinho item : itens) {
            if (item.getProduto().getCodigo() == produto.getCodigo()) {
                return item;
            }
        }
        return null;
    }

    private void validarEstoque(Produto produto, int quantidade) throws EstoqueInsuficienteException {
        if (!produto.temEstoque(quantidade)) {
            throw new EstoqueInsuficienteException(
                "Estoque insuficiente para \"" + produto.getNome() + "\". Quantidade solicitada: " + quantidade + ".");
        }
    }
}
