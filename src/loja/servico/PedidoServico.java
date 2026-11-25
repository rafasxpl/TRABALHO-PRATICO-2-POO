package loja.servico;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import loja.excecao.DadoInvalidoException;
import loja.excecao.EstoqueInsuficienteException;
import loja.excecao.LojaException;
import loja.excecao.OperacaoPedidoException;
import loja.modelo.pagamento.FormaPagamento;
import loja.modelo.pedido.Carrinho;
import loja.modelo.pedido.ItemCarrinho;
import loja.modelo.pedido.ItemPedido;
import loja.modelo.pedido.Pedido;
import loja.modelo.pedido.StatusPedido;
import loja.modelo.usuario.Cliente;
import loja.repositorio.RepositorioPedidos;
import loja.repositorio.RepositorioProdutos;

// Regras de negócio dos pedidos: finalizar, cancelar, confirmar pagamento e mudar situação
public class PedidoServico {
    private final RepositorioPedidos repositorioPedidos;
    private final RepositorioProdutos repositorioProdutos;
    private final CalculadoraFrete calculadoraFrete = new CalculadoraFrete();
    public PedidoServico(RepositorioPedidos repositorioPedidos, RepositorioProdutos repositorioProdutos) {
        this.repositorioPedidos = repositorioPedidos;
        this.repositorioProdutos = repositorioProdutos;
    }

    // Frete do carrinho atual (a TelaCheckout usa para mostrar o resumo antes de confirmar)
    public double calcularFrete(Carrinho carrinho) {
        return calculadoraFrete.calcular(carrinho.getItens(), carrinho.getSubtotal());
    }

    /*
    Fecha a compra: confere estoque, dá baixa, calcula frete e total, grava o pedido e esvazia o carrinho
    A situação inicial vem da forma de pagamento (Pix/cartão = PAGO, boleto = AGUARDANDO_PAGAMENTO)
    */
    public Pedido finalizar(Cliente cliente, FormaPagamento pagamento) throws LojaException {
        if (cliente == null || pagamento == null) {
            throw new DadoInvalidoException("Cliente e forma de pagamento são obrigatórios.");
        }
        Carrinho carrinho = cliente.getCarrinho();
        if (carrinho.estaVazio()) {
            throw new OperacaoPedidoException("O carrinho está vazio.");
        }

        // 1) confere tudo antes de mexer no estoque
        for (ItemCarrinho item : carrinho.getItens()) {
            if (!item.getProduto().temEstoque(item.getQuantidade())) {
                throw new EstoqueInsuficienteException(
                    "Estoque insuficiente para \"" + item.getProduto().getNome() + "\".");
            }
        }

        // 2) dá baixa, se algo falhar no meio, devolve o que já foi baixado
        List<ItemPedido> itensPedido = new ArrayList<>();
        List<ItemCarrinho> baixados = new ArrayList<>();
        try {
            for (ItemCarrinho item : carrinho.getItens()) {
                item.getProduto().baixarEstoque(item.getQuantidade());
                baixados.add(item);
                itensPedido.add(new ItemPedido(item));
            }
        } catch (LojaException e) {
            for (ItemCarrinho item : baixados) {
                item.getProduto().reporEstoque(item.getQuantidade());
            }
            throw e;
        }

        // 3) valores
        double subtotal = carrinho.getSubtotal();
        double frete = calculadoraFrete.calcular(carrinho.getItens(), subtotal);
        double total = pagamento.calcularTotal(subtotal + frete); // polimorfismo

        Pedido pedido = new Pedido(repositorioPedidos.proximoNumero(), cliente, itensPedido,
            subtotal, frete, total, pagamento, pagamento.getStatusInicial(), LocalDateTime.now());

        // 4) grava, se a gravação falhar, desfaz tudo (estoque devolvido e pedido retirado da lista)
        try {
            repositorioPedidos.adicionar(pedido);
            repositorioPedidos.salvar();
            repositorioProdutos.salvar();
        } catch (LojaException e) {
            desfazerFinalizacao(pedido, itensPedido);
            throw e;
        }

        // 5) só esvazia o carrinho depois que tudo deu certo
        carrinho.esvaziar();
        return pedido;
    }

    // Todos os pedidos (usado pelo PainelPedidos e pelo RelatorioServico)
    public List<Pedido> listar() {
        return repositorioPedidos.listar();
    }

    // Pedidos que estão em determinada situação
    public List<Pedido> listarPorStatus(StatusPedido status) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido pedido : repositorioPedidos.listar()) {
            if (pedido.getStatus() == status) {
                resultado.add(pedido);
            }
        }
        return resultado;
    }

    // Cancela pedido AGUARDANDO_PAGAMENTO ou PAGO (CANCELADO não cancela de novo) e devolve o estoque
    public void cancelar(Pedido pedido) throws LojaException {
        exigirPedido(pedido);
        StatusPedido atual = pedido.getStatus();
        if (atual != StatusPedido.AGUARDANDO_PAGAMENTO && atual != StatusPedido.PAGO) {
            throw new OperacaoPedidoException(
                "Não é possível cancelar um pedido com situação \"" + atual.getDescricao() + "\".");
        }
        for (ItemPedido item : pedido.getItens()) {
            item.getProduto().reporEstoque(item.getQuantidade());
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        repositorioPedidos.salvar();
        repositorioProdutos.salvar();
    }

    // O administrador confirma o boleto: AGUARDANDO_PAGAMENTO -> PAGO
    public void confirmarPagamento(Pedido pedido) throws LojaException {
        exigirPedido(pedido);
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new OperacaoPedidoException("Só é possível confirmar pagamento de pedido aguardando pagamento.");
        }
        pedido.setStatus(StatusPedido.PAGO);
        repositorioPedidos.salvar();
    }

    /*
    Muda a situação do pedido. Fluxo permitido:
    AGUARDANDO_PAGAMENTO -> PAGO (confirmar pagamento) e AGUARDANDO_PAGAMENTO/PAGO -> CANCELADO
    */
    public void atualizarStatus(Pedido pedido, StatusPedido novo) throws LojaException {
        exigirPedido(pedido);
        if (novo == null) {
            throw new DadoInvalidoException("Nova situação não informada.");
        }
        if (novo == StatusPedido.CANCELADO) {
            cancelar(pedido);
        } else if (novo == StatusPedido.PAGO) {
            confirmarPagamento(pedido);
        } else {
            throw new OperacaoPedidoException("Troca de \"" + pedido.getStatus().getDescricao()
                + "\" para \"" + novo.getDescricao() + "\" não é permitida.");
        }
    }

    // Devolve o estoque baixado e tenta tirar o pedido da lista/arquivo
    private void desfazerFinalizacao(Pedido pedido, List<ItemPedido> itens) {
        for (ItemPedido item : itens) {
            item.getProduto().reporEstoque(item.getQuantidade());
        }
        try {
            repositorioPedidos.remover(pedido);
            repositorioPedidos.salvar();
        } catch (LojaException ignorada) {
            // já estamos tratando uma falha, o erro original é o que será mostrado
        }
    }

    private void exigirPedido(Pedido pedido) throws DadoInvalidoException {
        if (pedido == null) {
            throw new DadoInvalidoException("Pedido não informado.");
        }
    }
}