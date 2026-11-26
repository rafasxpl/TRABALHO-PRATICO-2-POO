
package loja.servico;

import java.util.List;

import loja.modelo.pedido.Pedido;
import loja.modelo.pedido.StatusPedido;

public class RelatorioServico {

    private final PedidoServico pedidoServico;

    public RelatorioServico(PedidoServico pedidoServico) {
        this.pedidoServico = pedidoServico;
    }

    // Retorna a quantidade total de pedidos
    public int totalPedidos() {
        return pedidoServico.listar().size();
    }

    // Conta os pedidos de uma determinada situação
    public int quantidadePorStatus(StatusPedido status) {
        return pedidoServico.listarPorStatus(status).size();
    }

    // Calcula o faturamento considerando somente pedidos pagos
    public double calcularFaturamento() {
        double faturamento = 0.0;

        List<Pedido> pedidos = pedidoServico.listar();

        for (Pedido pedido : pedidos) {
            if (pedido.getStatus() == StatusPedido.PAGO) {
                faturamento += pedido.getTotal();
            }
        }

        return faturamento;
    }

    // Calcula o valor médio dos pedidos pagos
    public double calcularTicketMedio() {
        int quantidade = quantidadePorStatus(StatusPedido.PAGO);

        if (quantidade == 0) {
            return 0.0;
        }

        return calcularFaturamento() / quantidade;
    }
}
