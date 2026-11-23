package loja.visao.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import loja.excecao.LojaException;
import loja.modelo.produto.Produto;
import loja.servico.ProdutoServico;

// Painel administrativo de produtos: exibe a tabela e delega ações ao ProdutoServico.
public class PainelProdutos extends JPanel {
    private final ProdutoServico servico;
    private final ProdutoTableModel modeloTabela = new ProdutoTableModel();
    private final JTable tabela = new JTable(modeloTabela);

    private final JButton botaoCadastrar = new JButton("Cadastrar");
    private final JButton botaoAlterar = new JButton("Alterar");
    private final JButton botaoRemover = new JButton("Remover");
    private final JButton botaoAtualizar = new JButton("Atualizar");

    public PainelProdutos(ProdutoServico servico) {
        if (servico == null) {
            throw new IllegalArgumentException("ProdutoServico não pode ser nulo.");
        }
        this.servico = servico;

        montarComponentes();
        configurarEventos();
        atualizarTabela();
    }

    // Recarrega a tabela com os dados atuais do serviço
    public void atualizarTabela() {
        modeloTabela.setProdutos(servico.listar());
        atualizarEstadoBotoes();
    }

    private void montarComponentes() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoCreateRowSorter(true);
        tabela.getTableHeader().setReorderingAllowed(false);

        JScrollPane rolagem = new JScrollPane(tabela);
        add(rolagem, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelBotoes.add(botaoAtualizar);
        painelBotoes.add(botaoRemover);
        painelBotoes.add(botaoAlterar);
        painelBotoes.add(botaoCadastrar);

        add(painelBotoes, BorderLayout.SOUTH);

        atualizarEstadoBotoes();
    }

    private void configurarEventos() {
        botaoCadastrar.addActionListener(e -> cadastrarProduto());
        botaoAlterar.addActionListener(e -> alterarProdutoSelecionado());
        botaoRemover.addActionListener(e -> removerProdutoSelecionado());
        botaoAtualizar.addActionListener(e -> atualizarTabela());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                atualizarEstadoBotoes();
            }
        });
    }

    private void atualizarEstadoBotoes() {
        boolean temSelecao = tabela.getSelectedRow() != -1;
        botaoAlterar.setEnabled(temSelecao);
        botaoRemover.setEnabled(temSelecao);
    }

    // Converte a linha da visão (com ordenação/filtro) para o índice real do TableModel
    private Produto obterProdutoSelecionado() {
        int linhaVisao = tabela.getSelectedRow();
        if (linhaVisao == -1) {
            return null;
        }
        int linhaModelo = tabela.convertRowIndexToModel(linhaVisao);
        return modeloTabela.getProdutoAt(linhaModelo);
    }

    private void cadastrarProduto() {
        Window janelaPai = SwingUtilities.getWindowAncestor(this);
        DialogoProduto dialogo = new DialogoProduto(janelaPai, servico, null);
        dialogo.setVisible(true);

        if (dialogo.foiSalvo()) {
            atualizarTabela();
        }
    }

    private void alterarProdutoSelecionado() {
        Produto selecionado = obterProdutoSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para alterar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Window janelaPai = SwingUtilities.getWindowAncestor(this);
        DialogoProduto dialogo = new DialogoProduto(janelaPai, servico, selecionado);
        dialogo.setVisible(true);

        if (dialogo.foiSalvo()) {
            atualizarTabela();
        }
    }

    private void removerProdutoSelecionado() {
        Produto selecionado = obterProdutoSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para remover.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente remover o produto \"" + selecionado.getNome() + "\" (código "
                        + selecionado.getCodigo() + ")?",
                "Confirmar remoção",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            servico.remover(selecionado.getCodigo());
            atualizarTabela();
        } catch (LojaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}