package loja.modelo.produto;

import loja.excecao.DadoInvalidoException;
import loja.excecao.EstoqueInsuficienteException;

// Superclasse abstrata dos produtos da loja.
// Atributos comuns ficam aqui; o que muda entre físico e digital
// (estoque, peso, tipo, gravação em linha) é declarado como método abstrato.
public abstract class Produto {
    // Separador usado ao gravar o produto em arquivo .txt
    public static final String SEPARADOR = ";";

    private int codigo;
    private String nome;
    private String descricao;
    private String categoria;
    private double preco;

    protected Produto(int codigo, String nome, String descricao, String categoria, double preco)
            throws DadoInvalidoException {
        setCodigo(codigo);
        setNome(nome);
        setDescricao(descricao);
        setCategoria(categoria);
        setPreco(preco);
    }

    // ----- Métodos polimórficos (cada subclasse implementa do seu jeito) -----

    // Diz se existe quantidade suficiente para atender o pedido
    public abstract boolean temEstoque(int quantidade);

    // Retira a quantidade do estoque (produto físico lança exceção se faltar)
    public abstract void baixarEstoque(int quantidade) throws EstoqueInsuficienteException;

    // Devolve a quantidade ao estoque (ex.: cancelamento de pedido)
    public abstract void reporEstoque(int quantidade);

    // Peso em kg usado no cálculo do frete (0 para produto digital)
    public abstract double getPesoParaFrete();

    // "FISICO" ou "DIGITAL"
    public abstract String getTipo();

    // Linha para gravar no arquivo: TIPO;codigo;nome;descricao;categoria;preco;...atributos do tipo
    public abstract String paraLinha();

    // ----- Getters e setters com validação -----

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) throws DadoInvalidoException {
        if (codigo <= 0) {
            throw new DadoInvalidoException("O código do produto deve ser maior que zero.");
        }
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws DadoInvalidoException {
        this.nome = validarTexto(nome, "nome");
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) throws DadoInvalidoException {
        this.descricao = validarTexto(descricao, "descrição");
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) throws DadoInvalidoException {
        this.categoria = validarTexto(categoria, "categoria");
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) throws DadoInvalidoException {
        if (Double.isNaN(preco) || preco < 0) {
            throw new DadoInvalidoException("O preço não pode ser negativo.");
        }
        this.preco = preco;
    }

    // Texto não pode ser vazio nem conter o separador do arquivo
    private String validarTexto(String texto, String campo) throws DadoInvalidoException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new DadoInvalidoException("O campo " + campo + " não pode ficar vazio.");
        }
        if (texto.contains(SEPARADOR)) {
            throw new DadoInvalidoException("O campo " + campo + " não pode conter o caractere '" + SEPARADOR + "'.");
        }
        return texto.trim();
    }

    // Parte da linha comum a todos os tipos (usada pelas subclasses em paraLinha)
    protected String linhaBase() {
        return String.join(SEPARADOR, getTipo(), String.valueOf(codigo), nome, descricao, categoria,
                String.format(java.util.Locale.US, "%.2f", preco));
    }

    @Override
    public String toString() {
        return codigo + " - " + nome + " (" + getTipo() + ")";
    }
}
