package loja.visao.admin;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;
import loja.modelo.produto.Produto;
import loja.modelo.produto.ProdutoFisico;

// Adapta uma lista de produtos para a JTable do PainelProdutos.
// A tabela só mostra os dados: quem altera produtos é o ProdutoServico.
public class ProdutoTableModel extends AbstractTableModel {
    private static final String[] COLUNAS = {"Código", "Nome", "Categoria", "Tipo", "Preço", "Estoque"};

    private static final int COL_CODIGO = 0;
    private static final int COL_NOME = 1;
    private static final int COL_CATEGORIA = 2;
    private static final int COL_TIPO = 3;
    private static final int COL_PRECO = 4;
    private static final int COL_ESTOQUE = 5;

    // Quando o Formatador (Membro 4) estiver pronto, este formato pode ser trocado por ele
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private List<Produto> produtos = new ArrayList<>();

    // Troca os dados exibidos. Guarda uma cópia para a tabela não depender da lista do serviço
    public void setProdutos(List<Produto> novaLista) {
        this.produtos = new ArrayList<>(novaLista);
        fireTableDataChanged();
    }

    // Produto da linha (índice do modelo, não da tela)
    public Produto getProdutoAt(int linha) {
        return produtos.get(linha);
    }

    @Override
    public int getRowCount() {
        return produtos.size();
    }

    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }

    @Override
    public String getColumnName(int coluna) {
        return COLUNAS[coluna];
    }

    // O código é número para a ordenação da tabela não tratá-lo como texto
    @Override
    public Class<?> getColumnClass(int coluna) {
        if (coluna == COL_CODIGO) {
            return Integer.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Produto p = produtos.get(linha);

        switch (coluna) {
            case COL_CODIGO:
                return p.getCodigo();
            case COL_NOME:
                return p.getNome();
            case COL_CATEGORIA:
                return p.getCategoria();
            case COL_TIPO:
                return "FISICO".equals(p.getTipo()) ? "Físico" : "Digital";
            case COL_PRECO:
                return moeda.format(p.getPreco());
            case COL_ESTOQUE:
                // Downcast: só o produto físico tem estoque
                if (p instanceof ProdutoFisico) {
                    ProdutoFisico fisico = (ProdutoFisico) p;
                    return String.valueOf(fisico.getEstoque());
                }
                return "Ilimitado";
            default:
                return "";
        }
    }
}