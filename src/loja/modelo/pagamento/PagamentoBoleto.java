package loja.modelo.pagamento;

import loja.modelo.pedido.StatusPedido;

// Boleto: sem desconto e sem juros
// O pedido nasce aguardando pagamento
public class PagamentoBoleto extends FormaPagamento {
    @Override 
    public double calcularTotal(double valorBase) {
        return arredondar(valorBase);
    }

    @Override 
    public StatusPedido getStatusInicial() {
        return StatusPedido.AGUARDANDO_PAGAMENTO;
    }

    @Override
    public String getDescricao() {
        return "Boleto Bancário";
    }

    @Override 
    public String paraLinha() {
        return "BOLETO";
    }
}
