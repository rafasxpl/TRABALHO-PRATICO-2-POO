package loja.visao.admin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import loja.excecao.DadoInvalidoException;
import loja.excecao.LojaException;
import loja.modelo.produto.Produto;
import loja.modelo.produto.ProdutoDigital;
import loja.modelo.produto.ProdutoFisico;
import loja.servico.ProdutoServico;

// Formulário para cadastrar ou alterar um produto.
// Os campos específicos trocam conforme o tipo (físico ou digital).
// Se produtoEditado for null o diálogo cadastra; senão, altera aquele produto.
public class DialogoProduto extends JDialog {
    private static final String CARTAO_FISICO = "FISICO";
    private static final String CARTAO_DIGITAL = "DIGITAL";
    private static final int INDICE_FISICO = 0;

    private final ProdutoServico servico;
    private final Produto produtoEditado;
    private int codigoNovo;
    private boolean salvo = false;

    // Campos comuns a todos os produtos
    private final JTextField campoCodigo = new JTextField(12);
    private final JTextField campoNome = new JTextField(20);
    private final JTextField campoDescricao = new JTextField(20);
    private final JTextField campoCategoria = new JTextField(20);
    private final JTextField campoPreco = new JTextField(12);
    private final JComboBox<String> comboTipo = new JComboBox<>(new String[] {"Físico", "Digital"});

    // Campos que só existem em um dos tipos
    private final JTextField campoPeso = new JTextField(12);
    private final JTextField campoEstoque = new JTextField(12);
    private final JTextField campoTamanho = new JTextField(12);

    // Painel que mostra só os campos do tipo escolhido
    private final CardLayout cartoes = new CardLayout();
    private final JPanel painelEspecifico = new JPanel(cartoes);

    public DialogoProduto(Window dono, ProdutoServico servico, Produto produtoEditado) {
        super(dono, produtoEditado == null ? "Cadastrar produto" : "Alterar produto",
                Dialog.ModalityType.APPLICATION_MODAL);
        this.servico = servico;
        this.produtoEditado = produtoEditado;

        montarComponentes();
        preencherCampos();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(dono);
    }

    // Diz se o produto foi gravado (o painel usa para atualizar a tabela)
    public boolean foiSalvo() {
        return salvo;
    }

    //Montagem da tela

    private void montarComponentes() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        campoCodigo.setEditable(false); // o código é gerado pelo serviço

        adicionarLinha(formulario, 0, "Código:", campoCodigo);
        adicionarLinha(formulario, 1, "Tipo:", comboTipo);
        adicionarLinha(formulario, 2, "Nome:", campoNome);
        adicionarLinha(formulario, 3, "Descrição:", campoDescricao);
        adicionarLinha(formulario, 4, "Categoria:", campoCategoria);
        adicionarLinha(formulario, 5, "Preço (R$):", campoPreco);

        JPanel cartaoFisico = new JPanel(new GridBagLayout());
        adicionarLinha(cartaoFisico, 0, "Peso (kg):", campoPeso);
        adicionarLinha(cartaoFisico, 1, "Estoque:", campoEstoque);

        JPanel cartaoDigital = new JPanel(new GridBagLayout());
        adicionarLinha(cartaoDigital, 0, "Tamanho (MB):", campoTamanho);

        painelEspecifico.add(cartaoFisico, CARTAO_FISICO);
        painelEspecifico.add(cartaoDigital, CARTAO_DIGITAL);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(painelEspecifico, c);

