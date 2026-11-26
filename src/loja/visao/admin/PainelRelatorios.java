package loja.visao.admin;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import loja.modelo.pedido.StatusPedido;
import loja.servico.RelatorioServico;

public class PainelRelatorios extends JPanel {

    private final RelatorioServico servico;

    private final JLabel totalPedidos = new JLabel();
    private final JLabel aguardandoPagamento = new JLabel();
    private final JLabel pedidosPagos = new JLabel();
    private final JLabel pedidosCancelados = new JLabel();
    private final JLabel faturamento = new JLabel();
    private final JLabel ticketMedio = new JLabel();

    public PainelRelatorios(RelatorioServico servico) {
        this.servico = servico;
        montarComponentes();
        atualizar();
    }

    public void atualizar() {
        totalPedidos.setText("Total de pedidos: " + servico.totalPedidos());
        aguardandoPagamento.setText("Aguardando pagamento: "
                + servico.quantidadePorStatus(StatusPedido.AGUARDANDO_PAGAMENTO));
        pedidosPagos.setText("Pedidos pagos: "
                + servico.quantidadePorStatus(StatusPedido.PAGO));
        pedidosCancelados.setText("Pedidos cancelados: "
                + servico.quantidadePorStatus(StatusPedido.CANCELADO));
        faturamento.setText(String.format("Faturamento: R$ %.2f",
                servico.calcularFaturamento()));
        ticketMedio.setText(String.format("Ticket médio: R$ %.2f",
                servico.calcularTicketMedio()));
    }

    private void montarComponentes() {
        setLayout(new GridLayout(6, 1, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(totalPedidos);
        add(aguardandoPagamento);
        add(pedidosPagos);
        add(pedidosCancelados);
        add(faturamento);
        add(ticketMedio);
    }
}