
package loja.repositorio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import loja.modelo.produto.Produto;
import loja.modelo.produto.ProdutoFisico;
import loja.modelo.produto.ProdutoDigital;

import loja.excecao.DadoInvalidoException;
import loja.excecao.PersistenciaException;
import loja.excecao.ProdutoNaoEncontradoException;

public class RepositorioProdutos {

    private final List<Produto> produtos;
    private final Path arquivo;

    // Construtor
    public RepositorioProdutos() {
        this(Paths.get("dados", "produtos.txt"));
    }

    // Construtor que permite informar o caminho do arquivo
    public RepositorioProdutos(Path arquivo) {
        this.produtos = new ArrayList<>();
        this.arquivo = arquivo;
    }

    // Adiciona um produto
    public void adicionar(Produto produto) throws DadoInvalidoException {
        if (produto == null) {
            throw new DadoInvalidoException("Produto não informado.");
        }

        for (Produto p : produtos) {
            if (p.getCodigo() == produto.getCodigo()) {
                throw new DadoInvalidoException(
                    "Já existe um produto com esse código."
                );
            }
        }

        produtos.add(produto);
    }

    // Busca um produto pelo código
    public Produto buscarPorCodigo(int codigo)
            throws ProdutoNaoEncontradoException {

        for (Produto p : produtos) {
            if (p.getCodigo() == codigo) {
                return p;
            }
        }

        throw new ProdutoNaoEncontradoException(
            "Produto de código " + codigo + " não encontrado."
        );
    }

    // Remove um produto
    public void remover(int codigo)
            throws ProdutoNaoEncontradoException {

        Produto produto = buscarPorCodigo(codigo);
        produtos.remove(produto);
    }

    // Altera um produto existente
    public void alterar(Produto produto)
            throws DadoInvalidoException, ProdutoNaoEncontradoException {

        if (produto == null) {
            throw new DadoInvalidoException("Produto não informado.");
        }

        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getCodigo() == produto.getCodigo()) {
                produtos.set(i, produto);
                return;
            }
        }

        throw new ProdutoNaoEncontradoException(
            "Produto de código " + produto.getCodigo() + " não encontrado."
        );
    }

    // Retorna a lista de produtos sem permitir alterações externas
    public List<Produto> listar() {
        return Collections.unmodifiableList(produtos);
    }

    // Salva os produtos no arquivo TXT
    public void salvar() throws PersistenciaException {
        try {
            Path pasta = arquivo.getParent();

            if (pasta != null) {
                Files.createDirectories(pasta);
            }

            try (BufferedWriter escritor = Files.newBufferedWriter(arquivo)) {
                for (Produto produto : produtos) {
                    escritor.write(produto.paraLinha());
                    escritor.newLine();
                }
            }

        } catch (IOException e) {
            throw new PersistenciaException(
                "Erro ao salvar os produtos.", e
            );
        }
    }

    // Carrega os produtos do arquivo TXT
    public void carregar() throws PersistenciaException {

        if (!Files.exists(arquivo)) {
            return;
        }

        List<Produto> carregados = new ArrayList<>();

        try (BufferedReader leitor = Files.newBufferedReader(arquivo)) {

            String linha;

            while ((linha = leitor.readLine()) != null) {

                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] dados = linha.split(";", -1);

                if (dados.length < 7) {
                    throw new PersistenciaException(
                        "Linha incompleta no arquivo: " + linha
                    );
                }

                try {
                    String tipo = dados[0];

                    int codigo = Integer.parseInt(dados[1]);
                    String nome = dados[2];
                    String descricao = dados[3];
                    String categoria = dados[4];
                    double preco = Double.parseDouble(dados[5]);

                    Produto produto;

                    if (tipo.equals("FISICO") && dados.length == 8) {

                        double peso = Double.parseDouble(dados[6]);
                        int estoque = Integer.parseInt(dados[7]);

                        produto = new ProdutoFisico(
                            codigo,
                            nome,
                            descricao,
                            categoria,
                            preco,
                            peso,
                            estoque
                        );

                    } else if (tipo.equals("DIGITAL") && dados.length == 7) {

                        double tamanho = Double.parseDouble(dados[6]);

                        produto = new ProdutoDigital(
                            codigo,
                            nome,
                            descricao,
                            categoria,
                            preco,
                            tamanho
                        );

                    } else {
                        throw new IllegalArgumentException(
                            "Tipo ou quantidade de campos inválidos."
                        );
                    }

                    for (Produto existente : carregados) {
                        if (existente.getCodigo() == produto.getCodigo()) {
                            throw new IllegalArgumentException(
                                "Código de produto duplicado."
                            );
                        }
                    }

                    carregados.add(produto);

                } catch (IllegalArgumentException | DadoInvalidoException e) {

                    throw new PersistenciaException(
                        "Erro ao interpretar a linha: " + linha, e
                    );
                }
            }

            produtos.clear();
            produtos.addAll(carregados);

        } catch (IOException e) {

            throw new PersistenciaException(
                "Erro ao carregar os produtos.", e
            );
        }
    }
}
