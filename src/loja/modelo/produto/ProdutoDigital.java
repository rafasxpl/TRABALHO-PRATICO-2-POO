package loja.modelo.produto;

import java.util.Locale;
import loja.excecao.DadoInvalidoException;

// Produto baixado pela internet: estoque ilimitado e sem frete.
public class ProdutoDigital extends Produto {
    private double tamanhoArquivoMb;

    public ProdutoDigital(int codigo, String nome, String descricao, String categoria, double preco, double tamanhoArquivoMb) throws DadoInvalidoException {
        super(codigo, nome, descricao, categoria, preco);
        setTamanhoArquivoMb(tamanhoArquivoMb);
    }

    public double getTamanhoArquivoMb() {
        return tamanhoArquivoMb;
    }

    public void setTamanhoArquivoMb(double tamanhoArquivoMb) throws DadoInvalidoException {
        if (Double.isNaN(tamanhoArquivoMb) || tamanhoArquivoMb < 0) {
            throw new DadoInvalidoException("O tamanho do arquivo não pode ser negativo.");
        }
        this.tamanhoArquivoMb = tamanhoArquivoMb;
    }

    // Estoque ilimitado: basta a quantidade ser positiva
    @Override
    public boolean temEstoque(int quantidade) {
        return quantidade > 0;
    }

    // Nada a fazer: não existe estoque a controlar
    @Override
    public void baixarEstoque(int quantidade) {
    }

    @Override
    public void reporEstoque(int quantidade) {
    }

    @Override
    public double getPesoParaFrete() {
        return 0;
    }

    @Override
    public String getTipo() {
        return "DIGITAL";
    }

    @Override
    public String paraLinha() {
        return linhaBase() + SEPARADOR + String.format(Locale.US, "%.2f", tamanhoArquivoMb);
    }
}
