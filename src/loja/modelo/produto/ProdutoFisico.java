package loja.modelo.produto;

import java.util.Locale;
import loja.excecao.DadoInvalidoException;
import loja.excecao.EstoqueInsuficienteException;

// Produto com peso e estoque limitado. O peso entra no cálculo do frete.
public class ProdutoFisico extends Produto {
    private double pesoKg;
    private int estoque;

    public ProdutoFisico(int codigo, String nome, String descricao, String categoria, double preco,double pesoKg, int estoque) throws DadoInvalidoException {
        super(codigo, nome, descricao, categoria, preco);
        setPesoKg(pesoKg);
        setEstoque(estoque);
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(double pesoKg) throws DadoInvalidoException {
        if (Double.isNaN(pesoKg) || pesoKg < 0) {
            throw new DadoInvalidoException("O peso não pode ser negativo.");
        }
        this.pesoKg = pesoKg;
    }

    public int getEstoque() {
        return estoque;
    }

    // Usado no cadastro/alteração feitos pelo administrador.
    // Durante as vendas o estoque só muda por baixarEstoque e reporEstoque.
    public void setEstoque(int estoque) throws DadoInvalidoException {
        if (estoque < 0) {
            throw new DadoInvalidoException("O estoque não pode ser negativo.");
        }
        this.estoque = estoque;
    }

    @Override
    public boolean temEstoque(int quantidade) {
        return quantidade > 0 && quantidade <= estoque;
    }

    @Override
    public void baixarEstoque(int quantidade) throws EstoqueInsuficienteException {
        if (quantidade > estoque) {
            throw new EstoqueInsuficienteException("Estoque insuficiente para \"" + getNome()
                    + "\": pedido " + quantidade + ", disponível " + estoque + ".");
        }
        estoque -= quantidade;
    }

    @Override
    public void reporEstoque(int quantidade) {
        if (quantidade > 0) {
            estoque += quantidade;
        }
    }

    @Override
    public double getPesoParaFrete() {
        return pesoKg;
    }

    @Override
    public String getTipo() {
        return "FISICO";
    }

    @Override
    public String paraLinha() {
        return linhaBase() + SEPARADOR + String.format(Locale.US, "%.3f", pesoKg) + SEPARADOR + estoque;
    }
}
