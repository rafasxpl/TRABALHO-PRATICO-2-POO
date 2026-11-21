package loja.modelo.pagamento;

import loja.excecao.DadoInvalidoException;
import loja.modelo.pedido.StatusPedido;

/*
Cartão de crédito: 1 a 6 parcelas
Até 3x sem juros. De 4 parcelas em diante, juros simples de 2% ao mês
(total = valorBase x (1 + 1,02 * parcelas). O pedido já nasce pago
*/
public class PagamentoCartao extends FormaPagamento {
    public static final int MAX_PARCELAS = 6;
    private static final int PARCELAS_SEM_JUROS = 3;
    private static final double JUROS_MENSAL = 0.02;

    private int parcelas;

    public PagamentoCartao(int parcelas) throws DadoInvalidoException {
        setParcelas(parcelas);
    }

    public int getParcelas() {
        return parcelas;
    }

    public void setParcelas(int parcelas) throws DadoInvalidoException {
        if (parcelas < 1 || parcelas > MAX_PARCELAS) {
            throw new DadoInvalidoException("O número de parcelas deve ficar entre 1 e " + MAX_PARCELAS + ".");
        }
        this.parcelas = parcelas;
    }

    @Override
    public double calcularTotal(double valorBase) {
        if (parcelas <= PARCELAS_SEM_JUROS) {
            return arredondar(valorBase);
        }
        return arredondar(valorBase * (1 + JUROS_MENSAL * parcelas));
    }

    // Valor de cada parcela (usado pela TelaCheckout após downcast)
    public double getValorParcela(double valorBase) {
        return arredondar(calcularTotal(valorBase) / parcelas);
    }

    @Override
    public StatusPedido getStatusInicial() {
        return StatusPedido.PAGO;
    }

    @Override
    public String getDescricao() {
        String juros = parcelas <= PARCELAS_SEM_JUROS ? "sem juros" : "com juros simples de 2% a.m.";
        return "Cartão de crédito em " + parcelas + "x (" + juros + ")";
    }

    @Override
    public String paraLinha() {
        return "CARTAO;" + parcelas;
    }
}
