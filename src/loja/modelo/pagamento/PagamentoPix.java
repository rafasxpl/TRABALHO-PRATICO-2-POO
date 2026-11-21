package loja.modelo.pagamento;

import loja.modelo.pedido.StatusPedido;

// Pix: 5% de desconto. O pedido ja nasce pago
public class PagamentoPix extends FormaPagamento {
    private static final double DESCONTO = 0.05;

    @Override 
    public double calcularTotal(double valorBase) {
        return arredondar(valorBase * (1 - DESCONTO));
    }

    @Override 
    public StatusPedido getStatusInicial() {
        return StatusPedido.PAGO;
    }

    @Override 
    public String getDescricao() {
        return "Pix (5% de desconto)";
    }

    @Override 
    public String paraLinha() {
        return "PIX";
    }
}
