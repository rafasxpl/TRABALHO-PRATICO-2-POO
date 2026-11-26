package loja.repositorio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import loja.excecao.PersistenciaException;

/**
 * Superclasse abstrata e GENÉRICA de todos os repositórios. Guarda os itens
 * em uma lista em memória e sabe carregar/salvar essa lista em um arquivo de
 * texto, delegando às subclasses apenas a parte que muda de um tipo para o
 * outro: como transformar uma linha de texto em objeto (deLinha) e um
 * objeto em linha de texto (paraLinha) - POLIMORFISMO.
 *
 * NOTA DE INTEGRAÇÃO: pacote de responsabilidade do MEMBRO 2. Implementação
 * genérica para o Membro 4 compilar e testar as telas do cliente.
 *
 * @param <T> o tipo de objeto guardado (Produto, Usuario ou Pedido)
 */
public abstract class Repositorio<T> {

    protected List<T> itens = new ArrayList<>();
    private final String caminhoArquivo;

    protected Repositorio(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    /** Converte uma linha do arquivo de texto no objeto correspondente. */
    protected abstract T deLinha(String linha) throws PersistenciaException;

    /** Converte um objeto em uma linha de texto para gravar no arquivo. */
    protected abstract String paraLinha(T item);

    /** Lê o arquivo de texto e recarrega a lista em memória (lista fica vazia se o arquivo não existir). */
    public void carregar() throws PersistenciaException {
        itens.clear();
        List<String> linhas = ArquivoTexto.lerLinhas(caminhoArquivo);
        for (String linha : linhas) {
            if (linha == null || linha.isBlank()) {
                continue;
            }
            itens.add(deLinha(linha));
        }
    }

    /** Grava a lista em memória no arquivo de texto, sobrescrevendo o conteúdo anterior. */
    public void salvar() throws PersistenciaException {
        List<String> linhas = new ArrayList<>();
        for (T item : itens) {
            linhas.add(paraLinha(item));
        }
        ArquivoTexto.gravarLinhas(caminhoArquivo, linhas);
    }

    public void adicionar(T item) {
        itens.add(item);
    }

    public void remover(T item) {
        itens.remove(item);
    }

    /** Lista não modificável dos itens em memória (ENCAPSULAMENTO). */
    public List<T> listar() {
        return Collections.unmodifiableList(itens);
    }
}
