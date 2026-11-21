package loja.modelo.pagamento;

import loja.excecao.DadoInvalidoException;
import loja.modelo.pedido.StatusPedido;

// Forma de pagamento (classe abstrata), cada subclasse define suas regras
public abstract class FormaPagamento {
    // Valor final a pagar, aplicando desconto e juros (valorBase = subtotal + frete)
    public abstract double calcularTotal(double valorBase);
    
    // Situacao em que o pedido nasce com esta forma de pagamento
    public abstract StatusPedido getStatusInicial();

    // Texto para o resumo do checkout
    public abstract String getDescricao();

    // Texto para gravar em arquivo, ex: "PIX", "CARTAO;4", "BOLETO"
    public abstract String paraLinha();

    // Recria a forma de pagamento a partir do texto gravado por paraLinha()
    public static FormaPagamento deLinha(String texto) throws DadoInvalidoException {
        String[] partes = texto.split(";");
        if (partes[0].equals("PIX")) return new PagamentoPix();
        if (partes[0].equals("BOLETO")) return new PagamentoBoleto();
        if (partes[0].equals("CARTAO")) return new PagamentoCartao(Integer.parseInt(partes[1]));
        throw new DadoInvalidoException("Forma de pagamento desconhecida: " + partes[0]);
    }   

    // Arredonda para centavos
    protected static double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    @Override 
    public String toString() {
        return getDescricao();
    }
}
