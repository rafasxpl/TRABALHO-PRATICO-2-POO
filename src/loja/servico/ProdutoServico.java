package loja.servico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import loja.excecao.DadoInvalidoException;
import loja.excecao.ProdutoNaoEncontradoException;
import loja.modelo.produto.Produto;

// Regras do CRUD de produtos. As telas apenas chamam estes métodos.
// Por enquanto guarda os produtos numa lista própria em memória;
// quando o RepositorioProdutos (Membro 2) estiver pronto, ele substitui a lista.
public class ProdutoServico {
    private final List<Produto> produtos = new ArrayList<>();

    public void cadastrar(Produto produto) throws DadoInvalidoException {
        if (produto == null) {
            throw new DadoInvalidoException("Produto não informado.");
        }
        if (indiceDe(produto.getCodigo()) >= 0) {
            throw new DadoInvalidoException("Já existe um produto com o código " + produto.getCodigo() + ".");
        }
        produtos.add(produto);
    }

    // Substitui o produto que tem o mesmo código
    public void alterar(Produto produto) throws DadoInvalidoException, ProdutoNaoEncontradoException {
        if (produto == null) {
            throw new DadoInvalidoException("Produto não informado.");
        }
        int indice = indiceDe(produto.getCodigo());
        if (indice < 0) {
            throw new ProdutoNaoEncontradoException("Produto de código " + produto.getCodigo() + " não encontrado.");
        }
        produtos.set(indice, produto);
    }

    public void remover(int codigo) throws ProdutoNaoEncontradoException {
        int indice = indiceDe(codigo);
        if (indice < 0) {
            throw new ProdutoNaoEncontradoException("Produto de código " + codigo + " não encontrado.");
        }
        produtos.remove(indice);
    }

    public Produto buscarPorCodigo(int codigo) throws ProdutoNaoEncontradoException {
        int indice = indiceDe(codigo);
        if (indice < 0) {
            throw new ProdutoNaoEncontradoException("Produto de código " + codigo + " não encontrado.");
        }
        return produtos.get(indice);
    }

    // Lista somente leitura: quem chama não consegue alterar a coleção interna
    public List<Produto> listar() {
        return Collections.unmodifiableList(produtos);
    }

    public List<Produto> listarPorCategoria(String categoria) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getCategoria().equalsIgnoreCase(categoria)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // Próximo código livre (maior código existente + 1)
    public int proximoCodigo() {
        int maior = 0;
        for (Produto p : produtos) {
            maior = Math.max(maior, p.getCodigo());
        }
        return maior + 1;
    }

    private int indiceDe(int codigo) {
        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getCodigo() == codigo) {
                return i;
            }
        }
        return -1;
    }
}
