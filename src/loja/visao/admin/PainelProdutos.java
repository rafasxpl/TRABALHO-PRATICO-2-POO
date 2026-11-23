package loja.visao.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import loja.excecao.LojaException;
import loja.modelo.produto.Produto;
import loja.servico.ProdutoServico;

// Aba de produtos do administrador: lista, busca, cadastra, altera e remove.
// Não tem regra de negócio: toda validação e toda alteração passam pelo ProdutoServico.
public class PainelProdutos extends JPanel {
    private static final int COL_NOME = 1;
    private static final int COL_CATEGORIA = 2;

    private final ProdutoServico servico;
    private final ProdutoTableModel modelo = new ProdutoTableModel();
    private final JTable tabela = new JTable(modelo);
    private final TableRowSorter<ProdutoTableModel> ordenador = new TableRowSorter<>(modelo);
    private final JTextField campoBusca = new JTextField(20);

    public PainelProdutos(ProdutoServico servico) {
        this.servico = servico;
        montarComponentes();
        atualizar();
    }

    // Recarrega a tabela com os produtos do serviço.
    // Público para a TelaPrincipalAdmin poder chamar ao trocar de aba
    public void atualizar() {
        modelo.setProdutos(servico.listar());
    }

    // Montagem da tela

    private void montarComponentes() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Busca por nome ou categoria (consulta)
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Buscar por nome ou categoria:"));
        topo.add(campoBusca);

        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });

        tabela.setRowSorter(ordenador);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);

        // Duplo clique abre a alteração
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    alterarSelecionado();
                }
            }
        });

        JButton botaoCadastrar = new JButton("Cadastrar");
        JButton botaoAlterar = new JButton("Alterar");
        JButton botaoRemover = new JButton("Remover");
        botaoCadastrar.addActionListener(e -> abrirDialogo(null));
        botaoAlterar.addActionListener(e -> alterarSelecionado());
        botaoRemover.addActionListener(e -> removerSelecionado());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoCadastrar);
        botoes.add(botaoAlterar);
        botoes.add(botaoRemover);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    // Ações

    // Mostra só as linhas cujo nome ou categoria contém o texto digitado
    private void filtrar() {
        String texto = campoBusca.getText().trim();
        if (texto.isEmpty()) {
            ordenador.setRowFilter(null);
        } else {
            ordenador.setRowFilter(
                    RowFilter.regexFilter("(?i)" + Pattern.quote(texto), COL_NOME, COL_CATEGORIA));
        }
    }

    // Abre o formulário. Com produto null é cadastro; com produto é alteração
    private void abrirDialogo(Produto produto) {
        Window janela = SwingUtilities.getWindowAncestor(this);
        DialogoProduto dialogo = new DialogoProduto(janela, servico, produto);
        dialogo.setVisible(true); // diálogo modal: o código só segue quando ele fechar

        if (dialogo.foiSalvo()) {
            atualizar();
        }
    }

    private void alterarSelecionado() {
        Produto produto = produtoSelecionado();
        if (produto == null) {
            avisarSemSelecao();
            return;
        }
        abrirDialogo(produto);
    }

    private void removerSelecionado() {
        Produto produto = produtoSelecionado();
        if (produto == null) {
            avisarSemSelecao();
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(this,
                "Remover o produto \"" + produto.getNome() + "\"?",
                "Confirmar remoção", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            servico.remover(produto.getCodigo());
            atualizar();
        } catch (LojaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Produto da linha selecionada, ou null se não houver seleção.
    // A tela pode estar ordenada ou filtrada, então o índice precisa ser convertido para o do modelo
    private Produto produtoSelecionado() {
        int linhaNaTela = tabela.getSelectedRow();
        if (linhaNaTela < 0) {
            return null;
        }
        int linhaNoModelo = tabela.convertRowIndexToModel(linhaNaTela);
        return modelo.getProdutoAt(linhaNoModelo);
    }

    private void avisarSemSelecao() {
        JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.", "Aviso",
                JOptionPane.INFORMATION_MESSAGE);
    }
}