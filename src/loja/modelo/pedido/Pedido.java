package loja.modelo.pedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import loja.excecao.DadoInvalidoException;
import loja.modelo.pagamento.FormaPagamento;
import loja.modelo.usuario.Cliente;

/*
Pedido fechado. Quem monta o pedido é PedidoServico.finalizar
*/
public class Pedido {
    private final int numero;
    private final Cliente cliente;
    private final List<ItemPedido> itens;
    private final double subtotal;
    private final double frete;
    private final double total;
    private final FormaPagamento pagamento;
    private StatusPedido status;
    private final LocalDateTime data;

    public Pedido(int numero, Cliente cliente, List<ItemPedido> itens, double subtotal, double frete, double total, FormaPagamento pagamento, StatusPedido status, LocalDateTime data) throws DadoInvalidoException {
        if (cliente == null || itens == null || itens.isEmpty() || status == null || data == null) {
            throw new DadoInvalidoException("Dados do pedido incompletos.");
        }
        this.numero = numero;
        this.cliente = cliente;
        this.itens = new ArrayList<>(itens);
        this.subtotal = subtotal;
        this.frete = frete;
        this.total = total;
        this.pagamento = pagamento;
        this.status = status;
        this.data = data;
    }

     public int getNumero() {
        return numero;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getFrete() {
        return frete;
    }

    public double getTotal() {
        return total;
    }

    public FormaPagamento getFormaPagamento() {
        return pagamento;
    }

    public StatusPedido getStatus() {
        return status;
    }

    // as regras quando a troca é permitida ficam em PedidoServico
    public void setStatus(StatusPedido status) throws DadoInvalidoException {
        if (status == null) {
            throw new DadoInvalidoException("Situação não informada");
        }
        this.status = status;
    }

    public LocalDateTime getData() {
        return data;
    }
}