        comboTipo.addActionListener(e -> mostrarCamposDoTipo());

        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoCancelar = new JButton("Cancelar");
        botaoSalvar.addActionListener(e -> salvar());
        botaoCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(botaoSalvar);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoCancelar);
        botoes.add(botaoSalvar);

        setLayout(new BorderLayout());
        add(formulario, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    // Coloca um rótulo e um campo lado a lado numa linha do formulário
    private void adicionarLinha(JPanel painel, int linha, String rotulo, javax.swing.JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = linha;
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        painel.add(new JLabel(rotulo), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        painel.add(campo, c);
    }

    private void mostrarCamposDoTipo() {
        if (comboTipo.getSelectedIndex() == INDICE_FISICO) {
            cartoes.show(painelEspecifico, CARTAO_FISICO);
        } else {
            cartoes.show(painelEspecifico, CARTAO_DIGITAL);
        }
    }

    // Cadastro: campos vazios e próximo código livre.
    // Alteração: campos com os valores atuais e tipo travado
    private void preencherCampos() {
        if (produtoEditado == null) {
            codigoNovo = servico.proximoCodigo();
            campoCodigo.setText(String.valueOf(codigoNovo));
            mostrarCamposDoTipo();
            return;
        }

        campoCodigo.setText(String.valueOf(produtoEditado.getCodigo()));
        campoNome.setText(produtoEditado.getNome());
        campoDescricao.setText(produtoEditado.getDescricao());
        campoCategoria.setText(produtoEditado.getCategoria());
        campoPreco.setText(String.valueOf(produtoEditado.getPreco()));

        // Downcast: peso e estoque só existem em ProdutoFisico; tamanho só em ProdutoDigital
        if (produtoEditado instanceof ProdutoFisico) {
            ProdutoFisico fisico = (ProdutoFisico) produtoEditado;
            comboTipo.setSelectedIndex(INDICE_FISICO);
            campoPeso.setText(String.valueOf(fisico.getPesoKg()));
            campoEstoque.setText(String.valueOf(fisico.getEstoque()));
        } else if (produtoEditado instanceof ProdutoDigital) {
            ProdutoDigital digital = (ProdutoDigital) produtoEditado;
            comboTipo.setSelectedIndex(1);
            campoTamanho.setText(String.valueOf(digital.getTamanhoArquivoMb()));
        }

        // Trocar o tipo exigiria criar outro objeto, então o tipo fica fixo na alteração
        comboTipo.setEnabled(false);
        mostrarCamposDoTipo();
    }

    // Gravação

    private void salvar() {
        try {
            if (produtoEditado == null) {
                servico.cadastrar(montarProduto(codigoNovo));
            } else {
                alterarProdutoExistente();
            }
            salvo = true;
            dispose();
        } catch (LojaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Lê os campos da tela e cria o produto do tipo escolhido.
    // Qualquer valor inválido lança DadoInvalidoException (vinda do modelo ou da conversão)
    private Produto montarProduto(int codigo) throws DadoInvalidoException {
        String nome = campoNome.getText();
        String descricao = campoDescricao.getText();
        String categoria = campoCategoria.getText();
        double preco = lerDecimal(campoPreco.getText(), "preço");

        if (comboTipo.getSelectedIndex() == INDICE_FISICO) {
            double peso = lerDecimal(campoPeso.getText(), "peso");
            int estoque = lerInteiro(campoEstoque.getText(), "estoque");
            return new ProdutoFisico(codigo, nome, descricao, categoria, preco, peso, estoque);
        }

        double tamanho = lerDecimal(campoTamanho.getText(), "tamanho");
        return new ProdutoDigital(codigo, nome, descricao, categoria, preco, tamanho);
    }

    // Altera o MESMO objeto que já está na lista, em vez de trocar por um novo.
    // Assim os pedidos que apontam para este produto continuam enxergando os valores atualizados.
    private void alterarProdutoExistente() throws LojaException {
        // Primeiro monta um produto temporário: se algum campo for inválido, a exceção sai aqui e o produto original não chega a ser modificado pela metade
        Produto validado = montarProduto(produtoEditado.getCodigo());

        produtoEditado.setNome(validado.getNome());
        produtoEditado.setDescricao(validado.getDescricao());
        produtoEditado.setCategoria(validado.getCategoria());
        produtoEditado.setPreco(validado.getPreco());

        // Downcast para copiar os atributos de cada tipo
        if (produtoEditado instanceof ProdutoFisico && validado instanceof ProdutoFisico) {
            ProdutoFisico atual = (ProdutoFisico) produtoEditado;
            ProdutoFisico novo = (ProdutoFisico) validado;
            atual.setPesoKg(novo.getPesoKg());
            atual.setEstoque(novo.getEstoque());
        } else if (produtoEditado instanceof ProdutoDigital && validado instanceof ProdutoDigital) {
            ProdutoDigital atual = (ProdutoDigital) produtoEditado;
            ProdutoDigital novo = (ProdutoDigital) validado;
            atual.setTamanhoArquivoMb(novo.getTamanhoArquivoMb());
        }

        servico.alterar(produtoEditado);
    }

    // Conversão de texto em número

    // Aceita vírgula ou ponto como separador decimal
    private double lerDecimal(String texto, String campo) throws DadoInvalidoException {
        try {
            double valor = Double.parseDouble(texto.trim().replace(',', '.'));
            if (Double.isNaN(valor) || Double.isInfinite(valor)) {
                throw new NumberFormatException();
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new DadoInvalidoException("Informe um número válido no campo " + campo + ".");
        }
    }

    private int lerInteiro(String texto, String campo) throws DadoInvalidoException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new DadoInvalidoException("Informe um número inteiro válido no campo " + campo + ".");
        }
    }
}